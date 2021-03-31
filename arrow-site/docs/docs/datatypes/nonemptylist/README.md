---
layout: docs-core
title: NonEmptyList
permalink: /arrow/core/nonemptylist/
video: TC6IzE61OyE
---

## NonEmptyList




`NonEmptyList` is a data type used in __Λrrow__ to model ordered lists that have at least one value.
`NonEmptyList` is available in the `arrow-core-data` module under the `import arrow.core.NonEmptyList`

```groovy
// gradle
compile "io.arrow-kt:arrow-core-data:$arrow_version"
```

```kotlin:ank
// namespace
import arrow.core.*
```

## of

A `NonEmptyList` guarantees the list always has at least 1 element.

```kotlin:ank:silent
nonEmptyListOf(1, 2, 3, 4, 5) // NonEmptyList<Int>
nonEmptyListOf(1, 2) // NonEmptyList<Int>
//nonEmptyListOf() // does not compile
```

## head

Unlike `List#[0]`, `NonEmptyList#head` is a safe operation that guarantees no exception throwing.

```kotlin
nonEmptyListOf(1, 2, 3, 4, 5).head
```

## foldLeft

When we fold over a `NonEmptyList`, we turn a `NonEmptyList< A >` into `B` by providing a __seed__ value and a __function__ that carries the state on each iteration over the elements of the list.
The first argument is a function that addresses the __seed value__, this can be any object of any type which will then become the resulting typed value.
The second argument is a function that takes the current state and element in the iteration and returns the new state after transformations have been applied.

```kotlin:ank
fun sumNel(nel: NonEmptyList<Int>): Int =
  nel.foldLeft(0) { acc, n -> acc + n }

sumNel(nonEmptyListOf(1, 1, 1, 1))
```

## map

`map` allows us to transform `A` into `B` in `NonEmptyList< A >`

```kotlin:ank
nonEmptyListOf(1, 1, 1, 1).map { it + 1 }
```

## flatMap

`flatMap` allows us to compute over the contents of multiple `NonEmptyList< * >` values

```kotlin:ank
val nelOne: NonEmptyList<Int> = nonEmptyListOf(1)
val nelTwo: NonEmptyList<Int> = nonEmptyListOf(2)

nelOne.flatMap { one ->
  nelTwo.map { two ->
    one + two
  }
}
```

## Applicative Builder

Λrrow contains methods that allow you to preserve type information when computing over different `NonEmptyList` typed values.

```kotlin:ank
import arrow.core.*
import java.util.*

data class Person(val id: UUID, val name: String, val year: Int)

// Note each NonEmptyList is of a different type
val nelId: NonEmptyList<UUID> = nonEmptyListOf(UUID.randomUUID(), UUID.randomUUID())
val nelName: NonEmptyList<String> = nonEmptyListOf("William Alvin Howard", "Haskell Curry")
val nelYear: NonEmptyList<Int> = nonEmptyListOf(1926, 1900)

nelId.zip(nelName, nelYear) { id, name, year ->
  Person(id, name, year)
}
```

### Summary

- `NonEmptyList` is __used to model lists that guarantee at least one element__
- We can easily construct values of `NonEmptyList` with `nonEmptyListOf`
- `foldLeft`, `map`, `flatMap`, and others are used to compute over the internal contents of a `NonEmptyList` value.
- `NonEmptyList.mapN(..) { ... }` can be used to compute over multiple `NonEmptyList` values preserving type information and __abstracting over arity__ with `map`
