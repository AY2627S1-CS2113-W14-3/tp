package seedu.whatscooking;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.command.Command;
import seedu.whatscooking.command.Parser;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.storage.Storage;
import seedu.whatscooking.ui.Ui;

/**
 * Entry point of the WhatsCooking recipe manager application.
 * <p>
 * Runs the main loop: read a line of input, parse it into a {@link Command},
 * execute it against the shared {@link RecipeList}, and repeat until a
 * command signals exit. Every {@link WhatsCookingException} thrown while
 * parsing or executing a command is caught here in one place, so an invalid
 * command reports an error and the loop continues, rather than crashing the
 * application.
 * <p>
 * Recipes are loaded from disk once at startup and saved again after every
 * successful command. Saving centrally here, rather than inside individual
 * commands, means persistence covers every way the recipe list or a recipe
 * within it can change, including ones added later, without each command
 * needing to remember to trigger it.
 */
public class WhatsCooking {
    private static final Logger logger = AppLogger.getLogger(WhatsCooking.class);

    public static void main(String[] args) {
        logger.info("Starting WhatsCooking");

        Ui ui = new Ui();
        Storage storage = new Storage();
        RecipeList recipes = loadRecipes(storage, ui);
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(recipes, ui);
                storage.save(recipes);
                isExit = command.isExit();
            } catch (WhatsCookingException e) {
                logger.log(Level.FINE, "Reported to user: {0}", e.getMessage());
                ui.showError(e.getMessage());
            }
        }

        logger.info("Exiting WhatsCooking");
    }

    /**
     * Loads the recipes saved by a previous run, falling back to an empty
     * list if loading fails, so a missing or corrupted data file never stops
     * the app from starting.
     *
     * @param storage the storage to load from
     * @param ui used to tell the user loading failed, if it did
     * @return the loaded recipes, or an empty list if there was nothing to load or loading failed
     */
    private static RecipeList loadRecipes(Storage storage, Ui ui) {
        try {
            return storage.load();
        } catch (WhatsCookingException e) {
            logger.log(Level.WARNING, "Could not load saved recipes: {0}", e.getMessage());
            ui.showError("Could not load saved recipes, starting with an empty list. " + e.getMessage());
            return new RecipeList();
        }
    }
}
