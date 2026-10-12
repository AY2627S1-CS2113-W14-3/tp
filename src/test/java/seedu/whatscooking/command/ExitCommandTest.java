package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Tests for {@link ExitCommand}: it must end the main loop and say goodbye,
 * without altering the recipe list on the way out.
 */
class ExitCommandTest {
    @Test
    public void isExit_always_returnsTrue() {
        // input:    a new ExitCommand
        // actual:   command.isExit()
        // expected: true, so the main loop stops
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void execute_nonEmptyList_showsGoodbyeAndLeavesRecipesUnchanged() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Pancakes", "breakfast"));
        RecordingUi ui = new RecordingUi();

        new ExitCommand().execute(recipes, ui);

        // input:    exit run against a list holding 1 recipe
        // expected: goodbye shown, list untouched
        assertTrue(ui.hasShownGoodbye);
        assertEquals(1, recipes.size());
        assertEquals("Pancakes", recipes.get(0).getTitle());
    }

    /**
     * Records that the goodbye message was shown, without printing to the
     * console during the test run.
     */
    private static class RecordingUi extends Ui {
        private boolean hasShownGoodbye;

        @Override
        public void showGoodbye() {
            hasShownGoodbye = true;
        }
    }
}
