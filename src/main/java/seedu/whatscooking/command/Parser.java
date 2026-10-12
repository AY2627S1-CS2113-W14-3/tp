package seedu.whatscooking.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;

/**
 * Turns one line of raw user input into a {@link Command} object.
 * <p>
 * Input is split into a command word (the first token) and the remaining
 * arguments (everything after it). Each command word maps to exactly one
 * {@link Command} subclass; unrecognised command words result in a
 * {@link WhatsCookingException} rather than a crash.
 */
public class Parser {
    private static final Logger logger = AppLogger.getLogger(Parser.class);

    private Parser() {
        // Utility class: parse() is static, so instances are never needed.
    }

    /**
     * Parses one full line of user input into the {@link Command} it
     * represents.
     *
     * @param fullCommand the raw line typed by the user
     * @return the command to execute
     * @throws WhatsCookingException if the line is blank, or the command word
     *     is not recognised
     */
    public static Command parse(String fullCommand) throws WhatsCookingException {
        assert fullCommand != null : "Ui.readCommand() never returns null, so parse() should never be given one";

        String[] parts = fullCommand.trim().split(" ", 2);
        String commandWord = parts[0];
        String arguments = parts.length > 1 ? parts[1] : ""; //parts[1] if parts length > 1, else ""

        //A blank line has no command word, so report that rather than "I don't recognise the command: "
        if (commandWord.isEmpty()) {
            throw new WhatsCookingException("Please enter a command.");
        }

        logger.log(Level.FINE, "Parsed command word \"{0}\"", commandWord);

        switch (commandWord) {
        case "bye":
        case "exit":
            return new ExitCommand();
        case "add":
            return new AddCommand(arguments);      // Person B
        case "retrieve":
            return new RetrieveCommand(arguments);  // Person C
        case "search":
            return new SearchCommand(arguments);    // Person D
        default:
            logger.log(Level.WARNING, "Unrecognised command word \"{0}\"", commandWord);
            throw new WhatsCookingException("I don't recognise the command: " + commandWord);
        }
    }
}
