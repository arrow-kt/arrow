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

Often times we find ourselves in situations where we need to transform the contents of some datatype. `Functor#map` allows
us to safely compute over values under the assumption that they'll be there returning the transformation encapsulated in the same context. 

Consider both `Option` and `Try`:

`Option<A>` allows us to model absence and has two possible states, `Some(a: A)` if the value is not absent and `None` to represent an empty case.

In a similar fashion `Try<A>` may have two possible cases `Success(a: A)` for computations that succeed and `Failure(e: Throwable)` if they fail with an exception.

Both `Try` and `Option` are example datatypes that can be computed over transforming their inner results.

```kotlin:ank
import kategory.*

Try { "1".toInt() }.map { it * 2 }
Option(1).map { it * 2 }
```

Mapping over the empty/failed cases is always safe since the `map` operation in both Try and Option operate under the bias of those containing success values

```kotlin:ank
Try { "x".toInt() }.map { it * 2 }
none<Int>.map { it * 2 }
```

Kategory allows abstract polymorphic code that operates over the evidence of having an instance of a typeclass available. 
This enables programs that are not coupled to specific datatype implementations. 
The technique demonstrated below to write polymorphic code is available for all other [Typeclasses](/docs/typeclasses) beside `Functor`.

```kotlin:ank
inline fun <reified F> multiplyBy2(fa: HK<F, Int>, FT: Functor<F> = functor()): HK<F, Int> =
    FT.map(fa, { it * 2 })

multiplyBy2<OptionHK>(Option(1)) // Option(1)
multiplyBy2<TryHK>(Try { 1 })
``` 

In the example above we've defined a function that can operate over any data type for which a `Functor` instance is available.
And then we applied `multiplyBy2` to two different datatypes for which Functor instances exist.
This technique applied to other Typeclasses allows users to describe entire programs in terms of behaviors typeclasses removing
dependencies to concrete data types and how they operate.

This technique does not enforce inheritance or any kind of subtyping relationship and is frequently known as [`ad-hoc polymorphism`](https://en.wikipedia.org/wiki/Ad_hoc_polymorphism)
and frequently used in programming languages that support [Typeclasses](https://en.wikipedia.org/wiki/Type_class) and [Higher Kinded Types](https://en.wikipedia.org/wiki/Kind_(type_theory)).

Entire libraries and applications can be written without enforcing consumers to use the lib author provided datatypes but letting
users provide their own provided there is typeclass instances for their datatypes.

### Main Combinators

#### map

Transforms the inner contents

`fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>`

```kotlin:ank
val optionFunctor = functor<OptionHK>()
optionfunctor.map(Option(1), { it + 1 })
```

#### lift

Lift a function to the Functor context so it can be applied over values of the implementing datatype

`fun <A, B> lift(f: (A) -> B): (HK<F, A>) -> HK<F, B>`

```kotlin:ank
val lifted = optionFunctor.lift({ it + 1 })
lifted(Option(1))
```

#### Other combinators

For a full list of other useful combinators available in `Functor` see the [`KDoc](/kdocs/typeclasses/functor)

### Syntax

#### HK<F, A>#map

Maps over any higher kinded type constructor for which a functor instance is found

```kotlin:ank
Try { 1 }.map({ it + 1 })
```

#### ((A) -> B)#lift

Lift a function into the functor context

```kotlin:ank
val f = { n: Int -> n + 1 }.lift<OptionHK, Int, Int>()
f(Option(1))
```


### Laws

Kategory provides [`FunctorLaws`](/docs/typeclasses/laws#functorlaws) for internal verification of lawful instances and third party apps creating their own Functor instances.

#### Creating your own `Functor` instances

[Kategory already provides Functor instances for most common datatypes](#datatypes) both in Kategory and the Kotlin stdlib. 
Often times you may find the need to provide your own for unsupported datatypes. 

You may create or automatically derive instances of functor for your own datatypes which you will be able to use in the context of abstract polymorfic code
as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]

### Data types

Thw following datatypes in Kategory provide instances that adhere to the `Functor` typeclass.

- [Cofree](/docs/datatypes/cofree) 
- [Coproduct](/docs/datatypes/coproduct)  
- [Coyoneda](/docs/datatypes/coyoneda)
- [Either](/docs/datatypes/either)
- [EitherT](/docs/datatypes/eitherT)
- [FreeApplicative](/docs/datatypes/FreeApplicative)
- [Function1](/docs/datatypes/Function1)
- [Ior](/docs/datatypes/Ior)
- [Kleisli](/docs/datatypes/Kleisli)
- [OptionT](/docs/datatypes/OptionT)
- [StateT](/docs/datatypes/StateT)
- [Validated](/docs/datatypes/Validated)
- [WriterT](/docs/datatypes/WriterT)
- [Yoneda](/docs/datatypes/Yoneda) 
- [Const](/docs/datatypes/Const)
- [Try](/docs/datatypes/Try)
- [Eval](/docs/datatypes/Eval)
- [IO](/docs/datatypes/IO)
- [NonEmptyList](/docs/datatypes/NonEmptyList)
- [Id](/docs/datatypes/Id)
- [Function0](/docs/datatypes/Function0)

Additionally all instances of [`Applicative`](/docs/typeclasses/applicative), [`Monad`](/docs/typeclasses/monad) and their MTL variants implement the `Functor` typeclass directly
since they are all subtypes of `Functor`