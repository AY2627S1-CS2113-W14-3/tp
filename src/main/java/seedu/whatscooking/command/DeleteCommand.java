package seedu.whatscooking.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Deletes a recipe by its 1-based index or by its title.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code delete INDEX} e.g. {@code delete 1}</li>
 *   <li>{@code delete TITLE} e.g. {@code delete Apple Pie}</li>
 * </ul>
 * Title matching is case-insensitive.
 */
public class DeleteCommand extends Command {
    private static final Logger logger = AppLogger.getLogger(DeleteCommand.class);
    private final String arguments;

    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
<<<<<<< HEAD
=======
        assert recipes != null : "RecipeList should not be null";
        assert ui != null : "Ui should not be null";

>>>>>>> upstream/master
        String input = arguments.trim();
        logger.log(Level.FINE, "Executing delete with input \"{0}\"", input);

        if (input.isEmpty()) {
            logger.log(Level.FINE, "Rejected delete: empty argument");
            throw new WhatsCookingException(
                    "Please enter a recipe number or recipe name.");
        }

        Recipe deleted;
        if (isInteger(input)) {
            int index = Integer.parseInt(input);
            if (index <= 0) {
                logger.log(Level.FINE, "Rejected delete: non-positive index {0}", index);
                throw new WhatsCookingException(
                        "Recipe number must be positive.");
            }
<<<<<<< HEAD
=======
            assert index >= 1 : "index should be at least 1 after validation";
>>>>>>> upstream/master
            deleted = recipes.delete(index - 1);
        } else {
            deleted = recipes.deleteByName(input);
        }
<<<<<<< HEAD
=======

        assert deleted != null : "Deleted recipe should not be null";
>>>>>>> upstream/master
        logger.log(Level.FINE, "Deleted recipe \"{0}\"; list now holds {1}",
                new Object[]{deleted.getTitle(), recipes.size()});
        ui.showMessage("Deleted: " + deleted);
    }

    /**
     * Returns true if the given string consists only of digits.
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
