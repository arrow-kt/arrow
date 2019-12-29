---
layout: docs-core
title: Semigroup
permalink: /docs/arrow/typeclasses/semigroup/
redirect_from:
  - /docs/typeclasses/semigroup/
---

## Semigroup




A semigroup for some given type `A` has a single operation (which we will call `combine`), which takes two values of type `A`, and returns a value of type `A`. This operation must be guaranteed to be associative. That is to say that,

```kotlin
(a.combine(b)).combine(c)
```

must be the same as

```kotlin
a.combine(b.combine(c))
```

for all possible values of a, b, c.

There are instances of `Semigroup` defined for many types found in Arrow and the Kotlin std lib.
For example, `Int` values are combined using addition by default, but multiplication is also associative and forms another `Semigroup`.

### Examples

Now that you've learned about the Semigroup instance for Int, try to guess how it works in the following examples:

```kotlin:ank
import arrow.*
import arrow.core.extensions.*

Int.semigroup().run { 1.combine(2) }
```

```kotlin:ank   
import arrow.core.*
import arrow.core.extensions.*
import arrow.core.extensions.listk.semigroup.*

ListK.semigroup<Int>().run {
  listOf(1, 2, 3).k().combine(listOf(4, 5, 6).k())
}
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.semigroup.semigroup

Option.semigroup(Int.semigroup()).run {
  Option(1).combine(Option(2))
}
```

```kotlin:ank
Option.semigroup(Int.semigroup()).run {
  Option(1).combine(None)
}
```

Many of these types have methods defined directly on them, which allow for this example of combining: `+` on `List`. But the value of having a `Semigroup` typeclass available is that these compose.

Additionally, `Semigroup` adds `+` syntax to all types for which a Semigroup instance exists:

```kotlin:ank
Option.semigroup(Int.semigroup()).run {
  Option(1) + Option(2)
}
```

Contents partially adapted from [Scala Exercises Cat's Semigroup Tutorial](https://www.scala-exercises.org/cats/semigroup)


### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Semigroup

TypeClass(Semigroup::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Semigroup)
