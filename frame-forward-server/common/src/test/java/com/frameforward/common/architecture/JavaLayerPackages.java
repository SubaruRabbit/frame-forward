package com.frameforward.common.architecture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;

/** 仅供测试使用；检查源码结构，不替代依赖方向检查与职责评审。 */
public final class JavaLayerPackages {
    private static final Map<String, String> ROLES = Map.ofEntries(Map.entry("Controller", "controller"),
            Map.entry("ExceptionHandler", "controller"), Map.entry("Service", "service"),
            Map.entry("Business", "business"), Map.entry("Policy", "business"), Map.entry("Rules", "business"),
            Map.entry("Manager", "manager"), Map.entry("Mapper", "mapper"), Map.entry("Mappers", "mapper"),
            Map.entry("Entity", "model.entity"), Map.entry("Dto", "model.dto"), Map.entry("DTO", "model.dto"),
            Map.entry("Configuration", "config"));

    private JavaLayerPackages() {
    }

    public enum Kind {
        MISSING_PACKAGE, OUTSIDE_BASE_PACKAGE, DIRECTORY_PACKAGE_MISMATCH, ROOT_PACKAGE, ROLE_PACKAGE_MISMATCH
    }

    public record Violation(Path file, Kind kind, String actualPackage, String expectedPackage) {
    }

    public static List<Violation> inspect(Path sourceRoot, String basePackage) throws IOException {
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new IOException("目录检查需要 JDK 编译器");
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        var violations = new ArrayList<Violation>();
        try (var paths = Files.walk(sourceRoot);
                var files = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            var sources = paths.filter(path -> path.toString().endsWith(".java")).sorted().toList();
            if (sources.isEmpty())
                throw new IOException("没有找到 Java 源文件：" + sourceRoot);
            var task = (JavacTask) compiler.getTask(null, files, diagnostics, List.of("-proc:none"), null,
                    files.getJavaFileObjectsFromPaths(sources));
            for (var unit : task.parse())
                inspectUnit(sourceRoot.toAbsolutePath().normalize(), basePackage, unit, violations);
            if (diagnostics.getDiagnostics().stream().anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR))
                throw new IOException("Java 源文件解析失败：" + diagnostics.getDiagnostics());
        }
        return List.copyOf(violations);
    }

    private static void inspectUnit(Path root, String base, CompilationUnitTree unit, List<Violation> result) {
        Path relative = root.relativize(Path.of(unit.getSourceFile().toUri()).toAbsolutePath().normalize());
        if (unit.getPackageName() == null) {
            result.add(new Violation(relative, Kind.MISSING_PACKAGE, "", base));
            return;
        }
        String actual = unit.getPackageName().toString();
        String directory = relative.getParent() == null ? "" : relative.getParent().toString().replace('/', '.');
        if (!actual.equals(directory))
            result.add(new Violation(relative, Kind.DIRECTORY_PACKAGE_MISMATCH, actual, directory));
        if (!actual.equals(base) && !actual.startsWith(base + ".")) {
            result.add(new Violation(relative, Kind.OUTSIDE_BASE_PACKAGE, actual, base));
            return;
        }
        if (actual.equals(base))
            result.add(new Violation(relative, Kind.ROOT_PACKAGE, actual, base + ".<职责子包>"));
        for (var declaration : unit.getTypeDecls()) {
            if (declaration instanceof ClassTree type)
                inspectRole(relative, base, actual, type.getSimpleName().toString(), result);
        }
    }

    private static void inspectRole(Path file, String base, String actual, String name, List<Violation> result) {
        ROLES.entrySet().stream().filter(role -> name.endsWith(role.getKey())).findFirst().ifPresent(role -> {
            String expected = base + "." + role.getValue();
            if (!actual.equals(expected) && !actual.startsWith(expected + "."))
                result.add(new Violation(file, Kind.ROLE_PACKAGE_MISMATCH, actual, expected));
        });
    }
}
