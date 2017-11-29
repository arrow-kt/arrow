---
layout: docs
title: Applicative
permalink: /docs/typeclasses/applicative/
---

## Applicative

The `Applicative` typeclass abstracts the ability to lift values and apply functions over the computational context of a type constructor.
Examples of type constructors that can implement instances of the Applicative typeclass include `Option`, `NonEmptyList`,
`List` and many other datatypes that include a `pure` and either `ap` function. `ap` may be derived for monadic types that include a `Monad` instance via `flatMap`.

`Applicative` includes all combinators present in [`Functor`]({{ '/docs/typeclasses/functor/' | relative_url }}).

### Applicative Builder examples

Often times we find ourselves in situations where we need to compute multiple independent values resulting from operations that do not depend on each other.

In the following example we will define 3 invocations that may as well be remote or local services each one of them returning different results in the same computational context of `Option`

```kotlin:ank
import kategory.*

fun profileService(): Option<String> = Option("Alfredo Lambda")
fun phoneService(): Option<Int> = Option(55555555)
fun addressService(): Option<List<String>> = Option(listOf("1 Main Street", "11130", "NYC"))
```

This more or less illustrate the common use case of performing several independent operations where we need to get all the results together

Kategory features an [Applicative Builder]({{ '/docs/patterns/applicative_builder' | relative_url }}) that allows you to easily combine all the independent operations into one result.

```kotlin:ank
data class Profile(val name: String, val phone: Int, val address: List<String>)

val r: Option<Tuple3<String, Int, List<String>>> = Option.applicative().tupled(profileService(), phoneService(), addressService()).ev()
r.map { Profile(it.a, it.b, it.c) }
```

The Applicative Builder also provides a `map` operations that is able to abstract over arity in the same way as `tupled`

```kotlin:ank
Option.applicative().map(profileService(), phoneService(), addressService(), { (name, phone, addresses) ->
  Profile(name, phone, addresses)
})
```

### Main Combinators

#### pure

Lifts a value into the computational context of a type constructor

`fun <A> pure(a: A): HK<F, A>`

```kotlin:ank
Option.pure(1) // Option(1)
```

#### ap

Apply a function inside the type constructor's context

`fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>`

```kotlin:ank
Option.applicative().ap(Option(1), Option({ n: Int -> n + 1 })) // Option(2)
```

#### Other combinators

For a full list of other useful combinators available in `Applicative` see the [Source][applicative_source]{:target="_blank"}

### Syntax

#### HK<F, A>#pure

Lift a value into the computational context of a type constructor

```kotlin:ank
1.pure<OptionHK, Int>()
```

#### HK<F, A>#ap

Apply a function inside the type constructor's context

```kotlin:ank
Option(1).ap(Option({ n: Int -> n + 1 }))
```

#### HK<F, A>#map2

Map 2 values inside the type constructor context and apply a function to their cartesian product

```kotlin:ank
Option.applicative().map2(Option(1), Option("x"), { z: Tuple2<Int, String> ->  "${z.a}${z.b}" })
```

#### HK<F, A>#map2Eval

Lazily map 2 values inside the type constructor context and apply a function to their cartesian product.
Computation happens when `.value()` is invoked.

```kotlin:ank
Option.applicative().map2Eval(Option(1), Eval.later { Option("x") }, { z: Tuple2<Int, String> ->  "${z.a}${z.b}" }).value()
```


### Laws

Kategory provides [`ApplicativeLaws`][applicative_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Applicative instances.

#### Creating your own `Applicative` instances

Kategory already provides Applicative instances for most common datatypes both in Kategory and the Kotlin stdlib.

[//]: See [Deriving and creating custom typeclass] to provide your own Applicative instances for custom datatypes. //TODO: commented until section is created

### Data types

The following datatypes in Kategory provide instances that adhere to the `Applicative` typeclass.

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
- [Const]({{ '/docs/datatypes/const' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Eval]({{ '/docs/datatypes/eval' | relative_url }})
- [IO]({{ '/docs/effects/io' | relative_url }})
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [Id]({{ '/docs/datatypes/id' | relative_url }})
- [Function0]({{ '/docs/datatypes/function0' | relative_url }})

Additionally all instances of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) and their MTL variants implement the `Applicative` typeclass directly
since they are all subtypes of `Applicative`.

[applicative_source]: https://github.com/kategory/kategory/blob/master/kategory-core/src/main/kotlin/kategory/typeclasses/Applicative.kt
[applicative_law_source]: https://github.com/kategory/kategory/blob/master/kategory-test/src/main/kotlin/kategory/laws/ApplicativeLaws.kt

