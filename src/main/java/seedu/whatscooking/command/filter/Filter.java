package seedu.whatscooking.command.filter;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.command.SearchCommand;
import seedu.whatscooking.recipe.Recipe;

/** A condition used by {@link SearchCommand} to decide whether a recipe matches. */
public abstract class Filter {
    /** Tests a recipe against this filter. */
    public abstract boolean matches(Recipe recipe) throws WhatsCookingException;
}
