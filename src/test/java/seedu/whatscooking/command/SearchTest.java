package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

public class SearchTest {
    private RecipeList createListWithOneRecipe() {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        return recipes;
    }

    @Test 
    public void testSearchCommandTrue() throws WhatsCookingException {
        RecipeList oneRecipeList = createListWithOneRecipe();

        SearchCommand search = new SearchCommand("Chicken");
        assertTrue(search.internalExecute(oneRecipeList).size() == 1);
    }

    @Test 
    public void testSearchCommandFail() throws WhatsCookingException {
        RecipeList oneRecipeList = createListWithOneRecipe();

        SearchCommand search = new SearchCommand("Juice");
        assertTrue(search.internalExecute(oneRecipeList).size() == 0);
    }

    @Test
    public void internalExecute_multipleMatches_returnsMatchingIndicesInOrder()
            throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Vegetable Soup", "main"));
        recipes.add(new Recipe("Chicken Curry", "main"));

        ArrayList<Integer> matches = new SearchCommand("Chicken").internalExecute(recipes);

        assertEquals(java.util.List.of(0, 2), matches);
    }

    @Test
    public void internalExecute_searchIsCaseSensitive_doesNotMatchDifferentCase()
            throws WhatsCookingException {
        RecipeList recipes = createListWithOneRecipe();

        ArrayList<Integer> matches = new SearchCommand("chicken").internalExecute(recipes);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void internalExecute_emptyKeyword_matchesEveryRecipe() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Vegetable Soup", "main"));

        ArrayList<Integer> matches = new SearchCommand("").internalExecute(recipes);

        assertEquals(java.util.List.of(0, 1), matches);
    }

    @Test
    public void internalExecute_emptyRecipeList_returnsNoMatches() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();

        ArrayList<Integer> matches = new SearchCommand("Chicken").internalExecute(recipes);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void execute_matchingRecipes_displaysCountAndRecipes() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Vegetable Soup", "main"));
        RecordingUi ui = new RecordingUi();

        new SearchCommand("Chicken").execute(recipes, ui);

        assertEquals(java.util.List.of("Found 1 Recipes: ", "Chicken Pasta (main, serves 1)"),
                ui.messages);
    }

    private static class RecordingUi extends Ui {
        private final ArrayList<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }
    }
}
