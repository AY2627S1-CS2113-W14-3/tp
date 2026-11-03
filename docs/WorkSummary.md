# WhatsCooking - Work Summary

## Overview

WhatsCooking is a command-line recipe manager for home cooks. It stores recipes,
supports searching and retrieval, scales ingredient quantities, checks possible
allergens, suggests ingredient substitutions and tracks ingredients kept at home.

This document records the work covered by the current project documentation and
implementation. It follows the organisation used by the individual Project
Portfolio Pages in [`docs/team`](team/).

## Features covered

* Created and edited recipes with structured ingredients, steps, notes, recipe
  types and serving sizes.
* Listed, retrieved and deleted recipes by index or name.
* Saved recipes to disk and loaded them again when the application restarts.
* Searched recipes using composable filters.
* Modified ingredient quantities for a different serving size using exact
  fraction-based calculations.
* Displayed possible allergen warnings using whole-word matching.
* Suggested explicit ingredient alternatives without changing the recipe.
* Added and removed home-stock quantities, including expiry dates and expiry
  warnings.

## Engineering work

The application uses a command loop in which input is parsed into command
objects that operate on a shared recipe list. Validation is performed before
mutating application state, and common failures are reported through the
application's exception-handling path. Persistence is centralised so successful
changes are saved consistently, including in-place serving-size updates.

Important implementation considerations include:

* reusable validation for `add` and `edit`;
* case-insensitive recipe lookup and duplicate-title checks;
* immutable quantity values and exact arithmetic for scaling;
* safe handling of stock removal, expiry ordering and floating-point residue;
* defensive handling of missing or corrupted save files; and
* assertions, logging and tests for invalid states and command workflows.

## Documentation work

The User Guide documents command syntax, examples, expected output and common
errors. The Developer Guide documents the architecture, storage format,
command flows, design alternatives, limitations and automated verification.
It also contains class, sequence and activity diagrams, user stories,
non-functional requirements, glossary entries and manual testing instructions.

The detailed feature ownership and individual contributions are recorded in:

* [Cheuk Yin's PPP](team/cheuk-yin.md)
* [Siyu Jiang's PPP](team/irisiyuj.md)
* [CAO Jingyuan's PPP](team/littlecrawler.md)
* [Lim Sin's PPP](team/wunicorn21.md)

## Verification

Verification includes unit tests for the recipe, ingredient, parser, command,
storage and stock models, together with workflow tests that exercise the real
application and persistence across restarts. Manual test instructions in the
Developer Guide cover recipe creation and editing, serving-size changes,
allergen warnings, substitutions, saving and loading, retrieval and deletion,
and stock management.

## Remaining follow-up

Before treating this as an individual PPP, add the author's name, personal
code contributions, issue and pull-request links, review work and any
contributions outside the project team. Those details should be copied from
the relevant team member's PPP rather than inferred from the shared project
documentation.
