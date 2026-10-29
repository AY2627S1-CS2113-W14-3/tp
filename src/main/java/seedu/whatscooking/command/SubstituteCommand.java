package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.allergen.SubstitutionAdvisor;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Shows possible ingredient alternatives for {@code substitute RECIPE_NAME}.
 * Names are resolved as complete titles, including numeric titles, rather than recipe indices.
 * The command only displays advice; choosing and applying a replacement remains a user decision.
 */
public class SubstituteCommand extends Command {
    private final String arguments;

    /** Stores the text after the command word; validation happens when the command executes. */
    public SubstituteCommand(String arguments) {
        assert arguments != null : "Parser must supply an empty string rather than null";
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "Recipe list must be available";
        assert ui != null : "Ui must be available";
        String name = arguments.trim();
        if (name.isEmpty()) {
            throw new WhatsCookingException("Usage: substitute RECIPE_NAME. Use the full recipe name.");
        }
        Recipe recipe = recipes.getByName(name);
        ui.showSubstitutionSuggestions(recipe, SubstitutionAdvisor.suggest(recipe.getIngredients()));
    }
}
