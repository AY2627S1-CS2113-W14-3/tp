package seedu.whatscooking.command;

import java.util.logging.Level;
import java.util.logging.Logger;
import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Retrieves and displays one recipe by its 1-based index or by its title.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code retrieve INDEX} e.g. {@code retrieve 1}</li>
 *   <li>{@code retrieve TITLE} e.g. {@code retrieve Apple Pie}</li>
 * </ul>
 * Title matching is case-insensitive.
 */
public class RetrieveCommand extends Command {
    private static final Logger logger = AppLogger.getLogger(RetrieveCommand.class);
    private final String arguments;

    public RetrieveCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "RecipeList should not be null";
        assert ui != null : "Ui should not be null";

        String input = arguments.trim();
        logger.log(Level.FINE, "Executing retrieve with input \"{0}\"", input);

        if (input.isEmpty()) {
            logger.log(Level.FINE, "Rejected retrieve: empty argument");
            throw new WhatsCookingException("Please enter a recipe number or recipe name.");
        }

        if (isInteger(input)) {
            int index = Integer.parseInt(input);
            if (index <= 0) {
                logger.log(Level.FINE, "Rejected retrieve: non-positive index {0}", index);
                throw new WhatsCookingException("Recipe number must be positive.");
            }
            assert index >= 1 : "index should be at least 1 after validation";
            logger.log(Level.FINE, "Retrieving recipe at index {0}", index);
            ui.showRecipe(recipes, index);
            return;
        }

        Recipe recipe = recipes.getByName(input);
        assert recipe != null : "getByName should either return a recipe or throw";
        logger.log(Level.FINE, "Retrieved recipe \"{0}\" by name", recipe.getTitle());
        ui.showRecipe(recipe);
    }

    /**
     * Returns true if the given string consists only of digits.
     *
     * @param s the string to check
     * @return true if s is non-empty and all characters are digits
     */
    private boolean isInteger(String s) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
