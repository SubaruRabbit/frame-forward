import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** 验证相对 Git 基线的变更代码覆盖率与 PMD CPD 重复率。 */
public final class VerifyQualityReports {
    private static final double MIN_LINE_COVERAGE = 0.90;
    private static final double MIN_BRANCH_COVERAGE = 0.80;
    private static final double MAX_DUPLICATION = 0.02;
    private static final Pattern HUNK_PATTERN = Pattern.compile("^@@ -\\d+(?:,\\d+)? \\+(\\d+)(?:,(\\d+))? @@");
    private static final Pattern SOURCE_PATH_PATTERN = Pattern.compile("(?:^|.*/)src/main/java/(.+\\.java)$");

    private VerifyQualityReports() {
    }

    public static void main(String[] args) throws Exception {
        String baseRef = System.getenv("QUALITY_BASE_REF");
        if (baseRef == null || baseRef.isBlank()) {
            throw new IllegalStateException("QUALITY_BASE_REF 必须显式指定 Git 基线提交或引用。");
        }
        Path root = Path.of("").toAbsolutePath().normalize();
        verifyBaseRef(root, baseRef);
        Map<String, Set<Integer>> changedLines = changedSourceLines(root, baseRef);
        Map<String, SourceCoverage> coverageBySource = readJacocoReports(root);
        verifyCoverage(changedLines, coverageBySource);
        verifyDuplication(root);
    }

    static void verifyCoverage(Map<String, Set<Integer>> changedLines, Map<String, SourceCoverage> coverageBySource) {
        int executableLines = 0;
        int coveredLines = 0;
        int branches = 0;
        int coveredBranches = 0;
        for (Map.Entry<String, Set<Integer>> changedSource : changedLines.entrySet()) {
            SourceCoverage sourceCoverage = coverageBySource.get(changedSource.getKey());
            if (sourceCoverage == null) {
                throw new IllegalStateException("变更源码未出现在 JaCoCo XML 中：" + changedSource.getKey());
            }
            for (int lineNumber : changedSource.getValue()) {
                LineCoverage lineCoverage = sourceCoverage.lines.get(lineNumber);
                if (lineCoverage == null || !lineCoverage.executable()) {
                    continue;
                }
                executableLines++;
                if (lineCoverage.covered()) {
                    coveredLines++;
                }
                branches += lineCoverage.missedBranches + lineCoverage.coveredBranches;
                coveredBranches += lineCoverage.coveredBranches;
            }
        }
        if (executableLines == 0) {
            System.out.println("质量门禁：没有变更的可执行 Java 行，跳过变更代码覆盖率阈值检查。");
            return;
        }
        double lineCoverage = (double) coveredLines / executableLines;
        double branchCoverage = branches == 0 ? 1.0 : (double) coveredBranches / branches;
        System.out.printf("质量门禁：变更代码行覆盖率 %.2f%%，分支覆盖率 %.2f%%。%n", lineCoverage * 100,
                branchCoverage * 100);
        if (lineCoverage < MIN_LINE_COVERAGE || branchCoverage < MIN_BRANCH_COVERAGE) {
            throw new IllegalStateException("变更代码覆盖率未达到核心系统阈值：行 >= 90%，分支 >= 80%。");
        }
    }

    static void verifyDuplication(Path root) throws Exception {
        List<Path> reports = findReports(root, "cpd.xml");
        if (reports.isEmpty()) {
            throw new IllegalStateException("未找到 PMD CPD XML 报告。");
        }
        long duplicatedLines = 0;
        for (Path report : reports) {
            Document document = readXml(report);
            NodeList duplications = document.getElementsByTagName("duplication");
            for (int index = 0; index < duplications.getLength(); index++) {
                Element duplication = (Element) duplications.item(index);
                long lines = Long.parseLong(duplication.getAttribute("lines"));
                long occurrences = duplication.getElementsByTagName("file").getLength();
                duplicatedLines += lines * occurrences;
            }
        }
        long sourceLines = countProductionSourceLines(root);
        if (sourceLines == 0) {
            throw new IllegalStateException("未找到用于计算 PMD CPD 重复率的生产 Java 源码。");
        }
        double duplication = (double) duplicatedLines / sourceLines;
        System.out.printf("质量门禁：PMD CPD 重复率 %.2f%%。%n", duplication * 100);
        if (duplication > MAX_DUPLICATION) {
            throw new IllegalStateException("PMD CPD 重复率超过核心系统阈值 2%。");
        }
    }

    static Map<String, Set<Integer>> changedSourceLines(Path root, String baseRef) throws Exception {
        List<String> diff = run(root, "git", "diff", "--no-ext-diff", "--unified=0", baseRef, "--");
        Map<String, Set<Integer>> result = changedLinesFromDiff(diff);
        for (String untracked : run(root, "git", "ls-files", "--others", "--exclude-standard")) {
            String sourcePath = sourcePath(untracked);
            if (sourcePath == null) {
                continue;
            }
            int lineCount = Files.readAllLines(root.resolve(untracked)).size();
            for (int line = 1; line <= lineCount; line++) {
                result.computeIfAbsent(sourcePath, ignored -> new HashSet<>()).add(line);
            }
        }
        return result;
    }

