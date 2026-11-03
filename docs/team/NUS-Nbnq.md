# Nathan Bong - Project Portfolio Page

## Overview

WhatsCooking is a command-line recipe manager for home cooks who prefer typing. Users store recipes with their
ingredients, steps and notes, find them quickly, resize them for a different number of servings, check possible
allergens and keep track of the ingredients they have at home.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=NUS-nbnq&breakdown=true)

* **Enhancements implemented**:
    * **New feature: Searching through recipes.**
      (Pull request [\#36](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/30))
        * What it does: Searches the recipe collection using any combination of name,
          ingredient, recipe type, serving-size and home-stock filters.
        * Justification: As the collection grows, manually checking every recipe is
          slow and makes it difficult to find recipes that match what the user has
          available at home.
        * Highlights: Refactored filters behind a common `Filter` interface and
          introduced `FilterFactory` to parse filter arguments and construct the
          requested filters. This keeps `SearchCommand` small and makes it easier to
          add future filter types.
    * **Enhancement: Search by available stock and required servings.**
        * What it does: Allows users to find recipes that can be prepared from their
          current stock, optionally checking whether the stock is sufficient for a
          requested number of servings.
        * Justification: Connects recipe discovery to the stock-management feature,
          helping users decide what they can cook without manually comparing every
          ingredient.
        * Highlights: Added stock-aware matching, serving-size parsing and checks for
          insufficient quantities. The implementation preserves the existing search
          filters while supporting an arbitrary number of combined filters.
    * **Testing and quality improvements:**
        * Added and extended search tests for name, ingredient, type, serving-size,
          stock and multiple-filter combinations, including invalid inputs and
          insufficient stock.
        * Added parser coverage for the new search arguments and assertions for
          invalid search-command construction.
        * Refactored the filter implementation into separate classes and cleaned up
          the command code to satisfy the project's style and correctness checks.

* **Contributions to the UG**:
    * Documented the `search` command, including name and ingredient filters,
      multiple-filter usage, examples and invalid-input behaviour.
    * Extended the search documentation as stock-aware filtering was added,
      covering recipe availability and serving-size checks.
      (Pull request [\#59](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/59))

* **Contributions to the DG**:
    * Documented the design and implementation of recipe filtering: the common
      `Filter` abstraction, `NameFilter`, `IngredientFilter`, `StockFilter`,
      `FilterFactory` and the AND semantics used by `SearchCommand`.
    * Added the search filter class diagram and factory class diagram, together
      with explanations of validation, stock-aware matching, complexity,
      alternatives and automated verification.

* **Contributions to team-based tasks**:
    * Discussed and Clarified git topics and issues with team members during tutorials and in chats.
    * Merged teammates' PRs after review
      ([\#22](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/22),
      [\#42](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/63)).
    * Tracked my work with issues assigned to the v1.0 and v2.0 milestones
      ([\#23](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/23),
      [\#28](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/28),
      [\#43](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/43)).

* **Contributions beyond the project team**:
    * Reviewed other team's Developer Guide
      ([ClubFlow](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/3)), 
      and ([Lablog](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/24))
