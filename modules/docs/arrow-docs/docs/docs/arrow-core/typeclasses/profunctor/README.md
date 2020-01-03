---
layout: docs-core
title: Profunctor
permalink: /docs/arrow/typeclasses/profunctor/
redirect_from:
  - /docs/typeclasses/profunctor/
---

## Profunctor




Before reading this typeclass, we recommend that you understand [Contravariance](https://typeclasses.com/contravariance) first. But, for making things easier, we will consider `Contravariance` as the ability to _flip composition_.

`Profunctors` are [`Bifunctors`]({{ '/docs/arrow/typeclasses/bifunctor' | relative_url }}) that are contravariant in their first argument and covariant in the second one.

The core operation of the `Profunctor` typeclass is `dimap` (as `bimap` was already taken for `Bifunctor`).

`fun <A, B, C, D> Kind2<F, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Kind2<F, C, D>`

The main difference between `bimap` and `dimap` is the function they accept as their first argument:

* `bimap`: `fl: (A) -> C`
* `dimap`: `fl: (C) -> A`

And how does this work? Well, if we think in terms of function composition, functions can be composed in both directions:

```kotlin:ank
import arrow.core.*

val sum2: (Int) -> Int = { x -> x + 2 }
val str: (Int) -> String = { x -> x.toString() }

val f = str compose sum2
val g = sum2 andThen str
f(4) == g(4)
```

Functions are a binary type constructor of an input type and an output type. It is implemented in [`Function1`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-function1/' | relative_url }}).

So, if we have a function `(A) -> B` and a `Profunctor` instance for it, we can make the following transformation with `dimap`: `((C) -> A) -> ((A) -> B) -> ((B) -> D)`.

Example:

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.function1.profunctor.*

val fab: Function1<Double, Double> = { x: Double -> x * 3 }.k()
val f: (Int) -> Double = { x -> x.toDouble() / 2 }  
val g: (Double) -> String = { x -> "Result: $x" }

val h = Function1.profunctor().run { fab.dimap(f, g) }
h(4)
```

### Main Combinators

#### Kind2<F, A, B>#bimap

Contramap on the first type parameter and map on the second type parameter.

`fun Kind2<F, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Kind2<F, C, D>`

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.*

val f: Function1<Int, Int> = { x: Int -> x + 10 }.k()
val fl: (String) -> Int = { x -> x.toInt() }
val fr: (Int) -> List<Int> = { x -> List(x) { x } }

val g: Function1<String, List<Int>> = Function1.profunctor().run { f.dimap(fl, fr) }
g("6")
```

#### Other combinators

For a full list of other useful combinators available in `Profunctor`, see the [Source][profunctor_source]{:target="_blank"}

### Laws

Arrow provides [`ProfunctorLaws`][profunctor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Profunctor instances.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Profunctor

TypeClass(Profunctor::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Profunctor)

[profunctor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Profunctor.kt
[profunctor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/ProfunctorLaws.kt
