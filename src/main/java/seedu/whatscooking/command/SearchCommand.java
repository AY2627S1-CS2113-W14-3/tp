package seedu.whatscooking.command;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.ui.Ui;
import seedu.whatscooking.command.filter.Filter;
import seedu.whatscooking.command.filter.FilterFactory;

/**
 * Searches recipes using a filter in the form {@code f/{field}/{keyword}}.
 * The supported fields are {@code name}, {@code ingredient}, and {@code stock}.
 * The stock filter is written as {@code f/stock/{number}}, where the number is
 * the desired serving size.
 */
public class SearchCommand extends Command {
    private static final Logger logger = AppLogger.getLogger(SearchCommand.class);
    private final StockList stockList;
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
        ArrayList<Filter> filters = FilterFactory.createFilters(arguments, stockList);
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
