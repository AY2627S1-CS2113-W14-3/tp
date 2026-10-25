package seedu.whatscooking.command.filter;

import java.util.ArrayList;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.StockList;

/** Creates filter objects from search filter syntax. */
public final class FilterFactory {
    private FilterFactory() {
        // Utility class.
    }

    /** Parses and creates all whitespace-separated filters. */
    public static ArrayList<Filter> createFilters(String arguments, StockList stockList)
            throws WhatsCookingException {
        if (arguments.trim().isEmpty()) {
            throw new WhatsCookingException(
                    "Please provide at least one search filter in the form f/{field}/{keyword}.");
        }
        ArrayList<Filter> filters = new ArrayList<>();
        for (String filterText : arguments.trim().split("\\s+")) {
            filters.add(createFilter(filterText, stockList));
        }
        return filters;
    }

    private static Filter createFilter(String filterText, StockList stockList)
            throws WhatsCookingException {
        if (!filterText.startsWith("f/")) {
            throw invalidFormat();
        }
        String[] parts = filterText.split("/", 3);
        if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
            throw invalidFormat();
        }
        switch (parts[1]) {
        case "name":
            return new NameFilter(parts[2]);
        case "ingredient":
            return new IngredientFilter(parts[2]);
        case "stock":
            return new StockFilter(parseServingSize(parts[2]), stockList);
        default:
            throw new WhatsCookingException("Search filter must be name, ingredient, or stock.");
        }
    }

    private static WhatsCookingException invalidFormat() {
        return new WhatsCookingException("Search filters must use f/{name/ingredient/stock}/{keyword}.");
    }

    private static int parseServingSize(String text) throws WhatsCookingException {
        try {
            int servingSize = Integer.parseInt(text);
            if (servingSize <= 0) {
                throw new NumberFormatException();
            }
            return servingSize;
        } catch (NumberFormatException e) {
            throw new WhatsCookingException("Stock must be a positive integer.");
        }
    }
}
