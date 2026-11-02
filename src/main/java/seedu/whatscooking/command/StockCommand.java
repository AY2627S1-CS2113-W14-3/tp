package seedu.whatscooking.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockItem;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.ui.Ui;

/**
 * Manages the home ingredient stock (add, remove, list).
 */
public class StockCommand extends Command {
    private static final String EXPIRY_PREFIX = " by/";
    private final String arguments;
    private final StockList stockList;

    /**
     * Constructs a StockCommand.
     *
     * @param arguments the raw arguments after the "stock" command word
     * @param stockList the stock list to operate on
     */
    public StockCommand(String arguments, StockList stockList) {
        this.arguments = arguments;
        this.stockList = stockList;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert stockList != null : "StockList should not be null";

        String[] parts = arguments.trim().split(" ", 2);
        String action = parts[0];
        String data = parts.length > 1 ? parts[1].trim() : "";

        switch (action) {
        case "add":
            if (data.isEmpty()) {
                throw new WhatsCookingException(
                        "Please provide an ingredient to add. e.g., stock add 3 eggs");
            }
            handleAdd(data, ui);
            break;

        case "remove":
            if (data.isEmpty()) {
                throw new WhatsCookingException(
                        "Please provide an ingredient to remove. e.g., stock remove 3 eggs");
            }
            String removeData = data.contains(EXPIRY_PREFIX) ? data.split(EXPIRY_PREFIX, 2)[0].trim() : data;
            try {
                stockList.remove(new StockItem(new Ingredient(removeData), null));
            } catch (IllegalArgumentException e) {
                throw new WhatsCookingException("Invalid ingredient format: " + e.getMessage());
            }
            ui.showMessage("Removed from stock: " + removeData);
            break;

        case "list":
            if (!data.isEmpty()) {
                throw new WhatsCookingException(
                        "The 'list' action does not take any extra arguments.");
            }
            ui.showStockList(stockList.getAll());
            break;

        default:
            throw new WhatsCookingException(
                    "Unknown stock action. Use: stock add, stock remove, or stock list");
        }
    }

    private void handleAdd(String data, Ui ui) throws WhatsCookingException {
        LocalDate expiryDate = null;
        String ingredientText = data;

        // Check if user provided an expiry date
        if (data.contains(EXPIRY_PREFIX)) {
            String[] split = data.split(EXPIRY_PREFIX, 2);
            ingredientText = split[0].trim();
            String dateText = split[1].trim();
            try {
                expiryDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException e) {
                throw new WhatsCookingException("Invalid date format. Please use YYYY-MM-DD (e.g., by/2026-10-31).");
            }
        }
        try {
            Ingredient ingredient = new Ingredient(ingredientText);
            StockItem item = new StockItem(ingredient, expiryDate);
            stockList.add(item);

            String dateMsg = expiryDate == null ? "" : " (Expires: " + expiryDate + ")";
            ui.showMessage("Added to stock: " + ingredientText + dateMsg);
        } catch (IllegalArgumentException e) {
            throw new WhatsCookingException("Invalid ingredient format: " + e.getMessage());
        }
    }
}
