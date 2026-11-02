package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.recipe.StockItem;

/**
 * Tests for {@link Parser}: every supported command word maps to the right
 * {@link Command} subclass, and unusable input is rejected rather than
 * silently mis-parsed.
 */
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
    public void parse_exit_returnsExitCommand() throws WhatsCookingException {
        Command command = Parser.parse("exit");
        // input:    "exit", the second word that ends the program
        // actual:   command.isExit()
        // expected: true
        assertTrue(command.isExit());
    }

    @Test
    public void parse_commandWithArguments_returnsMatchingCommand() throws WhatsCookingException {
        // input:    one line per supported command word, each with arguments
        // actual:   the Command subclass returned
        // expected: the subclass that handles that word
        assertInstanceOf(AddCommand.class, Parser.parse("add egg taco, snack"));
        assertInstanceOf(RetrieveCommand.class, Parser.parse("retrieve 1"));
        assertInstanceOf(SearchCommand.class, Parser.parse("search taco"));
        assertInstanceOf(ModifyCommand.class, Parser.parse("modify egg taco s/2"));
        assertInstanceOf(ViewCommand.class, Parser.parse("view"));
    }

    @Test
    public void parse_nonExitCommand_doesNotEndProgram() throws WhatsCookingException {
        Command command = Parser.parse("add egg taco");
        // input:    "add egg taco"
        // actual:   command.isExit()
        // expected: false, so the main loop keeps running
        assertFalse(command.isExit());
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        // input:    "fly"
        // expected: WhatsCookingException
        assertThrows(WhatsCookingException.class, () -> Parser.parse("fly"));
    }

    @Test
    public void parse_blankInput_throwsException() {
        // input:    an empty line, and a line of only spaces
        // expected: WhatsCookingException for both
        assertThrows(WhatsCookingException.class, () -> Parser.parse(""));
        assertThrows(WhatsCookingException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_searchUsesProvidedStockList() throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("2 eggs"),null));
        Recipe recipe = new Recipe("Omelette", "main");
        recipe.addIngredient(new Ingredient("2 eggs"));
        RecipeList recipes = new RecipeList();
        recipes.add(recipe);

        SearchCommand command = (SearchCommand) Parser.parse("search f/stock/1", stock);

        assertEquals(java.util.List.of(0), command.internalExecute(recipes));
    }
}
