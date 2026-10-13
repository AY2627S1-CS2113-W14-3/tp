package seedu.whatscooking.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Changes a recipe's serving size and scales its ingredient quantities proportionally.
 * Selects a recipe by its full name, without regard to case.
 */
public class ModifyCommand extends Command {
    private static final Logger logger = AppLogger.getLogger(ModifyCommand.class);
    private static final String MESSAGE_WRONG_FORMAT =
            "Use: modify RECIPE_NAME s/NEW_SERVING_SIZE, e.g. modify egg taco s/2";
    private static final String MESSAGE_INVALID_SIZE =
            "Serving size must be a positive integer.";

    private final String arguments;

    /**
     * Stores the recipe name and serving-size argument for execution.
     *
     * @param arguments the text after the command word
     */
    public ModifyCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "The application must supply its recipe list";
        assert ui != null : "The application must supply its UI";

        if (arguments == null || arguments.isBlank()) {
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }
        String[] parts = arguments.strip().split("\\s+s/", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }

        String recipeName = parts[0].strip();
        int newServingSize = parseServingSize(parts[1].strip());
        Recipe recipe;
        try {
            recipe = recipes.getByName(recipeName);
        } catch (WhatsCookingException e) {
            throw new WhatsCookingException("Recipe \"" + recipeName + "\" could not be found.");
        }
        int previousServingSize = recipe.getServingSize();
        if (newServingSize == previousServingSize) {
            ui.showMessage(recipe.getTitle() + " already has a serving size of " + newServingSize
                    + ". No changes were made.");
            return;
        }
        try {
            recipe.scaleToServingSize(newServingSize);
        } catch (IllegalArgumentException e) {
            throw new WhatsCookingException(e.getMessage());
        }

        logger.log(Level.FINE, "Scaled recipe \"{0}\" from {1} to {2} servings",
                new Object[]{recipe.getTitle(), previousServingSize, newServingSize});
        ui.showModifiedRecipe(recipe, previousServingSize);
    }

    /**
     * Parses a positive integer and converts malformed or overflowing input to a user-facing error.
     */
    private static int parseServingSize(String text) throws WhatsCookingException {
        if (!text.matches("[0-9]+")) {
            throw new WhatsCookingException(MESSAGE_INVALID_SIZE);
        }
        try {
            int servingSize = Integer.parseInt(text);
            if (servingSize <= 0) {
                throw new WhatsCookingException(MESSAGE_INVALID_SIZE);
            }
            return servingSize;
        } catch (NumberFormatException e) {
            throw new WhatsCookingException(MESSAGE_INVALID_SIZE);
        }
    }
}
