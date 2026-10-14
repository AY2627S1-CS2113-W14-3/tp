package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Tests the modify command against its User Guide, including safe failure and name matching.
 */
class ModifyCommandTest {
    @Test
    void execute_userGuideExample_scalesIngredientsAndReportsUpdate() throws WhatsCookingException {
        RecipeList recipes = createRecipes();
        RecordingUi ui = new RecordingUi();
        Command command = Parser.parse("modify Egg Taco s/4");

        command.execute(recipes, ui);

        Recipe recipe = recipes.get(0);
        assertEquals(4, recipe.getServingSize());
        assertEquals("4 eggs", recipe.getIngredients().get(0).getDisplayDescription());
        assertEquals("8 tortillas", recipe.getIngredients().get(1).getDisplayDescription());
        assertEquals("200 g cheese", recipe.getIngredients().get(2).getDisplayDescription());
        assertSame(recipe, ui.modifiedRecipe);
        assertEquals(2, ui.previousServingSize);
        assertFalse(command.isExit());
    }

    @Test
    void execute_mixedCaseAndWhitespace_matchesFullName() throws WhatsCookingException {
        RecipeList recipes = createRecipes();

        new ModifyCommand("  eGG tACO \t s/ 3  ").execute(recipes, new RecordingUi());

        assertEquals(3, recipes.get(0).getServingSize());
        assertEquals(150.0, recipes.get(0).getIngredients().get(2).getQuantity());
    }

    @Test
    void execute_sameSize_reportsNoChange() throws WhatsCookingException {
        RecipeList recipes = createRecipes();
        RecordingUi ui = new RecordingUi();

        new ModifyCommand("Egg Taco s/2").execute(recipes, ui);

        assertEquals("Egg Taco already has a serving size of 2. No changes were made.", ui.message);
        assertNull(ui.modifiedRecipe);
        assertEquals(2.0, recipes.get(0).getIngredients().get(0).getQuantity());
    }

    @Test
    void execute_missingOrRepeatedParameters_rejectsWithoutChanges() throws WhatsCookingException {
        String[] inputs = {null, "", "  ", "Egg Taco", "s/2", "Egg Taco s/", "Egg Tacos/2",
            "Egg Taco s/2 s/3", "Egg Taco 2"};
        for (String input : inputs) {
            RecipeList recipes = createRecipes();
            RecordingUi ui = new RecordingUi();

            WhatsCookingException error = assertThrows(WhatsCookingException.class,
                    () -> new ModifyCommand(input).execute(recipes, ui));

            assertTrue(error.getMessage().startsWith("Use: modify"));
            assertEquals(2, recipes.get(0).getServingSize());
            assertEquals(100.0, recipes.get(0).getIngredients().get(2).getQuantity());
            assertNull(ui.message);
            assertNull(ui.modifiedRecipe);
        }
    }

    @Test
    void execute_invalidServingSize_rejectsWithoutChanges() throws WhatsCookingException {
        String[] sizes = {"0", "-1", "+2", "1.5", "2e3", "zero", "NaN", "Infinity",
            "2147483648", "9".repeat(100), "2 extra"};
        for (String size : sizes) {
            RecipeList recipes = createRecipes();
            RecordingUi ui = new RecordingUi();

            WhatsCookingException error = assertThrows(WhatsCookingException.class,
                    () -> new ModifyCommand("Egg Taco s/" + size).execute(recipes, ui));

            assertEquals("Serving size must be a positive integer.", error.getMessage());
            assertEquals(2, recipes.get(0).getServingSize());
            assertEquals(100.0, recipes.get(0).getIngredients().get(2).getQuantity());
            assertNull(ui.modifiedRecipe);
        }
    }

    @Test
    void execute_unknownOrPartialName_reportsMissingRecipe() throws WhatsCookingException {
        RecipeList recipes = createRecipes();
        for (String name : new String[]{"Unknown Recipe", "Egg"}) {
            WhatsCookingException error = assertThrows(WhatsCookingException.class,
                    () -> new ModifyCommand(name + " s/4").execute(recipes, new RecordingUi()));

            assertEquals("Recipe \"" + name + "\" could not be found.", error.getMessage());
            assertEquals(2, recipes.get(0).getServingSize());
        }
    }

    @Test
    void execute_duplicateNames_modifiesFirstMatchOnly() throws WhatsCookingException {
        RecipeList recipes = createRecipes();
        Recipe second = new Recipe("Egg Taco", "breakfast");
        second.addIngredient(new Ingredient("1 egg"));
        recipes.add(second);

        new ModifyCommand("Egg Taco s/4").execute(recipes, new RecordingUi());

        assertEquals(2, recipes.size());
        assertEquals(4, recipes.get(0).getServingSize());
        assertEquals(1, second.getServingSize());
        assertEquals(1.0, second.getIngredients().get(0).getQuantity());
    }

    @Test
    void execute_quantityOverflows_reportsErrorWithoutPartialUpdate() throws WhatsCookingException {
        RecipeList recipes = createRecipes();
        recipes.get(0).addIngredient(new Ingredient(Double.MAX_VALUE, "g", "rice"));
        RecordingUi ui = new RecordingUi();

        WhatsCookingException error = assertThrows(WhatsCookingException.class,
                () -> new ModifyCommand("Egg Taco s/4").execute(recipes, ui));

        assertEquals("Cannot scale recipe: an ingredient quantity would be too large or too small.",
                error.getMessage());
        assertEquals(2, recipes.get(0).getServingSize());
        assertEquals(2.0, recipes.get(0).getIngredients().get(0).getQuantity());
        assertNull(ui.modifiedRecipe);
    }

    @Test
    void execute_largestIntegerServingSize_acceptsValidBoundary() throws WhatsCookingException {
        RecipeList recipes = createRecipes();

        new ModifyCommand("Egg Taco s/2147483647").execute(recipes, new RecordingUi());

        assertEquals(Integer.MAX_VALUE, recipes.get(0).getServingSize());
        assertEquals((double) Integer.MAX_VALUE, recipes.get(0).getIngredients().get(0).getQuantity());
    }

    /**
     * Recreates the independent two-serving example from the User Guide.
     */
    private RecipeList createRecipes() {
        Recipe recipe = new Recipe("Egg Taco", "snack");
        recipe.setServingSize(2);
        recipe.addIngredient(new Ingredient("2 eggs"));
        recipe.addIngredient(new Ingredient("4 tortillas"));
        recipe.addIngredient(new Ingredient("100 g cheese"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);
        return recipes;
    }

    /**
     * Captures command feedback without replacing global console streams.
     */
    private static class RecordingUi extends Ui {
        private String message;
        private Recipe modifiedRecipe;
        private int previousServingSize;

        @Override
        public void showMessage(String message) {
            this.message = message;
        }

        @Override
        public void showModifiedRecipe(Recipe recipe, int previousServingSize) {
            this.modifiedRecipe = recipe;
            this.previousServingSize = previousServingSize;
        }
    }
}
