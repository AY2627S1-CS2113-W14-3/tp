package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;

class ParserTest {
    @Test
    public void parse_bye_returnsExitCommand() throws WhatsCookingException {
        Command command = Parser.parse("bye");
        // input:    "bye"
        // actual:   command.isExit()
        // expected: true
        assertTrue(command.isExit());
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        // input:    "fly"
        // expected: WhatsCookingException
        assertThrows(WhatsCookingException.class, () -> Parser.parse("fly"));
    }
}
