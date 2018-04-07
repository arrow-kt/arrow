---
layout: docs
title: Functor
permalink: /docs/typeclasses/functor/
---

## Functor

The `Functor` typeclass abstracts the ability to `map` over the computational context of a type constructor.
Examples of type constructors that can implement instances of the Functor typeclass include `Option`, `NonEmptyList`,
`List` and many other datatypes that include a `map` function with the shape `fun F<B>.map(f: (A) -> B): F<B>` where `F`
refers to `Option`, `List` or any other type constructor whose contents can be transformed.

### Example

Oftentimes we find ourselves in situations where we need to transform the contents of some datatype. `Functor#map` allows
us to safely compute over values under the assumption that they'll be there returning the transformation encapsulated in the same context.

Consider both `Option` and `Try`:

`Option<A>` allows us to model absence and has two possible states, `Some(a: A)` if the value is not absent and `None` to represent an empty case.

In a similar fashion `Try<A>` may have two possible cases `Success(a: A)` for computations that succeed and `Failure(e: Throwable)` if they fail with an exception.

Both `Try` and `Option` are example datatypes that can be computed over transforming their inner results.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.data.*

Try { "1".toInt() }.map { it * 2 }
Option(1).map { it * 2 }
```

Both `Try` and `Option` include ready to use `Functor` instances:

```kotlin:ank
val optionFunctor = Option.functor()
```

```kotlin:ank
val tryFunctor = Try.functor()
```

Mapping over the empty/failed cases is always safe since the `map` operation in both Try and Option operate under the bias of those containing success values

```kotlin:ank

Try { "x".toInt() }.map { it * 2 }
none<Int>().map { it * 2 }
```

### Main Combinators

#### Kind<F, A>#map

Transforms the inner contents

`fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>`

```kotlin:ank
optionFunctor.run { Option(1).map { it + 1 } }
```

#### lift

Lift a function to the Functor context so it can be applied over values of the implementing datatype

`fun <A, B> lift(f: (A) -> B): (Kind<F, A>) -> Kind<F, B>`

```kotlin:ank
val lifted = optionFunctor.lift({ n: Int -> n + 1 })
lifted(Option(1))
```

#### Other combinators

For a full list of other useful combinators available in `Functor` see the [Source][functor_source]{:target="_blank"}

### Laws

Arrow provides [`FunctorLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Functor instances.

#### Creating your own `Functor` instances

Arrow already provides Functor instances for most common datatypes both in Arrow and the Kotlin stdlib.
Oftentimes you may find the need to provide your own for unsupported datatypes.

You may create or automatically derive instances of functor for your own datatypes which you will be able to use in the context of abstract polymorfic code
as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

### Data types

The following datatypes in Arrow provide instances that adhere to the `Functor` typeclass.

- [Cofree]({{ '/docs/datatypes/cofree' | relative_url }})
- [Coproduct]({{ '/docs/datatypes/coproduct' | relative_url }})  
- [Coyoneda]({{ '/docs/datatypes/coyoneda' | relative_url }})
- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [EitherT]({{ '/docs/datatypes/eithert' | relative_url }})
- [FreeApplicative]({{ '/docs/datatypes/freeapplicative' | relative_url }})
- [Function1]({{ '/docs/datatypes/function1' | relative_url }})
- [Ior]({{ '/docs/datatypes/ior' | relative_url }})
- [Kleisli]({{ '/docs/datatypes/kleisli' | relative_url }})
- [OptionT]({{ '/docs/datatypes/optiont' | relative_url }})
- [StateT]({{ '/docs/datatypes/statet' | relative_url }})
- [Validated]({{ '/docs/datatypes/validated' | relative_url }})
- [WriterT]({{ '/docs/datatypes/writert' | relative_url }})
- [Yoneda]({{ '/docs/datatypes/yoneda' | relative_url }})
- [Const]({{ '/docs/datatypes/const' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Eval]({{ '/docs/datatypes/eval' | relative_url }})
- [IO]({{ '/docs/effects/io' | relative_url }})
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [Id]({{ '/docs/datatypes/id' | relative_url }})
- [Function0]({{ '/docs/datatypes/function0' | relative_url }})

Additionally all instances of [`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }}), [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) and their MTL variants implement the `Functor` typeclass directly
since they are all subtypes of `Functor`

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/arrow-data/src/main/kotlin/arrow/typeclasses/Functor.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/arrow-test/src/main/kotlin/arrow/laws/FunctorLaws.kt
