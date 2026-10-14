package seedu.whatscooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Runs the real CLI with text fixtures to check parsing, output and recovery after invalid input.
 * A separate JVM keeps console streams and application logging isolated from other tests.
 */
class ModifyWorkflowTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void main_modifySession_matchesExpectedOutput() throws Exception {
        Path input = Path.of("text-ui-test", "input.txt").toAbsolutePath();
        Path expected = Path.of("text-ui-test", "EXPECTED.txt").toAbsolutePath();
        Path actual = temporaryDirectory.resolve("ACTUAL.txt");
        Path errors = temporaryDirectory.resolve("errors.txt");
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classPath = Path.of(WhatsCooking.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI()).toString();

        Process process = new ProcessBuilder(javaExecutable, "-ea", "-cp", classPath, WhatsCooking.class.getName())
                .directory(temporaryDirectory.toFile())
                .redirectInput(input.toFile())
                .redirectOutput(actual.toFile())
                .redirectError(errors.toFile())
                .start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "The CLI did not exit after bye");
            assertEquals(0, process.exitValue(), Files.readString(errors));
            assertEquals("", Files.readString(errors), "Diagnostics must not appear on the user console");
            assertEquals(Files.readString(expected).replace("\r\n", "\n"),
                    Files.readString(actual).replace("\r\n", "\n"));
        } finally {
            process.destroyForcibly();
        }
    }
}
