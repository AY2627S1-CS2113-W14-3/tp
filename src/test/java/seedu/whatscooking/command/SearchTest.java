package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

public class SearchTest {
    @Test
    public void internalExecute_multipleMatches_returnsMatchingIndicesInOrder()
            throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Vegetable Soup", "main"));
        recipes.add(new Recipe("Chicken Curry", "main"));

        ArrayList<Integer> matches = new SearchCommand("f/name/Chicken").internalExecute(recipes);

        assertEquals(java.util.List.of(0, 2), matches);
    }

    @Test
    public void internalExecute_emptyRecipeList_returnsNoMatches() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();

        ArrayList<Integer> matches = new SearchCommand("f/name/Chicken").internalExecute(recipes);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void execute_matchingRecipes_displaysCountAndRecipes() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Vegetable Soup", "main"));
        RecordingUi ui = new RecordingUi();

        new SearchCommand("f/name/Chicken").execute(recipes, ui);

        assertEquals(java.util.List.of("Found 1 Recipes: ", "Chicken Pasta (main, serves 1)"),
                ui.messages);
    }

    @Test
    public void internalExecute_multipleFilters_requiresEveryFilterToMatch()
            throws WhatsCookingException {
        Recipe pasta = new Recipe("Chicken Pasta", "main");
        pasta.addIngredient(new Ingredient("200 g chicken"));
        Recipe curry = new Recipe("Chicken Curry", "main");
        curry.addIngredient(new Ingredient("2 tomatoes"));
        Recipe soup = new Recipe("Vegetable Soup", "main");
        soup.addIngredient(new Ingredient("2 chicken stock"));
        RecipeList recipes = new RecipeList();
        recipes.add(pasta);
        recipes.add(curry);
        recipes.add(soup);

        ArrayList<Integer> matches = new SearchCommand(
                "f/name/Chicken f/ingredient/chicken").internalExecute(recipes);

        assertEquals(java.util.List.of(0), matches);
    }

    @Test
    public void internalExecute_multipleFilters_allowsExtraWhitespace()
            throws WhatsCookingException {
        Recipe recipe = new Recipe("Chicken Pasta", "main");
        recipe.addIngredient(new Ingredient("1 chicken"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);

        ArrayList<Integer> matches = new SearchCommand(
                "  f/name/chicken   f/ingredient/chicken  ").internalExecute(recipes);

        assertEquals(java.util.List.of(0), matches);
    }


    private static class RecordingUi extends Ui {
        private final ArrayList<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }
    }
}
