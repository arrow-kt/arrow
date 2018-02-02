---
layout: docs
title: SetKW
permalink: /docs/datatypes/setkw/
---

## SetKW

SetKW(Kinded Wrapper) is a higher kinded wrapper around the the Set collection interface. 

It can be created from the Kotlin Set type with a convient `k()` function.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.data.*

setOf(1, 2, 5, 3, 2).k()
```

It can also be initialized with the following:

```kotlin:ank
SetKW(setOf(1, 2, 5, 3, 2))
```
or
```kotlin:ank
SetKW.pure(1)
```

given the following:
```kotlin:ank
val oldNumbers = setOf( -11, 1, 3, 5, 7, 9).k()
val evenNumbers = setOf(-2, 4, 6, 8, 10).k()
val integers = setOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5).k()
```
SetKW derives the following typeclasses:

[`Semigroup`](/docs/typeclasses/semigroup/) and [`SemigroupK`](/docs/typeclasses/semigroupk/):
```kotlin:ank
val uniqueNaturalNumbers_1 = oldNumbers.combineK(evenNumbers.combineK(integers))
uniqueNaturalNumbers_1
```
```kotlin:ank
val uniqueNaturalNumbers_2 = oldNumbers.combineK(evenNumbers).combineK(integers)
uniqueNaturalNumbers_2
```
[`Monoid`](/docs/typeclasses/monoid/) and [`MonoidK`](/docs/typeclasses/monoidk/):
```kotlin:ank
val sum_3 = SetKW.monoid<Int>().combine(uniqueNaturalNumbers_1, SetKW.empty()).foldLeft(0){sum, number -> sum + (number * number)}
sum_3
```kotlin:ank
val sum_4 = SetKW.monoid<Int>().combine(SetKW.empty(), uniqueNaturalNumbers_1).foldLeft(0){sum, number -> sum + (number * number)}
sum_4
```
```kotlin:ank
sum_3 == sum_4
```
[`Foldable`](/docs/typeclasses/foldable/):
```kotlin:ank
val sum_1 = uniqueNaturalNumbers_1.foldLeft(0){sum, number -> sum + (number * number)}
sum_1
```
```kotlin:ank
val sum_2 = uniqueNaturalNumbers_2.foldLeft(0){sum, number -> sum + (number * number)}
sum_2
```
```kotlin:ank
sum_1 == sum_2
```
