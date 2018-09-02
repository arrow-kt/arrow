---
layout: docs
title: Category
permalink: /docs/typeclasses/category/
---

## Category

{:.intermediate}
intermediate

TODO. Meanwhile you can find a short description in the [intro to typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}).

### Laws

Arrow provides [`CategoryLaws`][category_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Category instances.

### Data Types

The following datatypes in Arrow provide instances that adhere to the `Category` typeclass.

- [Function1]({{ '/docs/datatypes/function1' | relative_url }})

[category_law_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/CategoryLaws.kt
