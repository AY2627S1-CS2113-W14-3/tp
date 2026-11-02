package seedu.whatscooking.command.filter;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.recipe.StockItem;

/** Matches recipes that can be prepared from stock for a requested serving size. */
public class StockFilter extends Filter {
    private final StockList stockList;
    private final int targetServingSize;

    public StockFilter(int targetServingSize, StockList stockList) {
        this.targetServingSize = targetServingSize;
        this.stockList = stockList;
    }

    @Override
    public boolean matches(Recipe recipe) throws WhatsCookingException {
        Recipe scaledRecipe = new Recipe(recipe.getTitle(), recipe.getType());
        scaledRecipe.setServingSize(recipe.getServingSize());
        for (Ingredient ingredient : recipe.getIngredients()) {
            scaledRecipe.addIngredient(new Ingredient(ingredient.getQuantity(),
                    ingredient.getUnit(), ingredient.getName()));
        }
        scaledRecipe.scaleToServingSize(targetServingSize);

        for (Ingredient required : scaledRecipe.getIngredients()) {
            double available = 0;
            for (StockItem stocked : stockList.getAll()) {
                boolean sameName = stocked.getName().equalsIgnoreCase(required.getName());
                boolean sameUnit = (stocked.getUnit() == null && required.getUnit() == null)
                        || (stocked.getUnit() != null && required.getUnit() != null
                        && stocked.getUnit().equalsIgnoreCase(required.getUnit()));
                if (sameName && sameUnit) {
                    available += stocked.getQuantity();
                }
            }
            if (available + 0.0001 < required.getQuantity()) {
                return false;
            }
        }
        return true;
    }


}
