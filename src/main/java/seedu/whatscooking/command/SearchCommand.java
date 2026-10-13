package seedu.whatscooking.command;

import java.util.ArrayList;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Searches recipes using a filter in the form {@code f/{field}/{keyword}}.
 * The supported fields are {@code name} and {@code ingredient}.
 */
public class SearchCommand extends Command {
    private static final String FILTER_PREFIX = "f/";
    private final String arguments;

    public SearchCommand(String arguments) {
        this.arguments = arguments;
    }

    public ArrayList<Integer> internalExecute(RecipeList recipes) throws WhatsCookingException {
        Filter filter = parseFilter();
        ArrayList<Integer> matches = new ArrayList<>();

        for(int i = 0; i < recipes.size(); i++) {
            if (filter.matches(recipes.get(i))) {
                matches.add(i);
            }
        }
        return matches;
    }

    /** Parses and validates the user-provided filter. */
    private Filter parseFilter() throws WhatsCookingException {
        if (!arguments.startsWith(FILTER_PREFIX)) {
            throw new WhatsCookingException("Search filters must use f/{name/ingredient}/{keyword}.");
        }

        String[] parts = arguments.split("/", 3);
        if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
            throw new WhatsCookingException("Search filters must use f/{name/ingredient}/{keyword}.");
        }

        FilterType type;
        if (parts[1].equals("name")) {
            type = FilterType.NAME;
        } else if (parts[1].equals("ingredient")) {
            type = FilterType.INGREDIENT;
        } else {
            throw new WhatsCookingException("Search filter must be either name or ingredient.");
        }
        return new Filter(type, parts[2]);
    }

    private enum FilterType {
        NAME,
        INGREDIENT
    }

    /** Holds a validated filter and applies it to recipes. */
    private static class Filter {
        private final FilterType type;
        private final String keyword;

        Filter(FilterType type, String keyword) {
            this.type = type;
            this.keyword = keyword;
        }

        boolean matches(seedu.whatscooking.recipe.Recipe recipe) {
            if (type == FilterType.NAME) {
                return recipe.getTitle().contains(keyword);
            }
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getName().contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        ArrayList<Integer> out = internalExecute(recipes);
        ui.showMessage("Found " + out.size() + " Recipes: ");
        for (int i = 0; i < out.size(); i++) {

            ui.showMessage(recipes.get(out.get(i)).toString());
        }
    }
}
