package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Verifies that adding recipes uses structured ingredients and reports validation errors safely.
 */
class AddCommandIngredientTest {
    @Test
    void execute_mixedIngredientFormats_storesStructuredIngredients() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        RecordingUi ui = new RecordingUi();

        new AddCommand("omelette, breakfast i/3 eggs, 200g cheese, 1.5 tbsp olive oil st/Cook")
                .execute(recipes, ui);

        assertEquals(1, recipes.size());
        Recipe recipe = recipes.get(0);
        assertEquals(3, recipe.getIngredients().size());
        Ingredient eggs = recipe.getIngredients().get(0);
        assertEquals(3.0, eggs.getQuantity());
        assertNull(eggs.getUnit());
        assertEquals("eggs", eggs.getName());
        Ingredient cheese = recipe.getIngredients().get(1);
        assertEquals(200.0, cheese.getQuantity());
        assertEquals("g", cheese.getUnit());
        assertEquals("cheese", cheese.getName());
        Ingredient oil = recipe.getIngredients().get(2);
        assertEquals(1.5, oil.getQuantity());
        assertEquals("tbsp", oil.getUnit());
        assertEquals("olive oil", oil.getName());
        assertEquals("omelette, breakfast added!", ui.message);
        assertNull(ui.error);
    }

    @Test
    void execute_invalidIngredient_reportsErrorWithoutAddingRecipe() {
        String[] ingredients = {
            "0 eggs", "-1 eggs", "eggs", "NaN g salt", "200 g", "2xyz rice", "9".repeat(400) + " g rice"
        };
        for (String ingredient : ingredients) {
            RecipeList recipes = new RecipeList();
            RecordingUi ui = new RecordingUi();
            AddCommand command = new AddCommand("omelette i/" + ingredient);

            WhatsCookingException exception = assertThrows(
                    WhatsCookingException.class, () -> command.execute(recipes, ui), ingredient);

            assertTrue(exception.getMessage().contains("Ingredients require"), ingredient);
            assertEquals(0, recipes.size(), ingredient);
            assertNull(ui.message, ingredient);
        }
    }

    @Test
    void execute_invalidLaterIngredient_preservesExistingRecipes() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Recipe existing = new Recipe("toast", "breakfast");
        recipes.add(existing);
        RecordingUi ui = new RecordingUi();

        AddCommand command = new AddCommand("omelette i/3 eggs, 0 g cheese st/Cook");
        assertThrows(WhatsCookingException.class, () -> command.execute(recipes, ui));

        assertEquals(1, recipes.size());
        assertSame(existing, recipes.get(0));
        assertNull(ui.message);
    }

    @Test
    void execute_validCommandAfterFailure_addsOnlyValidRecipe() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        RecordingUi errorUi = new RecordingUi();
        RecordingUi successUi = new RecordingUi();

        AddCommand failing = new AddCommand("invalid i/0 eggs");
        assertThrows(WhatsCookingException.class, () -> failing.execute(recipes, errorUi));

        new AddCommand("omelette i/3 eggs").execute(recipes, successUi);

        assertEquals(1, recipes.size());
        assertEquals("omelette", recipes.get(0).getTitle());
        assertEquals("3 eggs", recipes.get(0).getIngredients().get(0).getDescription());
        assertNull(errorUi.message);
        assertEquals("omelette added!", successUi.message);
        assertNull(successUi.error);
    }

    /**
     * Records user-facing feedback without changing the process-wide console streams.
     */
    private static class RecordingUi extends Ui {
        private String message;
        private String error;

        @Override
        public void showMessage(String message) {
            this.message = message;
        }

        @Override
        public void showError(String message) {
            this.error = message;
        }
    }
}
