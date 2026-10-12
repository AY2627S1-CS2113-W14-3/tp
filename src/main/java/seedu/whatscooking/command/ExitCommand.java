package seedu.whatscooking.command;

import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Ends the application. Triggered by the "bye" or "exit" command word.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(RecipeList recipes, Ui ui) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
