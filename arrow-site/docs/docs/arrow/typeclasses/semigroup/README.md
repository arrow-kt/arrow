---
layout: docs-core
title: Semigroup
permalink: /arrow/typeclasses/semigroup/
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

```kotlin
import arrow.typeclasses.Semigroup

Semigroup.int().run { 1.combine(2) }
```

```kotlin
import arrow.typeclasses.Semigroup

Semigroup.list<Int>().run {
    listOf(1, 2, 3).combine(listOf(4, 5, 6))
}
```

```kotlin
import arrow.core.Option
import arrow.typeclasses.Semigroup

Semigroup.option(Semigroup.int()).run {
    Option(1).combine(Option(2))
}
```

```kotlin
import arrow.core.Option
import arrow.core.None
import arrow.typeclasses.Semigroup

Semigroup.option(Semigroup.int()).run {
    Option(1).combine(None)
}
```

Many of these types have methods defined directly on them, which allow for this example of combining: `+` on `List`. But the value of having a `Semigroup` typeclass available is that these compose.

Additionally, `Semigroup` adds `+` syntax to all types for which a Semigroup instance exists:

```kotlin
Semigroup.option(Semigroup.int()).run {
    Option(1) + Option(2)
}
```

Contents partially adapted from [Scala Exercises Cat's Semigroup Tutorial](https://www.scala-exercises.org/cats/semigroup)
