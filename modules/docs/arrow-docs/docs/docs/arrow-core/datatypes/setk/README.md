---
layout: docs-core
title: SetK
permalink: /arrow/core/setk/
video: xtnyCqeLI_4
---

## SetK




SetK(Kinded Wrapper) is a higher kinded wrapper around the the Set collection interface.

It can be created from the Kotlin Set type with a convenient `k()` function.

```kotlin:ank
import arrow.*
import arrow.core.*

setOf(1, 2, 5, 3, 2).k()
```

It can also be initialized with the following:

```kotlin:ank
SetK(setOf(1, 2, 5, 3, 2))
```
or
```kotlin:ank
SetK.just(1)
```

given the following:
```kotlin:ank
val oddNumbers = setOf( -11, 1, 3, 5, 7, 9).k()
val evenNumbers = setOf(-2, 4, 6, 8, 10).k()
val integers = setOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5).k()
```
SetK derives the following typeclasses:

[`Semigroup`]({{'/arrow/typeclasses/semigroup/' | relative_url }}) and [`SemigroupK`]({{'/arrow/typeclasses/semigroupk/' | relative_url }}):

```kotlin:ank
val numbers = oddNumbers.combineK(evenNumbers.combineK(integers))
numbers
```
```kotlin:ank
evenNumbers.combineK(integers).combineK(oddNumbers)
```

[`Monoid`]({{'/arrow/typeclasses/monoid/' | relative_url }}) and [`MonoidK`]({{'/arrow/typeclasses/monoidk/' | relative_url }}):
```kotlin:ank
numbers.combineK(SetK.empty())
```

[`Foldable`]({{'/arrow/typeclasses/foldable/' | relative_url }}):
```kotlin:ank
numbers.foldLeft(0) {sum, number -> sum + (number * number)}
```
