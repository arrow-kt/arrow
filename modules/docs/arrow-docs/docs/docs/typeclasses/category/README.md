---
layout: docs
title: Category
permalink: /docs/typeclasses/category/
---

## Category

{:.intermediate}
intermediate

`Category` is a typeclass that represents a mathematical Category. In essence a category provides a context for unifying ideas in different disciplines of mathematics. It turns out that a Category can play the same role in functional programming. To learn more about this a good reference is Bartosz Milewski's blog [Category Theory for Programmers](https://bartoszmilewski.com/2014/10/28/category-theory-for-programmers-the-preface/).

### Main Combinators

#### id

A constructor for the identity morphism for an object in the `Category`.

`fun <A> id(): Kind2<F, A, A>`

```kotlin:ank
import arrow.core.Function1
import arrow.core.invoke

Function1.id<Int>().invoke(1)
```

#### Kind2<F, B, C>.compose

Compose two morphisms the `Category`.

`fun <A, B, C> Kind2<F, B, C>.compose(arr: Kind2<F, A, B>): Kind2<F, A, C>`

```kotlin:ank
import arrow.core.category
import arrow.core.compose
import arrow.core.Function1

Function1.category().run {
  val plusTwo: (Int) -> Int = 2::plus
  plusTwo.compose(plusTwo)(0)
}
```

### Laws

Arrow provides [`CategoryLaws`][category_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Category instances.

### Data Types

The following datatypes in Arrow provide instances that adhere to the `Category` typeclass.

- [Function1]({{ '/docs/datatypes/function1' | relative_url }})

[category_law_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/CategoryLaws.kt
