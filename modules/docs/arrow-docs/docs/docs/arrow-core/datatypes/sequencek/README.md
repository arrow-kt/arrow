---
layout: docs-core
title: SequenceK
permalink: /docs/arrow/core/sequencek/
redirect_from:
  - /docs/datatypes/sequencek/
---

## SequenceK




SequenceK implements lazy lists representing lazily-evaluated ordered sequence of homogeneous values.

It can be created from Kotlin Sequence type with a convenient `k()` function.

```kotlin:ank
import arrow.*
import arrow.core.*

sequenceOf(1, 2, 3).k()
```

SequenceK derives many useful typeclasses. For instance, it has a [`SemigroupK`](/docs/arrow/typeclasses/semigroupk/) instance.

```kotlin:ank
val hello = sequenceOf('h', 'e', 'l', 'l', 'o').k()
val commaSpace = sequenceOf(',', ' ').k()
val world = sequenceOf('w', 'o', 'r', 'l', 'd').k()

hello.combineK(commaSpace.combineK(world)).toList() == hello.combineK(commaSpace).combineK(world).toList()
```

[`Functor`](/docs/arrow/typeclasses/functor/)

Transforming a sequence:
```kotlin:ank
val fibonacci = generateSequence(0 to 1) { it.second to it.first + it.second }.map { it.first }.k()
fibonacci.map { it * 2 }.takeWhile { it < 10 }.toList()
```

[`Applicative`](/docs/arrow/typeclasses/applicative/)

Applying a sequence of functions to a sequence:
```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.sequence.apply.ap

sequenceOf(1, 2, 3)
 .ap(sequenceOf({ x: Int -> x + 1}, { x: Int -> x * 2}))
 .toList()
```

SequenceK is a [`Monad`](/docs/arrow/typeclasses/monad/) too. For example, it can be used to model non-deterministic computations. (In a sense that the computations return an arbitrary number of results.)

```kotlin:ank
import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.fx

val positive = generateSequence(1) { it + 1 }.k() // sequence of positive numbers
val positiveEven = positive.filter { it % 2 == 0 }.k()

SequenceK.fx {
  val (p) = positive
  val (pe) = positiveEven
  p + pe
}.take(5).toList()
```

Folding a sequence,

```kotlin:ank
sequenceOf('a', 'b', 'c', 'd', 'e').k().foldLeft("") { x, y -> x + y }
```
