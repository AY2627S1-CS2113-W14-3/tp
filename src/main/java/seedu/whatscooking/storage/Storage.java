package seedu.whatscooking.storage;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;

/**
 * Saves the recipe list to a plain-text file and loads it back on the next run.
 * <p>
 * Each recipe is written as a fixed sequence of lines (title, type, serving
 * size, ingredients, steps, note) with no delimiter-escaping needed, since
 * every field is console input and therefore never itself contains a
 * newline. Recipes are written back-to-back with no separator; the line
 * counts embedded in the format (ingredient count, step count) are enough to
 * tell where one recipe ends and the next begins.
 */
public class Storage {
    private static final String DEFAULT_FILE_PATH = "data/whatscooking.txt";

    private static final Logger logger = AppLogger.getLogger(Storage.class);

    private final Path filePath;

    /** Creates storage backed by the default save location, {@value #DEFAULT_FILE_PATH}. */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage backed by a specific file, mainly so tests can point it
     * at a temporary file instead of the real save location.
     *
     * @param filePath path of the file to read from and write to
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Storage needs a file path to read and write";
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads the recipes saved by a previous run.
     *
     * @return the saved recipes, or an empty list if no data file exists yet
     * @throws WhatsCookingException if the data file exists but cannot be
     *     read, or its contents do not match the expected format
     */
    public RecipeList load() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        if (!Files.exists(filePath)) {
            logger.info("No data file at " + filePath + "; starting with an empty list");
            return recipes;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new WhatsCookingException("Could not read data file: " + e.getMessage());
        }

        try {
            int index = 0;
            while (index < lines.size()) {
                index = readRecipe(lines, index, recipes);
            }
        } catch (RuntimeException e) {
            //NumberFormatException or IndexOutOfBoundsException means the file does not match our format
            throw new WhatsCookingException("Data file is corrupted: " + e.getMessage());
        }

        logger.log(Level.INFO, "Loaded {0} recipe(s) from {1}", new Object[]{recipes.size(), filePath});
        return recipes;
    }

    /**
     * Writes the full recipe list to the data file, overwriting whatever was
     * saved before. Creates the parent directory if it does not exist yet.
     *
     * @param recipes the recipes to save
     * @throws WhatsCookingException if the file cannot be written
     */
    public void save(RecipeList recipes) throws WhatsCookingException {
        assert recipes != null : "Recipe list should never be null";

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            writeRecipe(lines, recipes.get(i));
        }

        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new WhatsCookingException("Could not save recipes to file: " + e.getMessage());
        }

        logger.log(Level.FINE, "Saved {0} recipe(s) to {1}", new Object[]{recipes.size(), filePath});
    }

    /** Appends one recipe's fields to {@code lines} in the order {@link #readRecipe} expects them. */
    private void writeRecipe(List<String> lines, Recipe recipe) {
        lines.add(recipe.getTitle());
        lines.add(recipe.getType());
        lines.add(String.valueOf(recipe.getServingSize()));

        lines.add(String.valueOf(recipe.getIngredients().size()));
        for (Ingredient ingredient : recipe.getIngredients()) {
            lines.add(formatQuantity(ingredient.getQuantity()));
            lines.add(ingredient.getUnit() == null ? "" : ingredient.getUnit());
            lines.add(ingredient.getName());
        }

        lines.add(String.valueOf(recipe.getSteps().size()));
        lines.addAll(recipe.getSteps());

        lines.add(recipe.getNote());
    }

    /**
     * Reads one recipe starting at {@code lines.get(index)} and adds it to
     * {@code recipes}.
     *
     * @return the index of the line just after this recipe, i.e. where the next one starts
     */
    private int readRecipe(List<String> lines, int index, RecipeList recipes) throws WhatsCookingException {
        String title = lines.get(index++);
        String type = lines.get(index++);
        int servingSize = Integer.parseInt(lines.get(index++));

        Recipe recipe = new Recipe(title, type);
        recipe.setServingSize(servingSize);

        int ingredientCount = Integer.parseInt(lines.get(index++));
        for (int i = 0; i < ingredientCount; i++) {
            double quantity = Double.parseDouble(lines.get(index++));
            String unit = lines.get(index++);
            String name = lines.get(index++);
            recipe.addIngredient(new Ingredient(quantity, unit, name));
        }

        int stepCount = Integer.parseInt(lines.get(index++));
        for (int i = 0; i < stepCount; i++) {
            recipe.addStep(lines.get(index++));
        }

        recipe.setNote(lines.get(index++));

        recipes.add(recipe);
        return index;
    }

    /** Formats a quantity the same way {@code AddCommand} would re-parse it: a plain decimal, no trailing zeroes. */
    private String formatQuantity(double quantity) {
        return BigDecimal.valueOf(quantity).stripTrailingZeros().toPlainString();
    }
}
