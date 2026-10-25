package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
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

        SearchCommand search = new SearchCommand("f/name/Chicken");
        assertTrue(search.internalExecute(oneRecipeList).size() == 1);
    }

    @Test 
    public void testSearchCommandFail() throws WhatsCookingException {
        RecipeList oneRecipeList = createListWithOneRecipe();

        SearchCommand search = new SearchCommand("f/name/Juice");
        assertTrue(search.internalExecute(oneRecipeList).size() == 0);
    }

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
    public void internalExecute_searchIsCaseInsensitive_matchesDifferentCase()
            throws WhatsCookingException {
        RecipeList recipes = createListWithOneRecipe();

        ArrayList<Integer> matches = new SearchCommand("f/name/chicken").internalExecute(recipes);

        assertEquals(java.util.List.of(0), matches);
    }

    @Test
    public void internalExecute_emptyKeyword_throwsException() {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Vegetable Soup", "main"));

        assertThrows(WhatsCookingException.class,
                () -> new SearchCommand("f/name/").internalExecute(recipes));
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
    public void internalExecute_ingredientFilter_matchesIngredientName()
            throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Recipe pasta = new Recipe("Pasta", "main");
        pasta.addIngredient(new Ingredient("200 g chicken"));
        recipes.add(pasta);
        Recipe soup = new Recipe("Soup", "main");
        soup.addIngredient(new Ingredient("2 tomatoes"));
        recipes.add(soup);

        ArrayList<Integer> matches = new SearchCommand("f/ingredient/chicken").internalExecute(recipes);

        assertEquals(java.util.List.of(0), matches);
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

    @Test
    public void internalExecute_invalidFilter_throwsException() {
        RecipeList recipes = createListWithOneRecipe();

        assertThrows(WhatsCookingException.class,
                () -> new SearchCommand("Chicken").internalExecute(recipes));
        assertThrows(WhatsCookingException.class,
                () -> new SearchCommand("f/category/Chicken").internalExecute(recipes));
        assertThrows(WhatsCookingException.class,
                () -> new SearchCommand("f/name").internalExecute(recipes));
    }

    @Test
    public void internalExecute_stockFilter_matchesRecipeWithAllRequiredStock()
            throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new Ingredient("2 eggs"));
        stock.add(new Ingredient("200 g flour"));

        Recipe recipe = new Recipe("Pancakes", "breakfast");
        recipe.addIngredient(new Ingredient("2 eggs"));
        recipe.addIngredient(new Ingredient("200 g flour"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);

        assertEquals(java.util.List.of(0),
                new SearchCommand("f/stock", stock).internalExecute(recipes));
    }

    @Test
    public void internalExecute_stockFilter_rejectsInsufficientQuantityOrWrongUnit()
            throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new Ingredient("1 egg"));
        stock.add(new Ingredient("200 g flour"));

        Recipe recipe = new Recipe("Pancakes", "breakfast");
        recipe.addIngredient(new Ingredient("2 eggs"));
        recipe.addIngredient(new Ingredient("200 ml flour"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);

        assertTrue(new SearchCommand("f/stock", stock).internalExecute(recipes).isEmpty());
    }

    @Test
    public void internalExecute_stockFilter_totalsDuplicateRecipeIngredients()
            throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new Ingredient("3 eggs"));

        Recipe recipe = new Recipe("Eggs", "main");
        recipe.addIngredient(new Ingredient("1 egg"));
        recipe.addIngredient(new Ingredient("2 eggs"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);

        assertEquals(java.util.List.of(0),
                new SearchCommand("f/stock/available", stock).internalExecute(recipes));
    }

    private static class RecordingUi extends Ui {
        private final ArrayList<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }
    }
}
