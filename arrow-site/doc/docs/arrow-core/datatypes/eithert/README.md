---
layout: docs-core
title: EitherT
permalink: /docs/arrow/data/eithert/
redirect_from:
  - /docs/datatypes/eithert/
video: 1h4X8CrMjVs
---


## EitherT

{:.advanced}
advanced

`EitherT`, also known as the `Either` monad transformer, allows you to compute inside the context when `Either` is nested in a different monad.

One issue we face with monads is that they don't compose. This can cause your code to get really hairy when trying to combine structures like `ObservableK` and `Either`. But there's a simple solution, and we're going to explain how you can use Monad Transformers to alleviate this problem.

For our purposes here, we're going to utilize a monad that serves as a container that may hold a value and where a computation can be performed.

Both `ObservableK<A>` and `Either<L, A>` would be examples of datatypes that provide instances for the `Monad` typeclasses.

Because [monads don't compose](http://tonymorris.github.io/blog/posts/monads-do-not-compose/), we may end up with nested structures such as `ObservableK<Either<BizError, ObservableK<Either<BizError, A>>>` when using `ObservableK` and `Either` together. Using Monad Transformers can help us to reduce this boilerplate.

In the most basic of scenarios, we'll only be dealing with one monad at a time, making our lives nice and easy. However, it's not uncommon to get into scenarios where some function calls will return `ObservableK<A>`, and others will return `Either<BizError, A>`.

So let's test this out with an example:

```kotlin:ank
import arrow.core.Option

data class Country(val code: String)
data class Address(val id: Int, val country: Option<Country>)
data class Person(val id: Int, val name: String, val address: Option<Address>)
```

To model our known errors, we will use an algebraic datatype expressed in Kotlin as a sealed hierarchy.

```kotlin:ank
sealed class BizError {
  data class PersonNotFound(val personId: Int): BizError()
  data class AddressNotFound(val personId: Int): BizError()
  data class CountryNotFound(val addressId: Int): BizError()
}

typealias PersonNotFound = BizError.PersonNotFound
typealias AddressNotFound = BizError.AddressNotFound
typealias CountryNotFound = BizError.CountryNotFound
```

We can now implement a naive lookup function to obtain the country code given a person result.

```kotlin:ank
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

fun getCountryCode(maybePerson : Either<BizError, Person>): Either<BizError, String> =
  maybePerson.flatMap { person ->
    person.address.toEither { AddressNotFound(person.id) }.flatMap { address ->
      address.country.fold({ CountryNotFound(address.id).left() }, { it.code.right() })
    }
  }
```

Nested flatMap calls flatten the `Either`, but the resulting function starts looking like a pyramid and can easily lead to callback hell.

We can further simplify this case by using Arrow `fx` facilities
that enable monad comprehensions for all datatypes for which a monad instance is available.

```kotlin:ank
import arrow.core.extensions.fx
import arrow.core.fix

fun getCountryCode(maybePerson : Either<BizError, Person>): Either<BizError, String> =
  Either.fx<BizError, String> {
    val (person) = maybePerson
    val (address) = person.address.toEither { AddressNotFound(person.id) }
    val (country) = address.country.toEither { CountryNotFound(address.id)}
    country.code
  }.fix()
```

Alright, a piece of cake right? That's because we were dealing with a simple type `Either`. But here's where things can get more complicated. Let's introduce another monad in the middle of the computation. For example, what happens when we need to load a person by id, then their address and country to obtain the country code from a remote service?

Consider this simple database representation:

```kotlin:ank
import arrow.core.Some

val personDB: Map<Int, Person> = mapOf(
  1 to Person(
        id = 1,
        name = "Alfredo Lambda",
        address = Some(
          Address(
            id = 1,
            country = Some(
              Country(
                code = "ES"
              )
            )
          )
        )
      )
)

val adressDB: Map<Int, Address> = mapOf(
  1 to Address(
    id = 1,
    country = Some(
      Country(
        code = "ES"
      )
    )
  )
)
```

Now we've got two new functions in the mix that are going to call a remote service, and they return a `ObservableK`. This is common in most APIs that handle loading asynchronously.

```kotlin:ank
import arrow.fx.rx2.ObservableK

fun findPerson(personId: Int): ObservableK<Either<BizError, Person>> =
  ObservableK.just(
    Option.fromNullable(personDB.get(personId)).toEither { PersonNotFound(personId) }
  ) //mock impl for simplicity

fun findCountry(addressId: Int): ObservableK<Either<BizError, Country>> =
  ObservableK.just(
    Option.fromNullable(adressDB.get(addressId))
      .flatMap { it.country }
      .toEither { CountryNotFound(addressId) }
  ) //mock impl for simplicity
```

A naive implementation attempt to get to a `country.code` from a `person.id` might look something like this.

```kotlin:ank
import arrow.fx.rx2.extensions.fx

fun getCountryCode(personId: Int) =
  findPerson(personId).map { maybePerson ->
    maybePerson.map { person ->
      person.address.map { address ->
        findCountry(address.id).map { maybeCountry ->
          maybeCountry.map { country ->
            country.code
          }
        }
      }  
    }
  }

val lifted = { personId: Int -> getCountryCode(personId) }
lifted
```


This isn't actually what we want since the inferred return type shows we are staking effects in a nested fashion.
We can't use flatMap in this case because the nested expression does not match the return type of the expression they're contained within. This is because we're not flatMapping properly over the nested types.

 Still not ideal. The levels of nesting are pyramidal with `flatMap` and `map`, and are as deep as the number of operations that you have to perform.

Let's look at how a similar implementation would look using monad comprehensions without transformers:

```kotlin:ank
import arrow.fx.rx2.extensions.fx

fun getCountryCode(personId: Int): ObservableK<Either<BizError, String>> =
  ObservableK.fx {
    val person = !findPerson(personId)
    val address = person.fold(
      { it.left() },
      { it.address.toEither { AddressNotFound(personId) } }
    )
    val maybeCountry = !address.fold(
      { ObservableK.just(it.left()) },
      { findCountry(it.id) }
    )
    val code = maybeCountry.fold(
      { it.left() },
      { it.code.right() }
    )
    code
  }
```

While we've got the logic working now, we're in a situation where we're forced to deal with the `Left cases`. We also have a ton of boilerplate type conversion with `fold`. The type conversion is necessary because, in a monad comprehension, you can only use a type of Monad. If we start with `ObservableK`, we have to stay in its monadic context by lifting anything we compute sequentially to a `ObservableK`, whether or not it's async.

This is a commonly encountered problem, especially in the context of async services. So how can we reconcile the fact that we're mixing `Either` and `ObservableK`?

### Monad Transformers to the Rescue!

Monad Transformers enable you to combine two monads into a super monad. In this case, we're going to use `EitherT`
from Arrow to express the effect of potential known controled biz error inside our async computations.

`EitherT` has the form of `EitherT<F, L, A>`.

This means that, for any monad `F` surrounding an `Either<L, A>`, we can obtain an `EitherT<F, L, A>`.
So our specialization `EitherT<ForObservableK, BizError, A>` is the EitherT transformer around values that are of `ObservableK<Either<BizError, A>>`.

We can now lift any value to a `EitherT<F, BizError, A>`, which looks like this:

```kotlin:ank
import arrow.fx.rx2.ForObservableK
import arrow.mtl.EitherT
import arrow.fx.rx2.extensions.observablek.applicative.applicative

val eitherTVal = EitherT.just<ForObservableK, BizError, Int>(ObservableK.applicative(), 1)
eitherTVal
```

And back to the `ObservableK<Either<BizError, A>>` running the transformer

```kotlin:ank
import arrow.mtl.value

eitherTVal.value()
```

So how would our function look if we implemented it with the EitherT monad transformer?

```kotlin:ank
import arrow.fx.rx2.extensions.observablek.monad.monad
import arrow.fx.rx2.fix
import arrow.mtl.extensions.eithert.monad.monad

fun getCountryCode(personId: Int): ObservableK<Either<BizError, String>> =
  EitherT.monad<ForObservableK, BizError>(ObservableK.monad()).fx.monad {
    val (person) = EitherT(findPerson(personId))
    val address = !EitherT(ObservableK.just(
      person.address.toEither { AddressNotFound(personId) }
    ))
    val (country) = EitherT(findCountry(address.id))
    country.code
  }.value().fix()
```

Here we no longer have to deal with the `Left` cases, and the binding to the values on the left side are already the underlying values we want to focus on instead of the potential biz error values. We have automatically `flatMapped` through the `ObservableK` and `Either` in a single expression, reducing the boilerplate and encoding the effects concerns in the type signatures.

## Additional syntax

As `EitherT<F, A ,B>` allows the manipulation of the nested `Either` structure, it provides a `mapLeft` method to map over the left element of nested Eithers.

```kotlin:ank
import arrow.core.extensions.option.functor.functor

EitherT(Option(3.left())).mapLeft(Option.functor(), {it + 1})
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.DataType
import arrow.reflect.tcMarkdownList
import arrow.mtl.EitherT

DataType(EitherT::class).tcMarkdownList()
```

Take a look at the [`OptionT` docs]({{ '/docs/arrow/data/optiont' | relative_url }}) for an alternative version of this content with the `OptionT` monad transformer

## Credits

Contents partially adapted from [FP for the avg Joe at the 47 Degrees blog](https://www.47deg.com/blog/fp-for-the-average-joe-part-2-scalaz-monad-transformers/)
