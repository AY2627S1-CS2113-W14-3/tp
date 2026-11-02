package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.allergen.SubstitutionSuggestion;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/** Verifies real parser dispatch, name resolution and unchanged recipes for substitution requests. */
class SubstituteCommandTest {
    @Test
    void execute_fullNameWithCaseAndWhitespace_displaysAdviceWithoutChangingRecipe() throws Exception {
        Recipe recipe = new Recipe("Banana Cake", "dessert");
        recipe.setServingSize(2);
        recipe.addIngredient(new Ingredient("2 eggs"));
        recipe.addStep("Bake");
        recipe.setNote("For tea");
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);
        RecordingUi ui = new RecordingUi();

        Command command = Parser.parse("substitute   bANANA cAKE  ");
        command.execute(recipes, ui);

        assertSame(recipe, ui.recipe);
        assertEquals("mashed banana", ui.suggestions.get(0).replacement());
        assertFalse(command.isExit());
        assertEquals(1, recipes.size());
        assertEquals("Banana Cake", recipe.getTitle());
        assertEquals("dessert", recipe.getType());
        assertEquals(2, recipe.getServingSize());
        assertEquals("2 eggs", recipe.getIngredients().get(0).getDisplayDescription());
        assertEquals(List.of("Bake"), recipe.getSteps());
        assertEquals("For tea", recipe.getNote());
    }

    @Test
    void execute_numericTitle_resolvesTitleInsteadOfIndex() throws Exception {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("First", ""));
        Recipe numericRecipe = new Recipe("2147483648", "");
        recipes.add(numericRecipe);
        RecordingUi ui = new RecordingUi();

        Parser.parse("substitute 2147483648").execute(recipes, ui);

        assertSame(numericRecipe, ui.recipe);
        assertTrue(ui.suggestions.isEmpty());
    }

    @Test
    void execute_missingName_reportsUsageWithoutDisplayingAdvice() {
        for (String input : List.of("substitute", "substitute   ")) {
            RecordingUi ui = new RecordingUi();
            WhatsCookingException error = assertThrows(WhatsCookingException.class,
                    () -> Parser.parse(input).execute(new RecipeList(), ui));

            assertTrue(error.getMessage().startsWith("Usage: substitute RECIPE_NAME"));
            assertNull(ui.recipe);
        }
    }

    @Test
    void execute_unknownOrPartialName_doesNotFallBackToAnotherRecipe() {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Banana Cake", ""));
        for (String name : List.of("Banana", "Missing", "1")) {
            RecordingUi ui = new RecordingUi();
            assertThrows(WhatsCookingException.class,
                    () -> new SubstituteCommand(name).execute(recipes, ui));
            assertNull(ui.recipe);
            assertEquals(1, recipes.size());
        }
    }

    @Test
    void execute_titleAndNoteMentionMilk_usesOnlyIngredientNames() throws Exception {
        Recipe recipe = new Recipe("Milk Cake", "");
        recipe.setNote("eggs");
        recipe.addStep("Add butter");
        recipe.addIngredient(new Ingredient("2 g rice"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);
        RecordingUi ui = new RecordingUi();

        new SubstituteCommand("Milk Cake").execute(recipes, ui);

        assertTrue(ui.suggestions.isEmpty());
    }

    /** Captures the command's UI boundary while leaving console formatting to workflow tests. */
    private static class RecordingUi extends Ui {
        private Recipe recipe;
        private List<SubstitutionSuggestion> suggestions;

        @Override
        public void showSubstitutionSuggestions(Recipe selected, List<SubstitutionSuggestion> results) {
            recipe = selected;
            suggestions = results;
        }
    }
}