    static Map<String, Set<Integer>> changedLinesFromDiff(List<String> diff) {
        Map<String, Set<Integer>> result = new HashMap<>();
        String sourcePath = null;
        for (String line : diff) {
            if (line.startsWith("+++ b/")) {
                sourcePath = sourcePath(line.substring("+++ b/".length()));
                continue;
            }
            Matcher hunk = HUNK_PATTERN.matcher(line);
            if (sourcePath == null || !hunk.matches()) {
                continue;
            }
            int start = Integer.parseInt(hunk.group(1));
            int count = hunk.group(2) == null ? 1 : Integer.parseInt(hunk.group(2));
            for (int lineNumber = start; lineNumber < start + count; lineNumber++) {
                result.computeIfAbsent(sourcePath, ignored -> new HashSet<>()).add(lineNumber);
            }
        }
        return result;
    }

    static Map<String, SourceCoverage> readJacocoReports(Path root) throws Exception {
        List<Path> reports = findReports(root, "jacoco.xml");
        if (reports.isEmpty()) {
            throw new IllegalStateException("未找到 JaCoCo XML 报告。");
        }
        Map<String, SourceCoverage> result = new HashMap<>();
        for (Path report : reports) {
            Document document = readXml(report);
            NodeList packages = document.getElementsByTagName("package");
            for (int packageIndex = 0; packageIndex < packages.getLength(); packageIndex++) {
                Element packageElement = (Element) packages.item(packageIndex);
                String packageName = packageElement.getAttribute("name");
                NodeList sourceFiles = packageElement.getElementsByTagName("sourcefile");
                for (int sourceIndex = 0; sourceIndex < sourceFiles.getLength(); sourceIndex++) {
                    Element sourceFile = (Element) sourceFiles.item(sourceIndex);
                    SourceCoverage sourceCoverage = new SourceCoverage();
                    NodeList lines = sourceFile.getElementsByTagName("line");
                    for (int lineIndex = 0; lineIndex < lines.getLength(); lineIndex++) {
                        Element line = (Element) lines.item(lineIndex);
                        sourceCoverage.lines.put(Integer.parseInt(line.getAttribute("nr")), new LineCoverage(
                                Integer.parseInt(line.getAttribute("mi")), Integer.parseInt(line.getAttribute("ci")),
                                Integer.parseInt(line.getAttribute("mb")), Integer.parseInt(line.getAttribute("cb"))));
                    }
                    result.put(packageName + "/" + sourceFile.getAttribute("name"), sourceCoverage);
                }
            }
        }
        return result;
    }

    static String sourcePath(String path) {
        Matcher matcher = SOURCE_PATH_PATTERN.matcher(path);
        return matcher.matches() && !path.contains("/generated-sources/") ? matcher.group(1) : null;
    }

    private static void verifyBaseRef(Path root, String baseRef) throws Exception {
        run(root, "git", "rev-parse", "--verify", baseRef + "^{commit}");
    }

    private static List<Path> findReports(Path root, String filename) throws IOException {
        try (var paths = Files.walk(root)) {
            return paths.filter(path -> path.getFileName().toString().equals(filename)).toList();
        }
    }

    private static long countProductionSourceLines(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            return paths.filter(path -> sourcePath(root.relativize(path).toString().replace('\\', '/')) != null)
                    .mapToLong(VerifyQualityReports::lineCount).sum();
        }
    }

    private static long lineCount(Path path) {
        try (var lines = Files.lines(path)) {
            return lines.count();
        } catch (IOException exception) {
            throw new IllegalStateException("无法读取源码：" + path, exception);
        }
    }

    private static Document readXml(Path report) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory.newDocumentBuilder().parse(report.toFile());
    }

    private static List<String> run(Path root, String... command) throws Exception {
        Process process = new ProcessBuilder(command).directory(root.toFile()).redirectErrorStream(true).start();
        List<String> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }
        if (process.waitFor() != 0) {
            throw new IllegalStateException("命令执行失败：" + String.join(" ", command) + System.lineSeparator()
                    + String.join(System.lineSeparator(), output));
        }
        return output;
    }

    static final class SourceCoverage {
        final Map<Integer, LineCoverage> lines = new HashMap<>();
    }

    record LineCoverage(int missedInstructions, int coveredInstructions, int missedBranches, int coveredBranches) {
        boolean executable() {
            return missedInstructions + coveredInstructions > 0;
        }

        boolean covered() {
            return coveredInstructions > 0;
        }
    }
}
