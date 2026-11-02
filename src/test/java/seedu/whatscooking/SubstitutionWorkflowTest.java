package seedu.whatscooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks advice in actual CLI processes and confirms it leaves persisted recipe contents unchanged. */
class SubstitutionWorkflowTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void main_restartedRecipe_adviceAndScalingPreserveOriginalIngredients() throws Exception {
        runSession("add Cake, dessert s/2 i/1 cup milk, 2 eggs, 1 tbsp peanut butter st/Bake\nbye\n", "setup");
        Path saveFile = temporaryDirectory.resolve("data/whatscooking.txt");
        String beforeAdvice = Files.readString(saveFile);

        String output = runSession("substitute cAkE\nretrieve Cake\nbye\n", "advice");

        assertTrue(output.contains("Substitution ideas for Cake:"), output);
        assertTrue(output.contains("  1. milk -> rice drink"), output);
        assertTrue(output.contains("  2. eggs -> mashed banana"), output);
        assertTrue(output.contains("  3. peanut butter -> sunflower seed butter"), output);
        assertTrue(output.contains("Not for omelettes or meringues"), output);
        assertTrue(output.contains("alternatives may contain allergens"), output);
        assertTrue(output.contains("Your recipe and quantities have not been changed."), output);
        assertTrue(output.contains("  1. 1 cup milk"), output);
        assertEquals(beforeAdvice, Files.readString(saveFile));

        String scaled = runSession("modify Cake s/4\nsubstitute Cake\nretrieve Cake\nbye\n", "scaled");
        assertTrue(scaled.contains("  1. milk -> rice drink"), scaled);
        assertTrue(scaled.contains("  1. 2 cup milk"), scaled);
        assertTrue(scaled.contains("  2. 4 eggs"), scaled);
    }

    @Test
    void main_errorsAndUnsupportedNames_keepAcceptingCommandsWithoutSafetyClaims() throws Exception {
        String output = runSession("""
                substitute
                substitute Missing
                add Drink i/1 cup coconut milk
                substitute Drink
                add Empty
                substitute Empty
                bye
                """, "errors");

        assertTrue(output.contains("Error: Usage: substitute RECIPE_NAME"), output);
        assertTrue(output.contains("Error: Recipe not found: Missing"), output);
        assertEquals(2, output.lines().filter(line ->
                line.equals("No built-in suggestions for the recorded ingredient names.")).count(), output);
        assertTrue(output.contains("Bye! Happy cooking."), output);
    }

    /** Each session uses Java 25 and shares only this test's isolated save directory. */
    private String runSession(String commands, String sessionName) throws Exception {
        Path input = temporaryDirectory.resolve(sessionName + "-input.txt");
        Path output = temporaryDirectory.resolve(sessionName + "-output.txt");
        Path errors = temporaryDirectory.resolve(sessionName + "-errors.txt");
        Files.writeString(input, commands);
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classPath = Path.of(WhatsCooking.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI()).toString();
        Process process = new ProcessBuilder(javaExecutable, "-ea", "-cp", classPath, WhatsCooking.class.getName())
                .directory(temporaryDirectory.toFile())
                .redirectInput(input.toFile()).redirectOutput(output.toFile()).redirectError(errors.toFile()).start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "The CLI did not exit after bye");
            assertEquals(0, process.exitValue(), Files.readString(errors));
            assertEquals("", Files.readString(errors));
            return Files.readString(output);
        } finally {
            process.destroyForcibly();
        }
    }
}
