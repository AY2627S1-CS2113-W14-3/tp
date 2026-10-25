package seedu.whatscooking.command.filter;

import seedu.whatscooking.recipe.Recipe;

/** Matches recipes whose title contains a keyword, ignoring case. */
public class NameFilter extends Filter {
    private final String keyword;

    public NameFilter(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean matches(Recipe recipe) {
        return recipe.getTitle().toLowerCase().contains(keyword.toLowerCase());
    }
}
