package seedu.whatscooking.command.filter;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.StockList;

/** Matches recipes that can be prepared from stock for a requested serving size. */
public class StockFilter extends Filter {
    private final StockList stockList;
    private final String keyword;

    public StockFilter(String keyword, StockList stockList) {
        this.keyword = keyword;
        this.stockList = stockList;
    }

    @Override
    public boolean matches(Recipe recipe) throws WhatsCookingException {
        int servingSize = parseServingSize(keyword);
        Recipe scaledRecipe = new Recipe(recipe.getTitle(), recipe.getType());
        scaledRecipe.setServingSize(recipe.getServingSize());
        for (Ingredient ingredient : recipe.getIngredients()) {
            scaledRecipe.addIngredient(new Ingredient(ingredient.getQuantity(),
                    ingredient.getUnit(), ingredient.getName()));
        }
        scaledRecipe.scaleToServingSize(servingSize);

        for (Ingredient required : scaledRecipe.getIngredients()) {
            double available = 0;
            for (Ingredient stocked : stockList.getAll()) {
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

    private int parseServingSize(String text) throws WhatsCookingException {
        if (!text.matches("[0-9]+")) {
            throw new WhatsCookingException("Stock must be a positive integer.");
        }
        try {
            int servingSize = Integer.parseInt(text);
            if (servingSize <= 0) {
                throw new WhatsCookingException("Stock must be a positive integer.");
            }
            return servingSize;
        } catch (NumberFormatException e) {
            throw new WhatsCookingException("Stock must be a positive integer.");
        }
    }
}
