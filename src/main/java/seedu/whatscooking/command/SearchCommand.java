package seedu.whatscooking.command;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.ui.Ui;

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

            FilterType type;
            if (parts[1].equals("name")) {
                type = FilterType.NAME;
            } else if (parts[1].equals("ingredient")) {
                type = FilterType.INGREDIENT;
            } else if (parts[1].equals("stock")) {
                type = FilterType.STOCK;
            } else {
                logger.log(Level.FINE, "Rejected search filter: unsupported filter type \"{0}\"", parts[1]);
                throw new WhatsCookingException("Search filter must be name, ingredient, or stock.");
            }
            filters.add(new Filter(type, parts.length == 2 ? "" : parts[2]));
        }
        return filters;
    }

    private enum FilterType {
        NAME,
        INGREDIENT,
        STOCK
    }

    /** Holds a validated filter and applies it to recipes. */
    private class Filter {
        private final FilterType type;
        private final String keyword;

        Filter(FilterType type, String keyword) {
            this.type = type;
            this.keyword = keyword;
        }

        boolean matches(Recipe recipe) throws WhatsCookingException {
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
            case STOCK:
                return canBeMadeWithStock(recipe, parseStockSize(keyword));
            default:
                logger.log(Level.FINE, "Rejected search filter: unexpected type {0}", type);
                throw new IllegalStateException("Unexpected filter type: " + type);
            }
        }

        /** Returns whether the stock contains enough for every ingredient at the desired serving size. */
        private boolean canBeMadeWithStock(Recipe recipe, int stockSize) {
            Recipe scaledRecipe = new Recipe(recipe.getTitle(), recipe.getType());
            scaledRecipe.setServingSize(recipe.getServingSize());
            for (Ingredient ingredient : recipe.getIngredients()) {
                scaledRecipe.addIngredient(new Ingredient(ingredient.getQuantity(),
                        ingredient.getUnit(), ingredient.getName()));
            }
            scaledRecipe.scaleToServingSize(stockSize);

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

        /** Parses the stock keyword as a positive integer. */
        private int parseStockSize(String text) throws WhatsCookingException {
            if (!text.matches("[0-9]+")) {
                throw new WhatsCookingException("Stock must be a positive integer.");
            }
            try {
                int stockSize = Integer.parseInt(text);
                if (stockSize <= 0) {
                    throw new WhatsCookingException("Stock must be a positive integer.");
                }
                return stockSize;
            } catch (NumberFormatException e) {
                throw new WhatsCookingException("Stock must be a positive integer.");
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
