package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.ui.Ui;

/**
 * Manages the home ingredient stock (add, remove, list).
 */
public class StockCommand extends Command {
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
            try {
                stockList.add(new Ingredient(data));
            } catch (IllegalArgumentException e) {
                throw new WhatsCookingException("Invalid ingredient format: " + e.getMessage());
            }
            ui.showMessage("Added to stock: " + data);
            break;

        case "remove":
            if (data.isEmpty()) {
                throw new WhatsCookingException(
                        "Please provide an ingredient to remove. e.g., stock remove 3 eggs");
            }
            try {
                stockList.remove(new Ingredient(data));
            } catch (IllegalArgumentException e) {
                throw new WhatsCookingException("Invalid ingredient format: " + e.getMessage());
            }
            ui.showMessage("Removed from stock: " + data);
            break;

        case "list":
            ui.showStockList(stockList.getAll());
            break;

        default:
            throw new WhatsCookingException(
                    "Unknown stock action. Use: stock add, stock remove, or stock list");
        }
    }
}
