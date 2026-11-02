# Lim Sin - Project Portfolio Page

## Overview

What's Cooking is a desktop recipe manager for home cooks who prefer typing. Users store recipes with their
ingredients, steps and notes, find them quickly, resize them for a different number of servings, check possible
allergens and keep track of the ingredients they have at home, all through a command-line interface.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=wunicorn21&breakdown=true)

* **Enhancements implemented**:
    * **New feature: add a recipe in one command (`add`).**
        * What it does: creates a recipe from one line with a title, optional type, serving size, ingredients,
          steps and note, e.g. `add egg taco, snack s/2 i/3 eggs st/Scramble egg n/Serve hot`.
        * Justification: every recipe enters the app through `add`, so `retrieve`, `modify`, `search`, allergen
          warnings and saving all depend on it.
        * Every part is checked before anything is saved, so an invalid command never leaves a
          half-built recipe; prefixes such as `i/` are not mistaken inside words like `wasabi/`; each error says
          what to fix.
    * **New feature: edit an existing recipe (`edit`).**
        * What it does: changes a recipe's title, type, note, one ingredient and/or one step, choosing the recipe
          by number or full name, with parts in any order,
          e.g. `edit egg taco i/2 5 corn tortillas st/1 Whisk the eggs`.
        * Justification: users previously had to delete and re-add a recipe to fix a typo or update it.
        * Every new value is checked first and the changes are applied only together, so a rejected
          `edit` never half-changes a recipe. It reuses `add`'s checks, so both commands accept exactly the same
          values.
    * **Enhancements to existing features:**
        * Blocked duplicate titles, ignoring case and extra spaces, including recipes loaded from a previous
          session, and added an optional serving size (`s/`) that `modify` scales from.
          ([\#34](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/34))
        * Moved the checks shared by `add` and `edit` into one utility class, `RecipeInputParser`.
          ([\#34](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/34),
          [\#46](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/46))
        * v2.1: extended `edit` to names, ingredients and steps; fixed extra-space duplicates, the `(, serves 1)`
          display, multi-word types and digits-only titles that clashed with recipe indexes.
          ([\#63](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/63))
        * Wrote the tests for all of the above (`AddCommandTest`, `EditCommandTest`, `RecipeListTest`,
          `RecipeTest` and text UI test cases).

* **Contributions to the UG**:
    * Wrote the `add` and `edit` sections (formats, rules, examples, expected output and errors) and their
      Command Summary entries.
    * Wrote the introduction and "Notes about the command format", replacing the template placeholders.

* **Contributions to the DG**:
    * Wrote the design subsection "Recipe input design" and the implementation subsection "Recipe creation and
      editing: `add` and `edit`" (flow, validation, alternatives considered and automated tests).
    * Added three UML diagrams: a class diagram of the recipe input classes, a sequence diagram for `add` and an
      activity diagram for `edit` ([\#57](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/57),
      updated in [\#63](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/63)).
    * Added the user stories, non-functional requirements, glossary terms and manual testing instructions for
      `add` and `edit`.

* **Contributions to team-based tasks**:
    * Planned and tracked my work with issues and milestones from v1.0 to v2.1
      ([\#19](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/19),
      [\#32](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/32),
      [\#33](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/33),
      [\#61](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/61),
      [\#62](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/62)).
    * Merged teammates' PRs after review
      ([\#39](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/39),
      [\#50](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/50),
      [\#53](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/53),
      [\#56](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/56)).

* **Review/mentoring contributions**:
    * Reviewed teammates' PRs with comments on
      [\#18](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/18),
      [\#36](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/36),
      [\#39](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/39) and
      [\#50](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/50) (DG review), and approved
      [\#2](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/2),
      [\#3](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/3),
      [\#25](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/25) and
      [\#30](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/30).

* **Contributions beyond the project team**:
    * Reviewed another team's Developer Guide (CS2113-W10-4, Jaweeper):
      [tp\#4](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/4).
    * Gave code review comments on classmates' iPs:
      [ip\#52](https://github.com/NUS-CS2113-AY2627-S1/ip/pull/52),
      [ip\#97](https://github.com/NUS-CS2113-AY2627-S1/ip/pull/97).
    *     * Updated the README, website landing page and About Us page (team photos, portfolio links), and
      removed leftover template files ([\#65](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/65)).