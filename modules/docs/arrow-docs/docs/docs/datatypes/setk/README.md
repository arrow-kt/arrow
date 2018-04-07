---
layout: docs
title: SetK
permalink: /docs/datatypes/setK/
video: xtnyCqeLI_4
---

## SetK

SetK(Kinded Wrapper) is a higher kinded wrapper around the the Set collection interface. 

It can be created from the Kotlin Set type with a convient `k()` function.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.data.*

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
val oldNumbers = setOf( -11, 1, 3, 5, 7, 9).k()
val evenNumbers = setOf(-2, 4, 6, 8, 10).k()
val integers = setOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5).k()
```
SetK derives the following typeclasses:

[`Semigroup`](/docs/typeclasses/semigroup/) and [`SemigroupK`](/docs/typeclasses/semigroupk/):

```kotlin:ank
val numbers = oldNumbers.combineK(evenNumbers.combineK(integers))
numbers
```
```kotlin:ank
evenNumbers.combineK(integers).combineK(oldNumbers)
```

[`Monoid`](/docs/typeclasses/monoid/) and [`MonoidK`](/docs/typeclasses/monoidk/):
```kotlin:ank
SetK.monoidK().run { numbers.combineK(SetK.empty()) }
```

[`Foldable`](/docs/typeclasses/foldable/):
```kotlin:ank
numbers.foldLeft(0) {sum, number -> sum + (number * number)}
```

Available Instances:

[Show]({{ '/docs/typeclasses/show' | relative_url }})
[Eq]({{ '/docs/typeclasses/eq' | relative_url }})
[Foldable]({{ '/docs/typeclasses/foldable' | relative_url }})
[Monoid]({{ '/docs/typeclasses/monoid' | relative_url }})
[MonoidK]({{ '/docs/typeclasses/monoidk' | relative_url }})
[Semigroup]({{ '/docs/typeclasses/semigroup' | relative_url }})
[SemigroupK]({{ '/docs/typeclasses/semigroupk' | relative_url }})
[At]({{ '/docs/optics/at' | relative_url }})
