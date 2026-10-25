package seedu.whatscooking.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.allergen.Allergen;
import seedu.whatscooking.allergen.AllergenChecker;
import seedu.whatscooking.recipe.Ingredient;
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
     * every ingredient, every step, the note if one is set, and any possible allergens.
     *
     * @param recipe the recipe to display
     */
    public void showRecipe(Recipe recipe) {
        showLine();
        System.out.println(recipe);
        List<String> ingredientDescriptions = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientDescriptions.add(ingredient.getDisplayDescription());
        }
        printNumberedList("Ingredients:", ingredientDescriptions);
        printNumberedList("Steps:", recipe.getSteps());
        if (!recipe.getNote().isEmpty()) {
            System.out.println("Note: " + recipe.getNote());
        }
        String allergenWarning = getAllergenWarning(recipe);
        if (!allergenWarning.isEmpty()) {
            System.out.println(allergenWarning);
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
     * Shows a separate warning after a recipe is added, if its ingredient names match the built-in list.
     * Prints nothing when no keywords match; absence of a warning does not mean the recipe is allergen-free.
     *
     * @param recipe the successfully added recipe
     */
    public void showAllergenWarnings(Recipe recipe) {
        String warning = getAllergenWarning(recipe);
        if (!warning.isEmpty()) {
            showLine();
            System.out.println(warning);
            showLine();
        }
    }

    /** Builds the same warning for both add and retrieve, without storing derived allergen flags. */
    private String getAllergenWarning(Recipe recipe) {
        assert recipe != null : "A recipe must be supplied before displaying allergen warnings";
        StringJoiner names = new StringJoiner(", ");
        for (Allergen allergen : AllergenChecker.findAllergens(recipe.getIngredients())) {
            names.add(allergen.getDisplayName());
        }
        if (names.length() == 0) {
            return "";
        }
        return "Possible allergens: " + names + ". Check ingredient labels.";
    }

    /**
     * Shows the updated serving size and ingredient amounts after a successful resize.
     *
     * @param recipe the resized recipe
     * @param previousServingSize the number of servings before the change
     */
    public void showModifiedRecipe(Recipe recipe, int previousServingSize) {
        showLine();
        System.out.println("Serving size of " + recipe.getTitle() + " has been updated from "
                + previousServingSize + " to " + recipe.getServingSize() + ".");
        System.out.println("Updated ingredients:");
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            System.out.println((i + 1) + ". " + recipe.getIngredients().get(i).getDisplayDescription());
        }
        showLine();
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

    /**
     * Prints the list of ingredients currently available in the home stock.
     * Displays a friendly message if the stock is empty.
     *
     * @param stock the list of ingredients to display
     */
    public void showStockList(List<Ingredient> stock) {
        showLine();
        if (stock.isEmpty()) {
            System.out.println("Your home stock is empty.");
        } else {
            System.out.println("Current Home Stock:");
            for (Ingredient item : stock) {
                System.out.println("- " + item.getDisplayDescription());
            }
        }
        showLine();
    }
}
