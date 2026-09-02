import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** VerifyQualityReports 的最小回归测试入口。 */
public final class VerifyQualityReportsTest {
    private VerifyQualityReportsTest() {
    }

    public static void main(String[] args) throws Exception {
        parsesChangedProductionLines();
        rejectsCoverageBelowThreshold();
        readsJacocoXml();
    }

    private static void parsesChangedProductionLines() {
        Map<String, Set<Integer>> changed = VerifyQualityReports.changedLinesFromDiff(List.of(
                "+++ b/auth/src/main/java/com/frameforward/auth/AuthService.java", "@@ -10 +10,2 @@"));
        assertEquals(Set.of(10, 11), changed.get("com/frameforward/auth/AuthService.java"));
    }

    private static void rejectsCoverageBelowThreshold() {
        VerifyQualityReports.SourceCoverage source = new VerifyQualityReports.SourceCoverage();
        source.lines.put(8, new VerifyQualityReports.LineCoverage(1, 0, 0, 0));
        assertThrows(() -> VerifyQualityReports.verifyCoverage(Map.of("com/frameforward/auth/AuthService.java", Set.of(8)),
                Map.of("com/frameforward/auth/AuthService.java", source)));
    }

    private static void readsJacocoXml() throws Exception {
        Path root = Files.createTempDirectory("quality-report-test");
        Path report = root.resolve("auth/target/site/jacoco/jacoco.xml");
        Files.createDirectories(report.getParent());
        Files.writeString(report, "<!DOCTYPE report PUBLIC \"-//JACOCO//DTD Report 1.1//EN\" \"report.dtd\">"
                + "<report><package name=\"com/frameforward/auth\"><sourcefile name=\"AuthService.java\">"
                + "<line nr=\"8\" mi=\"0\" ci=\"1\" mb=\"0\" cb=\"1\"/>"
                + "</sourcefile></package></report>");
        assertEquals(true, VerifyQualityReports.readJacocoReports(root).get("com/frameforward/auth/AuthService.java")
                .lines.get(8).covered());
    }

    private static void assertThrows(ThrowingRunnable action) {
        try {
            action.run();
        } catch (IllegalStateException expected) {
            return;
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
        throw new AssertionError("预期质量门禁失败。");
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("期望 " + expected + "，实际 " + actual);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
