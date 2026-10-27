# Lim Sin - Project Portfolio Page

## Overview

What's Cooking is a desktop recipe manager for home cooks who prefer typing. Users store recipes with their
ingredients, steps and notes, find them quickly, resize them for a different number of servings, check possible
allergens and keep track of the ingredients they have at home, all through a command-line interface.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=wunicorn21&breakdown=true)

* **New Feature**: Added the ability to add a recipe in one command (`add`).
    * What it does: creates a recipe from a single line containing a title, an optional type, serving size,
      ingredients, steps and a note, e.g. `add egg taco, snack s/2 i/3 eggs st/Scramble egg n/Serve hot`.
    * Justification: `add` is how every recipe enters the app, so every other feature (`retrieve`, `modify`,
      `search`, allergen warnings, saving) depends on it.
    * Highlights: every part of the line is checked before anything is saved, so an invalid command never leaves a
      half-built recipe in the list. The parser keeps prefixes such as `i/` from being matched inside words like
      `wasabi/`, and reports which part is wrong and how to fix it.

* **New Feature**: Added the ability to edit an existing recipe (`edit`).
    * What it does: changes a recipe's title, type and/or note by its number, e.g.
      `edit 1 title/egg wrap n/Add chilli`. The parts can be given in any order.
    * Justification: users previously had to delete and re-add a recipe to fix a typo or update a note.
    * Highlights: every new value is checked before any change is applied, so a rejected `edit` leaves the recipe
      exactly as it was. A new title cannot match another recipe's title, but changing only the letter case of a
      recipe's own title is allowed.

* **Enhancements to existing features**:
    * Blocked duplicate recipe titles in `add`, ignoring letter case and surrounding spaces. This also covers
      recipes loaded from a previous session. (Pull request [\#34](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/34))
    * Added an optional serving size (`s/`) to `add`, which `modify` uses as the base when scaling ingredients.
      (Pull request [\#34](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/34))
    * Added note support and stricter input validation to `add`.
      (Pull request [\#21](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/21))
    * Refactored the input checks shared by `add` and `edit` into a single utility class, `RecipeInputParser`, so
      both commands accept the same values and show the same error messages.
      (Pull requests [\#34](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/34),
      [\#46](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/46))
    * Wrote tests for all of the above: `AddCommandTest`, `EditCommandTest`, `RecipeListTest` and new cases in the
      text UI test.

* **Documentation**:
    * User Guide:
        * Wrote the sections for `add` and `edit`, including formats, rules, examples, expected output and errors.
        * Added `add` and `edit` to the Command Summary.
    * Developer Guide:
        * Wrote the section "Recipe creation and editing: `add` and `edit`", covering the design, the command flow and
          the alternatives considered.
        * Added three UML diagrams: a class diagram (Figure 7), a sequence diagram for `add` (Figure 8) and an
          activity diagram for `edit` (Figure 9).
        * Added user stories, non-functional requirements, glossary terms and manual testing instructions for `add`
          and `edit`.

* **Project management**:
    * Planned and tracked my v1.0 and v2.0 work with issues and milestones
      ([\#19](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/19),
      [\#32](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/32),
      [\#33](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/33)).
    * Merged teammates' pull requests after review, e.g.
      [\#39](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/39).

* **Community**:
    * Reviewed teammates' pull requests, with non-trivial review comments on
      [\#18](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/18),
      [\#36](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/36) and
      [\#39](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/39), and approvals on
      [\#2](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/2),
      [\#3](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/3),
      [\#25](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/25) and
      [\#30](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/30).
