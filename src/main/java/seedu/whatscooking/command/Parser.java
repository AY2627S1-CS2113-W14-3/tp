package seedu.whatscooking.command;

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
    /**
     * Parses one full line of user input into the {@link Command} it
     * represents.
     *
     * @param fullCommand the raw line typed by the user
     * @return the command to execute
     * @throws WhatsCookingException if the command word is not recognised
     */
    public static Command parse(String fullCommand) throws WhatsCookingException {
        String[] parts = fullCommand.trim().split(" ", 2);
        String commandWord = parts[0];
        String arguments = parts.length > 1 ? parts[1] : ""; //parts[1] if parts length > 1, else ""

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
            throw new WhatsCookingException("I don't recognise the command: " + commandWord);
        }
    }
}
