package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Represents one user command, e.g. "add", "retrieve", "search", "bye".
 * <p>
 * Each supported command is its own subclass implementing
 * {@link #execute(RecipeList, Ui)}, rather than one class branching on the
 * command word internally. That way, adding a new command means adding a new
 * file instead of editing a shared switch statement, which keeps multiple
 * people's command work from colliding in the same file.
 */
public abstract class Command {
    /**
     * Carries out this command: reads and/or mutates {@code recipes}, and
     * reports results to the user via {@code ui}.
     *
     * @param recipes the recipe list this command operates on
     * @param ui       used to display output to the user
     * @throws WhatsCookingException if the command cannot be completed, e.g.
     *     an invalid index or malformed arguments
     */
    public abstract void execute(RecipeList recipes, Ui ui) throws WhatsCookingException;

    /**
     * Whether this command should end the main loop after executing.
     * Overridden to return true only by {@link ExitCommand}.
     *
     * @return true if the application should exit after this command
     */
    public boolean isExit() {
        return false;
    }
}
