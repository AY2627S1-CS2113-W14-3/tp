package seedu.whatscooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks allergen warnings through the real CLI, including failures, serving sizes and saved recipes. */
class AllergenWorkflowTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void main_addThenRetrieveByIndexAndName_showsTheSameWarning() throws Exception {
        String output = runSession("""
                add Omelette, breakfast s/2 i/2 eggs, 100 g cheese st/Cook n/Serve hot
                retrieve 1
                retrieve oMeLeTtE
                bye
                """, "add-and-retrieve");

        String warning = "Possible allergens: milk, eggs. Check ingredient labels.";
        assertEquals(3, countWarnings(output), output);
        assertEquals(3, output.lines().filter(warning::equals).count(), output);
        assertTrue(output.contains("Omelette (breakfast, serves 2)"), output);
        assertTrue(output.contains("  1. 2 eggs"), output);
        assertTrue(output.contains("Note: Serve hot"), output);
    }

    @Test
    void main_invalidLaterIngredient_doesNotAddRecipeOrPrintWarning() throws Exception {
        String output = runSession("add Broken i/1 eggs, 0 g cheese\nview\nbye\n", "invalid");

        assertTrue(output.contains("Error: Invalid ingredient"), output);
        assertTrue(output.contains("No recipes found."), output);
        assertEquals(0, countWarnings(output), output);
    }

    @Test
    void main_duplicateRecipe_doesNotWarnAboutTheRejectedIngredients() throws Exception {
        String output = runSession("""
                add Peanut snack i/1 peanuts
                add peanut snack i/1 milk
                retrieve 1
                bye
                """, "duplicate");

        assertTrue(output.contains("already exists"), output);
        assertEquals(2, countWarnings(output), output);
        assertFalse(output.contains("Possible allergens: milk"), output);
    }

    @Test
    void main_noIngredientMatches_doesNotScanTitleStepsOrNote() throws Exception {
        String output = runSession("""
                add Egg milk, main i/1 eggplant, 1 nutmeg st/Add peanuts n/Contains sesame
                retrieve 1
                add Empty
                retrieve Empty
                bye
                """, "no-matches");

        assertTrue(output.contains("Egg milk (main, serves 1)"), output);
        assertTrue(output.contains("Empty (, serves 1)"), output);
        assertEquals(0, countWarnings(output), output);
    }

    @Test
    void main_restartAndScale_keepsWarningsAndScalesFromAddedServingSize() throws Exception {
        String firstOutput = runSession("""
                add Peanut noodles, main s/2 i/2 tbsp peanut oil, 1 tofu st/Mix
                bye
                """, "before-restart");
        assertEquals(1, countWarnings(firstOutput), firstOutput);

        String secondOutput = runSession("""
                modify Peanut noodles s/4
                retrieve 1
                retrieve peanut noodles
                bye
                """, "after-restart");

        String warning = "Possible allergens: peanuts, soybeans. Check ingredient labels.";
        assertEquals(2, countWarnings(secondOutput), secondOutput);
        assertEquals(2, secondOutput.lines().filter(warning::equals).count(), secondOutput);
        assertTrue(secondOutput.contains("Peanut noodles (main, serves 4)"), secondOutput);
        assertTrue(secondOutput.contains("  1. 4 tbsp peanut oil"), secondOutput);
        assertTrue(secondOutput.contains("  2. 2 tofu"), secondOutput);
    }

    /** Counts visible warnings rather than incidental ingredient names in the rest of the output. */
    private long countWarnings(String output) {
        return output.lines().filter(line -> line.startsWith("Possible allergens:")).count();
    }

    /** Runs an isolated Java 25 process; sessions in one test share only their temporary save directory. */
    private String runSession(String commands, String sessionName) throws IOException,
            InterruptedException, URISyntaxException {
        Path input = temporaryDirectory.resolve(sessionName + "-input.txt");
        Path output = temporaryDirectory.resolve(sessionName + "-output.txt");
        Path errors = temporaryDirectory.resolve(sessionName + "-errors.txt");
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
            assertEquals("", Files.readString(errors), "Logs and exceptions must not leak to the console");
            return Files.readString(output);
        } finally {
            process.destroyForcibly();
        }
    }
}
