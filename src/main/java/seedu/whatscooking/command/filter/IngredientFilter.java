package seedu.whatscooking.command.filter;

import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;

/** Matches recipes containing an ingredient whose name contains a keyword. */
public class IngredientFilter extends Filter {
    private final String keyword;

    public IngredientFilter(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean matches(Recipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getName().toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
