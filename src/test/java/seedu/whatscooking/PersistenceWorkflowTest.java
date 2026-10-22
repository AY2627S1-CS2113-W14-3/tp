package seedu.whatscooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Runs the real CLI across two separate processes sharing one working
 * directory, to check that recipes saved in the first session are loaded
 * back in the second: the actual guarantee the save/load feature exists for.
 */
class PersistenceWorkflowTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void secondSession_afterFirstSessionAdds_seesTheRecipe() throws Exception {
        runSession("add Egg Taco, snack i/3 eggs st/Scramble egg\nbye\n", "session1-out.txt");

        String secondSessionOutput = runSession("view\nbye\n", "session2-out.txt");

        // input:    a recipe added in session 1, viewed in a fresh session 2
        // actual:   session 2's output
        // expected: the recipe added in session 1 is listed
        assertTrue(secondSessionOutput.contains("Egg Taco (snack, serves 1)"),
                "Expected the recipe saved in session 1 to appear in session 2:\n" + secondSessionOutput);
    }

    /** Runs one WhatsCooking process against {@code commands} and returns its console output. */
    private String runSession(String commands, String outputFileName) throws IOException,
            InterruptedException, URISyntaxException {
        Path input = temporaryDirectory.resolve(outputFileName + "-input.txt");
        Path output = temporaryDirectory.resolve(outputFileName);
        Path errors = temporaryDirectory.resolve(outputFileName + "-errors.txt");
        Files.writeString(input, commands);

        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classPath = Path.of(WhatsCooking.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI()).toString();

        Process process = new ProcessBuilder(javaExecutable, "-ea", "-cp", classPath, WhatsCooking.class.getName())
                .directory(temporaryDirectory.toFile())
                .redirectInput(input.toFile())
                .redirectOutput(output.toFile())
                .redirectError(errors.toFile())
                .start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "The CLI did not exit after bye");
            assertEquals(0, process.exitValue(), Files.readString(errors));
        } finally {
            process.destroyForcibly();
        }
        return Files.readString(output);
    }
}
