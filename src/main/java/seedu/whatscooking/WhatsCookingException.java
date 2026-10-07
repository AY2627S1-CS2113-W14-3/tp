package seedu.whatscooking;

/**
 * Signals an error caused by bad user input or an invalid operation on the
 * recipe list, such as an unknown command word, a malformed argument, or a
 * recipe index that is out of range.
 * <p>
 * This is a <em>checked</em> exception (it extends {@link Exception} rather
 * than {@code RuntimeException}) so that the compiler forces every command to
 * declare the failures it can produce. The main loop catches it in one place,
 * reports the message to the user, and carries on reading the next command.
 * Errors that represent bugs rather than user mistakes should not use this
 * class &mdash; let those surface as ordinary unchecked exceptions.
 * <p>
 * The message passed to the constructor is shown directly to the user, so it
 * should be written for them rather than for a developer. Prefer
 * {@code "Recipe 7 does not exist; you have 3 recipes."} over
 * {@code "IndexOutOfBounds: 7"}.
 */
public class WhatsCookingException extends Exception {
    /**
     * Constructs an exception carrying a user-facing explanation of what went
     * wrong.
     *
     * @param message the message to display to the user
     */
    public WhatsCookingException(String message) {
        super(message);
    }
}
