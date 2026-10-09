package seedu.whatscooking;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.command.Command;
import seedu.whatscooking.command.Parser;
import seedu.whatscooking.recipe.RecipeList;
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
 */
public class WhatsCooking {
    private static final Logger logger = AppLogger.getLogger(WhatsCooking.class);

    public static void main(String[] args) {
        logger.info("Starting WhatsCooking");

        Ui ui = new Ui();
        RecipeList recipes = new RecipeList();
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(recipes, ui);
                isExit = command.isExit();
            } catch (WhatsCookingException e) {
                logger.log(Level.FINE, "Reported to user: {0}", e.getMessage());
                ui.showError(e.getMessage());
            }
        }

        logger.info("Exiting WhatsCooking");
    }
}
