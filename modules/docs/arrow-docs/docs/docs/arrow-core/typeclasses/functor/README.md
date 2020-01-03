---
layout: docs-core
title: Functor
permalink: /docs/arrow/typeclasses/functor/
redirect_from:
  - /docs/typeclasses/functor/
video: EUqg3fSahhk
---

## Functor




The `Functor` typeclass abstracts the ability to `map` over the computational context of a type constructor.
Examples of type constructors that can implement instances of the Functor typeclass include `Option`, `NonEmptyList`,
`List`, and many other datatypes that include a `map` function with the shape `fun F<A>.map(f: (A) -> B): F<B>` where `F`
refers to `Option`, `List`, or any other type constructor whose contents can be transformed.

### Example

Oftentimes we find ourselves in situations where we need to transform the contents of some datatype. `Functor#map` allows
us to safely compute over values under the assumption that they'll be there returning the transformation encapsulated in the same context.

Consider both `Option` and `Try`:

`Option<A>` allows us to model absence and has two possible states: `Some(a: A)` if the value is not absent, and `None` to represent an empty case.

In a similar fashion, `Try<A>` may have two possible cases: `Success(a: A)` for computations that succeed, and `Failure(e: Throwable)` if they fail with an exception.

Both `Try` and `Option` are example datatypes that can be computed over transforming their inner results.

```kotlin:ank
import arrow.*
import arrow.core.*

Try { "1".toInt() }.map { it * 2 }
Option(1).map { it * 2 }
```

Both `Try` and `Option` include ready-to-use `Functor` instances:

```kotlin:ank
import arrow.core.extensions.option.functor.*

val optionFunctor = Option.functor()
```

```kotlin:ank
import arrow.core.extensions.`try`.functor.*

val tryFunctor = Try.functor()
```

Mapping over the empty/failed cases is always safe since the `map` operation in both Try and Option operate under the bias of those containing success values.

```kotlin:ank

Try { "x".toInt() }.map { it * 2 }
none<Int>().map { it * 2 }
```

### Main Combinators

#### Kind<F, A>#map

Transforms the inner contents.

`fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>`

```kotlin:ank
optionFunctor.run { Option(1).map { it + 1 } }
```

#### lift

Lift a function to the Functor context so it can be applied over values of the implementing datatype.

`fun <A, B> lift(f: (A) -> B): (Kind<F, A>) -> Kind<F, B>`

```kotlin:ank
val lifted = optionFunctor.lift({ n: Int -> n + 1 })
lifted(Option(1))
```

#### Other combinators

For a full list of other useful combinators available in `Functor`, see the [Source][functor_source]{:target="_blank"}

### Laws

Arrow provides [`FunctorLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Functor instances.

#### Creating your own `Functor` instances

Arrow already provides Functor instances for most common datatypes both in Arrow and the Kotlin stdlib.
Oftentimes, you may find the need to provide your own for unsupported datatypes.

You may create or automatically derive instances of Functor for your own datatypes which you will be able to use in the context of abstract polymorphic code
as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Functor

TypeClass(Functor::class).dtMarkdownList()
```

Additionally, all instances of [`Applicative`]({{ '/docs/arrow/typeclasses/applicative' | relative_url }}), [`Monad`]({{ '/docs/arrow/typeclasses/monad' | relative_url }}), and their MTL variants, implement the `Functor` typeclass directly
since they are all subtypes of `Functor`

ank_macro_hierarchy(arrow.typeclasses.Functor)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Functor.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/FunctorLaws.kt
