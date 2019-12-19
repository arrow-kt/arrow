---
layout: docs-core
title: Bifunctor
permalink: /docs/arrow/typeclasses/bifunctor/
redirect_from:
  - /docs/typeclasses/bifunctor/
---

## Bifunctor




`Bifunctor` is a lot like [`Functor`]({{ '/docs/arrow/typeclasses/functor' | relative_url }}). It offers a nice solution for those times when you don’t want to ignore the leftmost type argument of a binary type constructor, such as `Either` or `Tuple2`.

Its core operation, `bimap`, closely resembles `map`, except it lifts two functions into the new context, allowing you to apply one or both.

```kotlin
fun Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>
```

`bimap` takes two unary functions and a binary type constructor as a receiver, such as `Tuple2(1, 3)` or `Left(5)`, and applies whichever function it can -- both if possible!

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.typeclasses.*
import arrow.core.extensions.either.bifunctor.*

fun <F> greet(BF: Bifunctor<F>, p: Kind2<F, String, String>): Kind2<F, String, String> =
    BF.run { p.bimap({ "Hello $it" }, { "General $it" }) }

greet(Either.bifunctor(), Left("there")) // Left("Hello there")    
```

```kotlin:ank
greet(Either.bifunctor(), Right("Kenobi")) // Right("General Kenobi")
```

```kotlin:ank
import arrow.core.extensions.tuple2.bifunctor.*

greet(Tuple2.bifunctor(), Tuple2("there", "Kenobi")) // Tuple2("Hello there", "General Kenobi")
```

So, `bimap` is `map`, but for binary type constructors where you want the ability to lift two functions at once.

### Main Combinators

#### Kind2<F, A, B>#bimap

Transforms the inner contents of a binary type constructor.

`fun Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>`

```kotlin:ank
val tuple2Bifunctor = Tuple2.bifunctor()
tuple2Bifunctor.run { Tuple2(4, 4).bimap({ it + 1 }, { it - 1 }) }
```

#### Other combinators

For a full list of other useful combinators available in `Bifunctor`, see the [Source][bifunctor_source]{:target="_blank"}

### Laws

Arrow provides [`BifunctorLaws`][bifunctor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Bifunctor instances.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Bifunctor

TypeClass(Bifunctor::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Bifunctor)

[bifunctor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Bifunctor.kt
[bifunctor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/BifunctorLaws.kt
