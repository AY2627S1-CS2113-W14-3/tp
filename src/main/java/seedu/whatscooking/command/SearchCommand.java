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
        ArrayList<Filter> filters = parseFilters();
        ArrayList<Integer> matches = new ArrayList<>();

        for(int i = 0; i < recipes.size(); i++) {
            boolean matchesAllFilters = true;
            for (Filter filter : filters) {
                if (!filter.matches(recipes.get(i))) {
                    matchesAllFilters = false;
                    break;
                }
            }
            if (matchesAllFilters) {
                matches.add(i);
            }
        }
        return matches;
    }

    /** Parses and validates the user-provided filter. */
    private ArrayList<Filter> parseFilters() throws WhatsCookingException {
        String trimmedArguments = arguments.trim();
        if (trimmedArguments.isEmpty()) {
            throw new WhatsCookingException("Search filters must use f/{name/ingredient}/{keyword}.");
        }

        // A filter does not contain spaces, so whitespace is a separator between filters.
        String[] filterStrings = trimmedArguments.split("\\s+");
        ArrayList<Filter> filters = new ArrayList<>();
        for (String filterString : filterStrings) {
            if (!filterString.startsWith(FILTER_PREFIX)) {
                throw new WhatsCookingException("Search filters must use f/{name/ingredient}/{keyword}.");
            }
            String[] parts = filterString.split("/", 3);
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
            filters.add(new Filter(type, parts[2]));
        }
        return filters;
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
            switch (type) {
                case NAME:
                    return recipe.getTitle().toLowerCase().contains(keyword.toLowerCase());
                case INGREDIENT:
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (ingredient.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                default:
                    throw new IllegalStateException("Unexpected filter type: " + type);
            }
        }
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "The application must supply its recipe list";
        assert ui != null : "The application must supply its UI";
        ArrayList<Integer> out = internalExecute(recipes);
        ui.showMessage("Found " + out.size() + " Recipes: ");
        for (int i = 0; i < out.size(); i++) {

            ui.showMessage(recipes.get(out.get(i)).toString());
        }
    }
}
