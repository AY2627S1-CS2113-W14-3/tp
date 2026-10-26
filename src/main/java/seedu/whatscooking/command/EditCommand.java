package seedu.whatscooking.command;

import static seedu.whatscooking.command.RecipeInputParser.NOTE_PREFIX;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Changes the title, type and/or note of an existing recipe:
 * {@code edit INDEX [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE]}.
 * <p>
 * The parts can be given in any order, but each at most once. Every new value
 * is checked before anything changes, so a rejected edit leaves the recipe as it was.
 */
public class EditCommand extends Command {
    static final String TITLE_PREFIX = " title/";
    static final String TYPE_PREFIX = " type/";

    private static final Logger logger = AppLogger.getLogger(EditCommand.class);
    private static final String[] PREFIXES = {TITLE_PREFIX, TYPE_PREFIX, NOTE_PREFIX};

    private static final String MESSAGE_WRONG_FORMAT =
            "WRONG FORMAT! Edit requires \"edit INDEX [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE]\" "
                    + "with at least one part, e.g. edit 1 title/egg wrap type/lunch";
    private static final String MESSAGE_INVALID_INDEX =
            "The recipe number must be a positive whole number. Use view to see recipe numbers.";
    private static final String MESSAGE_REPEATED_PREFIX = "Each of title/, type/ and n/ can only be given once.";
    private static final String MESSAGE_DUPLICATE_TITLE =
            "A recipe called \"%s\" already exists. Choose a different title.";

    private final String arguments;

    /**
     * Creates an edit command for the given argument string.
     *
     * @param arguments everything the user typed after "edit"; never null
     */
    public EditCommand(String arguments) {
        assert arguments != null : "Parser should pass \"\" rather than null when edit has no arguments";
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "Recipe list should never be null";
        assert ui != null : "Ui should never be null";
        logger.log(Level.FINE, "Executing edit with arguments \"{0}\"", arguments);

        //Split into [index, everything else]
        String[] parts = arguments.trim().split(" ", 2);
        if (parts[0].isEmpty() || parts.length < 2) {
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }
        int index = parseIndex(parts[0]);
        Recipe recipe = recipes.get(index - 1);

        //Leading space lets a prefix match even when it is the first thing typed
        String fields = " " + parts[1];
        String newTitle = getValue(fields, TITLE_PREFIX);
        String newType = getValue(fields, TYPE_PREFIX);
        String newNote = getValue(fields, NOTE_PREFIX);

        //Check every new value first, so a rejected edit changes nothing
        if (newTitle != null) {
            newTitle = RecipeInputParser.parseTitle(newTitle);
            boolean isSameRecipe = newTitle.equalsIgnoreCase(recipe.getTitle().trim());
            if (!isSameRecipe && recipes.containsTitle(newTitle)) {
                logger.log(Level.INFO, "Edit rejected: duplicate title \"{0}\"", newTitle);
                throw new WhatsCookingException(String.format(MESSAGE_DUPLICATE_TITLE, newTitle));
            }
        }
        if (newType != null) {
            newType = RecipeInputParser.parseType(newType);
        }
        if (newNote != null) {
            newNote = RecipeInputParser.parseNote(newNote);
        }

        if (newTitle != null) {
            recipe.setTitle(newTitle);
        }
        if (newType != null) {
            recipe.setType(newType);
        }
        if (newNote != null) {
            recipe.setNote(newNote);
        }

        logger.log(Level.INFO, "Edited recipe {0}", index);
        ui.showMessage(buildResultMessage(index, recipe));
    }

    /**
     * Converts the 1-based recipe number typed by the user.
     *
     * @param text the recipe number as typed
     * @return the 1-based recipe number
     * @throws WhatsCookingException if the text is not a positive whole number
     */
    private int parseIndex(String text) throws WhatsCookingException {
        if (!text.matches("[0-9]+")) {
            throw new WhatsCookingException(MESSAGE_INVALID_INDEX);
        }
        try {
            int index = Integer.parseInt(text);
            if (index <= 0) {
                throw new WhatsCookingException(MESSAGE_INVALID_INDEX);
            }
            return index;
        } catch (NumberFormatException e) {
            //Only reached when the number is too large to fit in an int
            throw new WhatsCookingException(MESSAGE_INVALID_INDEX);
        }
    }

    /**
     * Returns the text typed after {@code prefix}, up to the next prefix or the end of the line.
     *
     * @param fields everything after the recipe number, starting with a space
     * @param prefix the prefix to look for, e.g. " title/"
     * @return the text after the prefix, or null if the prefix was not given
     * @throws WhatsCookingException if a prefix is repeated, no prefix is given,
     *     or there is extra text before the first prefix
     */
    private String getValue(String fields, String prefix) throws WhatsCookingException {
        List<Integer> starts = findPrefixStarts(fields);
        if (starts.isEmpty() || !fields.substring(0, starts.get(0)).isBlank()) {
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }

        int start = fields.indexOf(prefix);
        if (start == -1) {
            return null;
        }
        int valueStart = start + prefix.length();
        int valueEnd = fields.length();
        for (int otherStart : starts) {
            if (otherStart > start) {
                valueEnd = otherStart;
                break;
            }
        }
        return fields.substring(valueStart, valueEnd);
    }

    /**
     * Returns where each prefix starts in {@code fields}, in order.
     *
     * @throws WhatsCookingException if any prefix appears more than once
     */
    private List<Integer> findPrefixStarts(String fields) throws WhatsCookingException {
        List<Integer> starts = new ArrayList<>();
        for (String prefix : PREFIXES) {
            int start = fields.indexOf(prefix);
            if (start == -1) {
                continue;
            }
            if (fields.indexOf(prefix, start + 1) != -1) {
                throw new WhatsCookingException(MESSAGE_REPEATED_PREFIX);
            }
            starts.add(start);
        }
        starts.sort(Comparator.naturalOrder());
        return starts;
    }

    /** Returns the confirmation shown after a successful edit. */
    private String buildResultMessage(int index, Recipe recipe) {
        String message = "Updated recipe " + index + ":" + System.lineSeparator() + recipe;
        if (!recipe.getNote().isEmpty()) {
            message += System.lineSeparator() + "Note: " + recipe.getNote();
        }
        return message;
    }
}
