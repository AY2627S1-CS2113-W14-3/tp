# CAO JINGYUAN - Project Portfolio Page

## Overview

WhatsCooking is a command-line recipe manager for home cooks. It stores recipes, searches the collection,
scales ingredient quantities, flags possible allergens and tracks home stock.

## Summary of Contributions

* **Code contributed:** [RepoSense](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=littlecrawler&breakdown=true).
* **Structured ingredients:** Implemented quantity/unit/name parsing and integrated it with `add`,
  with tests for accepted formats and invalid input
  ([PR #15](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/15)).
* **Serving-size modification:** Implemented `modify` and the immutable `IngredientQuantity` model
  ([PR #22](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/22)). Quantities use reduced fractions so
  display rounding does not affect later calculations within a session. All scaled results are checked
  before any ingredient changes, preventing a failed resize from leaving partial updates.
* **Allergen warnings:** Added the nine-category checker and consistent warnings on `add` and
  `retrieve`, with whole-word matching, stable results, assertions and logging
  ([PR #39](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/39)). Also addressed the tutor's
  `Ingredient` readability feedback and checked `add s/` compatibility with `modify`.
* **Ingredient alternatives:** Implemented the Week 11 `substitute` command
  ([PR #56](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/56), pending merge). It uses an explicit
  complete-name catalogue, returns immutable suggestions with cooking caveats, and preserves every
  recipe quantity and field. Tests cover compound names, numbering,
  locale differences, save/restart and later resizing.
* **Testing:** Added ingredient parsing/display, scaling, command and CLI workflow tests. The workflow
  tests use isolated directories and actual Java processes to check persistence and console behavior.
* **Documentation:** Wrote the UG sections for structured ingredients, `modify`, allergen warnings
  and `substitute`. Documented their class design, execution flows, alternatives and limitations in
  the DG, with editable class and sequence diagrams. The Week 11 revision separates Design and
  Implementation, updates my diagrams in response to review, and adds substitution manual tests.
* **Team review and integration:** Reviewed stock-expiry
  [PR #48](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/48#pullrequestreview-5669254981), identifying
  [mutation before a failed removal](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/48#discussion_r4390265577),
  [floating-point residual batches](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/48#discussion_r4390279801)
  and [incorrect DG expectations](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/48#discussion_r4390315672).
  Merged teammates' [PR #41](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/41),
  [PR #48](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/48) and
  [PR #49](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/49); their implementation remains attributed
  to the respective authors.
* **Task tracking:** Tracked the allergen and documentation work in
  [issue #37](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/37) and
  [issue #38](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/38).
* **Reviews beyond the team:** Gave eight inline DG comments on 28 October across
  [MishMash](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/15#pullrequestreview-5678828471)
  and [CakeQueue](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/6#pullrequestreview-5678750361),
  covering diagram readability, missing algorithm and storage explanations, command consistency
  and state transitions. Also reported missing diagrams in
  [GameVault](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/19#pullrequestreview-5678692505).
