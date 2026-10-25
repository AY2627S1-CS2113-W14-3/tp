package seedu.whatscooking.command;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.ui.Ui;
import seedu.whatscooking.command.filter.Filter;
import seedu.whatscooking.command.filter.IngredientFilter;
import seedu.whatscooking.command.filter.NameFilter;
import seedu.whatscooking.command.filter.StockFilter;

/**
 * Searches recipes using a filter in the form {@code f/{field}/{keyword}}.
 * The supported fields are {@code name}, {@code ingredient}, and {@code stock}.
 * The stock filter is written as {@code f/stock/{number}}, where the number is
 * the desired serving size.
 */
public class SearchCommand extends Command {
    private final StockList stockList;
    private static final String FILTER_PREFIX = "f/";
    private static final Logger logger = AppLogger.getLogger(SearchCommand.class);
    private final String arguments;

    public SearchCommand(String arguments, StockList stockList) {
        this.arguments = arguments;
        this.stockList = stockList;
    }

    /**
     * Constructs a search with an empty stock list. This overload keeps searches
     * independent from the application's stock when no StockList is supplied.
     *
     * @param arguments the raw search filters
     */
    public SearchCommand(String arguments) {
        this(arguments, new StockList());
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
            throw new WhatsCookingException(
                "Please provide at least one search filter in the form f/{field}/{keyword}.");
        }

        // A filter does not contain spaces, so whitespace is a separator between filters.
        String[] filterStrings = trimmedArguments.split("\\s+");
        ArrayList<Filter> filters = new ArrayList<>();
        for (String filterString : filterStrings) {
            if (!filterString.startsWith(FILTER_PREFIX)) {
                logger.log(Level.FINE, "Rejected search filter: invalid prefix in \"{0}\"", filterString);
                throw new WhatsCookingException("Search filters must use f/{name/ingredient/stock}/{keyword}.");
            }
            String[] parts = filterString.split("/", 3);
            if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
                logger.log(Level.FINE, "Rejected search filter: invalid format \"{0}\"", filterString);
                throw new WhatsCookingException("Search filters must use f/{name/ingredient/stock}/{keyword}.");
            }

            Filter filter;
            if (parts[1].equals("name")) {
                filter = new NameFilter(parts[2]);
            } else if (parts[1].equals("ingredient")) {
                filter = new IngredientFilter(parts[2]);
            } else if (parts[1].equals("stock")) {
                int servingSize = parseServingSize(parts[2]);
                filter = new StockFilter(servingSize, stockList);
            } else {
                logger.log(Level.FINE, "Rejected search filter: unsupported filter type \"{0}\"", parts[1]);
                throw new WhatsCookingException("Search filter must be name, ingredient, or stock.");
            }
            filters.add(filter);
        }
        return filters;
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
