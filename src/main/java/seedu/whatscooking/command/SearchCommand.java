package seedu.whatscooking.command;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Searches recipes using a filter in the form {@code f/{field}/{keyword}}.
 * The supported fields are {@code name} and {@code ingredient}.
 */
public class SearchCommand extends Command {
    private static final String FILTER_PREFIX = "f/";
    private static final Logger logger = AppLogger.getLogger(SearchCommand.class);
    private final String arguments;

    public SearchCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Returns a list of indices of recipes that match all filters.
     *
     * @param recipes the list of recipes to search
     * @return a list of indices of recipes that match all filters
     * @throws WhatsCookingException if a filter is malformed
     */
    public ArrayList<Integer> internalExecute(RecipeList recipes) throws WhatsCookingException {
        ArrayList<Filter> filters = parseFilters();
        ArrayList<Integer> matches = new ArrayList<>();

        logger.log(Level.FINE, "Running SearchCommand with {0} filters on {1} recipes",
                new Object[]{filters.size(), recipes.size()});

        for (int i = 0; i < recipes.size(); i++) {
            boolean matchesAllFilters = true;
            for (Filter filter : filters) {
                if (!filter.matches(recipes.get(i))) {
                    matchesAllFilters = false;
                    break;
                }
            }
            if (matchesAllFilters) {
                logger.log(Level.FINE, "Recipe {0} matches all filters", recipes.get(i).getTitle());
                matches.add(i);
            }
        }
        return matches;
    }

    /** Parses and validates the user-provided filter. */
    private ArrayList<Filter> parseFilters() throws WhatsCookingException {
        String trimmedArguments = arguments.trim();
        if (trimmedArguments.isEmpty()) {
            logger.log(Level.FINE, "Rejected search: no filter found");
            throw new WhatsCookingException("Please provide at least one search filter in the form f/{field}/{keyword}.");
        }

        // A filter does not contain spaces, so whitespace is a separator between filters.
        String[] filterStrings = trimmedArguments.split("\\s+");
        ArrayList<Filter> filters = new ArrayList<>();
        for (String filterString : filterStrings) {
            if (!filterString.startsWith(FILTER_PREFIX)) {
                logger.log(Level.FINE, "Rejected search filter: invalid prefix in \"{0}\"", filterString);
                throw new WhatsCookingException("Search filters must use f/{name/ingredient}/{keyword}.");
            }
            String[] parts = filterString.split("/", 3);
            if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
                logger.log(Level.FINE, "Rejected search filter: invalid format \"{0}\"", filterString);
                throw new WhatsCookingException("Search filters must use f/{name/ingredient}/{keyword}.");
            }

            FilterType type;
            if (parts[1].equals("name")) {
                type = FilterType.NAME;
            } else if (parts[1].equals("ingredient")) {
                type = FilterType.INGREDIENT;
            } else {
                logger.log(Level.FINE, "Rejected search filter: unsupported filter type \"{0}\"", parts[1]);
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

        boolean matches(Recipe recipe) {
            logger.log(Level.FINE, "Using filter: type={0}, keyword={1} on recipe {2}",
                    new Object[]{type, keyword, recipe.getTitle()});
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
                    logger.log(Level.FINE, "Rejected search filter: unexpected type {0}", type);
                    throw new IllegalStateException("Unexpected filter type: " + type);
            }
        }
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "The application must supply its recipe list";
        assert ui != null : "The application must supply its UI";

        logger.log(Level.FINE, "Executing command with input \"{0}\"", arguments.trim());
        ArrayList<Integer> out = internalExecute(recipes);
        logger.log(Level.FINE, "Search found {0} matching recipes", out.size());
        ui.showMessage("Found " + out.size() + " Recipes: ");
        for (int i = 0; i < out.size(); i++) {
            ui.showMessage(recipes.get(out.get(i)).toString());
        }
    }
}
