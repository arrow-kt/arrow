---
layout: docs-core
title: Applicative
permalink: /docs/arrow/typeclasses/applicative/
redirect_from:
  - /docs/typeclasses/applicative/
---

## Applicative




The `Applicative` typeclass abstracts the ability to lift values and apply functions over the computational context of a type constructor.
Examples of type constructors that can implement instances of the Applicative typeclass include `Option`, `NonEmptyList`,
`List`, and many other datatypes that include a `just` and either `ap` function. `ap` may be derived for monadic types that include a `Monad` instance via `flatMap`.

`Applicative` includes all combinators present in [`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }}).

### Applicative Builder examples

We often find ourselves in situations where we need to compute multiple independent values resulting from operations that do not depend on each other.

In the following example, we will define three invocations that may as well be remote or local services—each one of them returning different results in the same computational context of `Option`.

```kotlin:ank
import arrow.*
import arrow.core.*

fun profileService(): Option<String> = Some("Alfredo Lambda")
fun phoneService(): Option<Int> = Some(55555555)
fun addressService(): Option<List<String>> = Some(listOf("1 Main Street", "11130", "NYC"))
```

This more or less illustrates the common use case of performing several independent operations where we need to get all the results together.

The typeclass features several methods related to Applicative Builders that allow you to easily combine all the independent operations into one result.

```kotlin:ank
import arrow.core.extensions.option.applicative.*

data class Profile(val name: String, val phone: Int, val address: List<String>)

val r: Option<Tuple3<String, Int, List<String>>> = Option.applicative().tupled(profileService(), phoneService(), addressService()).fix()
r.map { Profile(it.a, it.b, it.c) }
```

The Applicative Builder also provides a `map` operations that is able to abstract over arity in the same way as `tupled`.

```kotlin:ank
Option.applicative().map(profileService(), phoneService(), addressService(), { (name, phone, addresses) ->
  Profile(name, phone, addresses)
})
```

### Main Combinators

#### just

A constructor function, also known as `pure` in other languages.
It lifts a value into the computational context of a type constructor.

`fun <A> just(a: A): Kind<F, A>`

```kotlin:ank
Option.just(1) // Some(1)
```

#### Kind<F, A>#ap

Apply a function inside the type constructor's context.

`fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B>`

```kotlin:ank
Option.applicative().run { Some(1).ap(Some({ n: Int -> n + 1 })) }
```

#### Other combinators

For a full list of other useful combinators available in `Applicative`, see the [Source][applicative_source]{:target="_blank"}


#### Kind<F, A>#map2

Map two values inside the type constructor context and apply a function to their cartesian product.

```kotlin:ank
Option.applicative().run { Some(1).map2(Some("x")) { z: Tuple2<Int, String> ->  "${z.a}${z.b}" } }
```

#### Kind<F, A>#map2Eval

Lazily map two values inside the type constructor context and apply a function to their cartesian product.
Computation happens when `.value()` is invoked.

```kotlin:ank
Option.applicative().run { Some(1).map2Eval(Eval.later { Some("x") }, { z: Tuple2<Int, String> ->  "${z.a}${z.b}" }).value() }
```

#### Kind<F, A>#followedBy

Sequences actions, discarding the value of the first argument.

```kotlin:ank
Option.applicative().run { Some(1).followedBy(Some(2)) }
```

#### Kind<F, A>#apTap

This is a reverse for `followedBy`. Sequences actions but discarding the value of the second argument.

```kotlin:ank
Option.applicative().run { Some(1).apTap(Some(2)) } 
```

### Apply

A closely related type class is Apply, which is identical to Applicative, modulo the ``just`` method. Indeed, Applicative is a subclass of Apply with the addition of this method.

```kotlin:ank
import arrow.typeclasses.Functor 

interface Apply<F> : Functor<F> {
  fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B>
}

interface Applicative<F> : Apply<F> {
  fun <A> just(a: A): Kind<F, A> 
}
```

One of the motivations for Apply’s existence is that some types have Apply instances but not Applicative.

### Laws

Arrow provides [`ApplicativeLaws`][applicative_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Applicative instances.

#### Creating your own `Applicative` instances

Arrow already provides Applicative instances for most common datatypes both in Arrow and the Kotlin stdlib.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own Applicative instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Applicative

TypeClass(Applicative::class).dtMarkdownList()
```

Additionally, all instances of [`Monad`]({{ '/docs/arrow/typeclasses/monad' | relative_url }}) and their MTL variants implement the `Applicative` typeclass directly
since they are all subtypes of `Applicative`.

ank_macro_hierarchy(arrow.typeclasses.Applicative)

[applicative_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Applicative.kt
[applicative_law_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/ApplicativeLaws.kt
