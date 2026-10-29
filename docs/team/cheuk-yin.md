# Cheuk Yin - Project Portfolio Page

## Overview

WhatsCooking is a command-line recipe manager for home cooks who prefer typing. Users store recipes with their
ingredients, steps and notes, find them quickly, resize them for a different number of servings, check possible
allergens and keep track of the ingredients they have at home.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=cheuk-yin&breakdown=true)

* **Application skeleton**: Built the structure that every command runs on.
  (Pull requests [\#11](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/11),
  [\#17](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/17))
    * What it does: the main loop reads a line, `Parser` turns it into a `Command` subclass, the command runs
      against the shared `RecipeList`, and `Ui` prints the result. Also added the first versions of `Recipe`,
      `RecipeList`, `Ingredient` and stubs for each planned command.
    * Justification: with one class per command, each member could develop their command in a separate file
      without editing the same code. Every later feature (`add`, `edit`, `modify`, `search`,
      `stock`, `substitute`) plugs into this loop.
    * Highlights: standardised error handling so that every user error is a `WhatsCookingException` caught in one
      place, which means an invalid command reports an error instead of crashing. Added `AppLogger`, which sends
      all log records to a file so that diagnostic messages never mix with the user's console output.
      Added assertions for programming errors throughout the skeleton classes.

* **New Feature**: Added saving and loading of recipes.
  (Pull request [\#36](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/36))
    * What it does: saves every recipe to `data/whatscooking.txt` after each successful command, and loads them
      back when the app starts. The user never has to save manually.
    * Justification: without it, every recipe is lost when the app closes, which makes the product unusable for
      its main purpose of keeping a recipe collection.
    * Highlights: saving happens once in the main loop, not inside each command, so changes made in place (such
      as `modify` scaling a stored recipe) are saved without the command needing to do anything. A missing data
      file starts the app with an empty list, and a corrupted one shows a warning instead of crashing. Loaded
      values go through the same validation as typed input. The plain-text format needs no external library or
      escaping.

* **New Feature**: Added the `view` command to list all recipes.
  (Pull request [\#18](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/18))
    * What it does: lists every recipe with its number, title, type and serving size.
    * Justification: users previously had no way to see the recipes they had added or to find the number needed
      by `retrieve`, `delete` and `edit`.

* **Testing**: Wrote `StorageTest` (missing file, round trip of every field, folder creation, corrupted file),
  `PersistenceWorkflowTest` (runs the real app as two separate processes to check that a recipe survives a
  restart), `ViewCommandTest`, `RecipeTest`, `ExitCommandTest` and the extended `ParserTest`.

* **Documentation**:
    * User Guide:
        * Wrote the sections "Quick Start", "Viewing all recipes: `view`", "Exiting the program: `bye`" and
          "Saving the data", and the FAQ entries on transferring data and the log file.
        * Added `bye` to the Command Summary.
    * Developer Guide:
        * Wrote the Design sections "Architecture" and "Storage design", and the Implementation sections
          "Command execution: main loop and error handling", "Recipe persistence: saving and loading" and
          "Listing recipes: `view`".
        * Added four UML diagrams: an architecture class diagram, a main loop sequence diagram, a storage class
          diagram and a storage loading sequence diagram.
        * Added user stories, non-functional requirements, glossary terms and manual testing instructions for
          viewing, saving and loading.
        * Restructured my sections when the team moved to a separate Design and Implementation layout.
          (Pull request [\#52](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/52))
        * While checking the DG against the code, found and documented two bugs in the main loop: `bye` cannot
          exit if saving fails, and the app crashes if input ends without `bye`.

* **Project management**:
    * Set up the project for the team by renaming the template's package and main class to WhatsCooking.
      (Pull request [\#11](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/11))
    * Tracked my work with issues assigned to the v1.0 and v2.0 milestones
      ([\#27](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/27),
      [\#35](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/35),
      [\#51](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/51),
      [\#58](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/58)).

* **Community**:
    * Reviewed and approved teammates' pull requests
      [\#21](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/21),
      [\#34](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/34) and
      [\#46](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/46).
    * Reviewed another team's Developer Guide
      ([GameVault](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/19#pullrequestreview-5678626329)), with
      four comments pointing out
      [an incorrect dependency label](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/19#discussion_r4397533005),
      two sequence diagrams that
      [left out](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/19#discussion_r4397575506)
      [method calls](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/19#discussion_r4397588973) made by the
      code, and [missing diagrams](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/19#discussion_r4397628653)
      for how `Ui`, `Parser` and `Command` work together.
