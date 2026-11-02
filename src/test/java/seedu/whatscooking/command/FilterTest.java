package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.command.filter.IngredientFilter;
import seedu.whatscooking.command.filter.NameFilter;
import seedu.whatscooking.command.filter.StockFilter;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.StockItem;
import seedu.whatscooking.recipe.StockList;

/** Tests each search filter independently of SearchCommand. */
public class FilterTest {
    @Test
    public void nameFilter_matchesCaseInsensitively() throws WhatsCookingException {
        assertTrue(new NameFilter("chicken").matches(new Recipe("Chicken Pasta", "main")));
        assertFalse(new NameFilter("juice").matches(new Recipe("Chicken Pasta", "main")));
    }

    @Test
    public void ingredientFilter_matchesIngredientName() throws WhatsCookingException {
        Recipe recipe = new Recipe("Pasta", "main");
        recipe.addIngredient(new Ingredient("200 g chicken"));

        assertTrue(new IngredientFilter("CHICK").matches(recipe));
        assertFalse(new IngredientFilter("tomato").matches(recipe));
    }

    @Test
    public void stockFilter_matchesAllIngredientsForRequestedServings()
            throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("4 eggs"),null)); //updated by Siyu
        Recipe recipe = new Recipe("Omelette", "main");
        recipe.addIngredient(new Ingredient("2 eggs"));
        recipe.setServingSize(2);

        assertTrue(new StockFilter(4, stock).matches(recipe));
        assertFalse(new StockFilter(5, stock).matches(recipe));
    }

    @Test
    public void stockFilter_rejectsInvalidServingSize() {
        StockFilter filter = new StockFilter(0, new StockList());
        Recipe recipe = new Recipe("Omelette", "main");

        assertThrows(IllegalArgumentException.class, () -> filter.matches(recipe));
    }
}
