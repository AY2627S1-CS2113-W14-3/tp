# JIANG Siyu - Project Portfolio Page

## Overview

WhatsCooking is a command-line recipe manager for home cooks. It stores recipes, searches the collection,
scales ingredient quantities, flags possible allergens and tracks home stock.

## Summary of Contributions
* **Code contributed:**  [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=irisiyuj&tabRepo=AY2627S1-CS2113-W14-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)
* **New Feature**: Added the ability to manage home stock (`stock add`, `stock remove`, `stock list`).
    * What it does: Allows users to track ingredients they currently have at home. It automatically handles plural matching (e.g., "egg" and "eggs") and prioritizes removing items that are expiring soonest.
    * Justification: This feature significantly enhances the core value proposition by allowing users to see what they can cook right now based on their current inventory.
    * Highlights: This enhancement affects existing commands and required an in-depth analysis of design alternatives. The implementation was challenging as it required handling floating-point inaccuracies safely and creating a robust matching algorithm.

* **New Feature**: Added stock expiry dates and expiration flags.
    * What it does: Users can add items with an optional `by/YYYY-MM-DD` parameter. The `stock list` command visually flags items as `[EXPIRED]` or `[EXPIRING SOON]`.
    * Justification: Helps users reduce food waste by using up ingredients before they go bad.
    * Highlights: Required creating a new `StockItem` wrapper class to avoid polluting the original `Ingredient` class used by recipes. Sorting logic was added to ensure expired items are used first.

* **New Feature**: Implemented `retrieve` and `delete` commands (v1.0).
    * What it does: Allows users to view or remove recipes by their index or name, with case-insensitive matching.
    * Justification: In a CLI environment, typing the full index every time can be tedious. Allowing name-based retrieval and deletion makes the application much more user-friendly and forgiving. Without this, users would be forced to constantly run the `view` command to remember exact indices.
    * Highlights: This feature required extending the base `RecipeList` with safe, case-insensitive lookup methods (`getByName`). I also had to implement robust out-of-bounds and empty-string validation. A key technical decision was ensuring that failed retrievals or deletions (e.g., typing a non-existent recipe name) threw precise error messages without corrupting the underlying data structure.
  
* **Enhancements to existing features**:
    * Refactored the `StockList` data model: Upgraded the stock storage from directly using `Ingredient` to a new `StockItem` wrapper class. Updated all dependent classes (e.g., `StockFilter`, `Ui`, `Storage`) and fixing type conflicts across the team's codebase.
    * Wrote comprehensive unit tests for `StockList`, `StockCommand`, `RetrieveCommand`, `DeleteCommand`.

* **User Guide:**:
    * Added documentation for the `retrieve`, `delete`, and `stock` features, including syntax, examples, and expected outputs.
    * Added `retrieve` and `delete` to the Command Summary.
* **Developer Guide:**
    * Added implementation details for the Stock Management feature.
    * Created and updated the Object Diagram and Sequence Diagram for the Stock component.
    * Added manual testing instructions for the Stock feature and user stories for `retrieve`, `delete`, and `stock` features.
* **Project management**:
    * Planned and tracked my v1.0 and v2.0 work with issues and milestones:
    [Issue #8](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/8), [#9](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/9), [#10](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/10), [#40](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/40), [#47](https://github.com/AY2627S1-CS2113-W14-3/tp/issues/47)
    * Published the official v1.0 release on GitHub. [v1.0](https://github.com/AY2627S1-CS2113-W14-3/tp/releases/tag/v1.0)
* **Community:**
    * PRs reviewed (with non-trivial review comments) and merged:
    [PR #15](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/15),
    [PR #17](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/17),
    [PR #45](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/45),
    [PR #47](https://github.com/AY2627S1-CS2113-W14-3/tp/pull/52)