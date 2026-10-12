package seedu.whatscooking.ui;

import java.util.List;
import java.util.Scanner;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;

/**
 * Handles all console input and output for the application.
 * <p>
 * Centralising printing here means commands never call
 * {@code System.out}/{@code System.in} directly; they only ever talk to this
 * class. That keeps the display format (banners, dividers, error prefixes)
 * consistent and in one place, and means the format can change later without
 * touching every command.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner in;

    public Ui() {
        this.in = new Scanner(System.in);
    }

    public void showWelcome() {
        showLine();
        System.out.println("Welcome to WhatsCooking!");
        System.out.println("What recipe shall we manage today?");
        showLine();
    }

    public void showGoodbye() {
        showLine();
        System.out.println("Bye! Happy cooking.");
        showLine();
    }

    /**
     * Reads one line of raw user input, to be handed to the parser.
     *
     * @return the line typed by the user, unmodified
     */
    public String readCommand() {
        return in.nextLine();
    }

    public void showLine() {
        System.out.println(DIVIDER);
    }

    public void showError(String message) {
        showLine();
        System.out.println("Error: " + message);
        showLine();
    }

    public void showMessage(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Prints the full detail of a single recipe: title, type, serving size,
     * every ingredient, every step, and the note if one is set.
     *
     * @param recipe the recipe to display
     */
    public void showRecipe(Recipe recipe) {
        showLine();
        System.out.println(recipe);
        printNumberedList("Ingredients:", recipe.getIngredients());
        printNumberedList("Steps:", recipe.getSteps());
        if (!recipe.getNote().isEmpty()) {
            System.out.println("Note: " + recipe.getNote());
        }
        showLine();
    }

    /**
     * Looks up the recipe at the given 1-based position and prints its full
     * detail, same as {@link #showRecipe(Recipe)}. This is the overload a
     * command should call when it only has a recipe's display number (what
     * the user typed, e.g. "retrieve 2") rather than the {@link Recipe}
     * object itself.
     *
     * @param recipes the list to look the recipe up in
     * @param index   1-based position of the recipe, as shown to the user
     * @throws WhatsCookingException if index does not correspond to a recipe
     */
    public void showRecipe(RecipeList recipes, int index) throws WhatsCookingException {
        assert index > 0 : "Display numbering is 1-based, so commands must reject indices below 1 before calling";

        showRecipe(recipes.get(index - 1));
    }

    /**
     * Prints a numbered summary of every recipe in the given list. Each line
     * shows only the recipe's {@link Recipe#toString()} summary, not its
     * full detail; use {@link #showRecipe(Recipe)} to show one recipe in
     * full.
     *
     * @param recipes the full list of recipes to display
     */
    public void listRecipes(RecipeList recipes) throws WhatsCookingException {
        showLine();
        if (recipes.isEmpty()) {
            System.out.println("No recipes found.");
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                System.out.println((i + 1) + ". " + recipes.get(i));
            }
        }
        showLine();
    }

    /**
     * Prints a heading followed by each item on its own indented line,
     * numbered from 1. Shared by the ingredient and step lists so the two
     * are always formatted identically.
     *
     * @param heading the label printed above the items, e.g. "Ingredients:"
     * @param items   the items to print, in display order
     */
    private void printNumberedList(String heading, List<?> items) {
        System.out.println(heading);
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
    }
}
