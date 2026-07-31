package shiroroku.theaurorian;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs {@code scripts/validate_resources.py} as the primary content/integrity gate.
 * Keeps the heavy datapack assertions out of fragile in-JVM Minecraft bootstraps.
 */
class ResourceIntegrityTest {

    @Test
    void resourceIntegrityScriptPasses() throws Exception {
        Path root = findProjectRoot();
        Path script = root.resolve("scripts/validate_resources.py");
        assertTrue(Files.isRegularFile(script), "missing " + script);

        List<String> command = new ArrayList<>();
        command.add(resolvePython());
        command.add(script.toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(root.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }
        boolean finished = process.waitFor(120, TimeUnit.SECONDS);
        assertTrue(finished, "validate_resources.py timed out");
        int code = process.exitValue();
        if (code != 0) {
            fail("validate_resources.py failed (exit " + code + "):\n" + output);
        }
    }

    private static String resolvePython() {
        for (String candidate : new String[]{"python3", "python"}) {
            try {
                Process p = new ProcessBuilder(candidate, "--version").redirectErrorStream(true).start();
                if (p.waitFor(10, TimeUnit.SECONDS) && p.exitValue() == 0) {
                    return candidate;
                }
            } catch (IOException | InterruptedException ignored) {
                // try next
            }
        }
        fail("python3/python not found on PATH");
        return "python3";
    }

    private static Path findProjectRoot() {
        Path dir = Path.of("").toAbsolutePath();
        for (int i = 0; i < 6; i++) {
            if (Files.isRegularFile(dir.resolve("build.gradle")) && Files.isDirectory(dir.resolve("scripts"))) {
                return dir;
            }
            Path parent = dir.getParent();
            if (parent == null) {
                break;
            }
            dir = parent;
        }
        // Gradle test cwd is usually project root
        return Path.of("").toAbsolutePath();
    }
}
