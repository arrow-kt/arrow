---
layout: docs
title: EitherT
permalink: /docs/datatypes/eithert/
---


## EitherT

`EitherT` also known as the `Either` monad transformer allows to compute inside the context when `Either` is nested in a different monad.
 
One issue we face with monads is that they don't compose. This can cause your code to get really hairy when trying to combine structures like `ObservableKW` and `Either`. But there's a simple solution, and we're going to explain how you can use Monad Transformers to alleviate this problem.

For our purposes here, we're going to utilize a monad that serves as a container that may hold a value and where a computation can be performed.

Given that both `ObservableKW<A>` and `Either<L, A>` would be examples of datatypes that provide instances for the `Monad` typeclasses.

Because [monads don't compose](http://tonymorris.github.io/blog/posts/monads-do-not-compose/), we may end up with nested structures such as `ObservableKW<Either<BizError, ObservableKW<Either<BizError, A>>>` when using `ObservableKW` and `Either` together. Using Monad Transformers can help us to reduce this boilerplate.

In the most basic of scenarios, we'll only be dealing with one monad at a time making our lives nice and easy. However, it's not uncommon to get into scenarios where some function calls will return `ObservableKW<A>`, and others will return `Either<BizError, A>`.

So let's test this out with an example:

```kotlin:ank
import kategory.*
import kategory.Either.*
import kategory.Option.*

data class Country(val code: String)
data class Address(val id: Int, val country: Option<Country>)
data class Person(val id: Int, val name: String, val address: Option<Address>)
```

To model our known errors we will use an algebraic datatype expressed in Kotlin as a sealed hierarchy.

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
fun getCountryCode(maybePerson : Either<BizError, Person>): Either<BizError, String> = 
  maybePerson.flatMap { person ->
    person.address.toEither({ AddressNotFound(person.id) }).flatMap { address ->
      address.country.fold({ CountryNotFound(address.id).left() }, { it.code.right() })
    }
  }
```

Nested flatMap calls flatten the `Either` but the resulting function starts looking like a pyramid and can easily lead to callback hell.

We can further simplify this case by using Kategory `binding` facilities
that enables monad comprehensions for all datatypes for which a monad instance is available.

```kotlin:ank
fun getCountryCode(maybePerson : Either<BizError, Person>): Either<BizError, String> = 
  Either.monadError<BizError>().binding {
    val person = maybePerson.bind()
    val address = person.address.toEither({ AddressNotFound(person.id) }).bind()
    val country = address.country.toEither({ CountryNotFound(address.id) }).bind()
    yields(country.code)
  }.ev()
```

Alright, a piece of cake right? That's because we were dealing with a simple type `Either`. But here's where things can get more complicated. Let's introduce another monad in the middle of the computation. For example what happens when we need to load a person by id, then their address and country to obtain the country code from a remote service?

Consider this simple database representation:

```kotlin:ank
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

Now we've got two new functions in the mix that are going to call a remote service, and they return a `ObservableKW`. This is common in most APIs that handle loading asynchronously.

```kotlin:ank
import kategory.effects.*

fun findPerson(personId : Int) : ObservableKW<Either<BizError, Person>> = 
  ObservableKW.pure(
    Option.fromNullable(personDB.get(personId)).toEither { PersonNotFound(personId) }
  ) //mock impl for simplicity
  
fun findCountry(addressId : Int) : ObservableKW<Either<BizError, Country>> = 
  ObservableKW.pure(
    Option.fromNullable(adressDB.get(addressId))
      .flatMap { it.country }
      .toEither { CountryNotFound(addressId) }
  ) //mock impl for simplicity
  
```
    
A naive implementation attempt to get to a `country.code` from a `person.id` might look something like this.

```kotlin:ank
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
 
 Still not ideal. The levels of nesting are pyramidal with `flatMap` and `map` and are as deep as the number of operations that you have to perform.

Let's look at how a similar implementation would look like using monad comprehensions without transformers:

```kotlin:ank
fun getCountryCode(personId: Int): ObservableKW<Either<BizError, String>> = 
      ObservableKW.monad().binding {
        val person = findPerson(personId).bind()
        val address = person.fold (
          { it.left() }, 
          { it.address.toEither { AddressNotFound(personId) } }
        )
        val maybeCountry = address.fold(
          { ObservableKW.pure(it.left()) }, 
          { findCountry(it.id) }
        ).bind()
        val code = maybeCountry.fold(
            { it.left() }, 
            { it.code.right() }
        )
        yields(code)
      }.ev()
```

While we've got the logic working now, we're in a situation where we're forced to deal with the `Left cases`. We also have a ton of boilerplate type conversion with `fold`. The type conversion is necessary because in a monad comprehension you can only use a type of Monad. If we start with `ObservableKW`, we have to stay in it’s monadic context by lifting anything we compute sequentially to a `ObservableKW` whether or not it's async.

This is a commonly encountered problem, especially in the context of async services. So how can we reconcile the fact that we're mixing `Either` and `ObservableKW`?

### Monad Transformers to the Rescue!

Monad Transformers enable you to combine two monads into a super monad. In this case, we're going to use `EitherT`
from Kategory to express the effect of potential known controled biz error inside our async computations.

`EitherT` has the form of `EitherT<F, L, A>`.

This means that for any monad `F` surrounding an `Either<L, A>` we can obtain an `EitherT<F, L, A>`.
So our specialization `EitherT<ObservableKWHK, BizError, A>` is the EitherT transformer around values that are of `ObservableKW<Either<BizError, A>>`.

We can now lift any value to a `EitherT<F, BizError, A>` which looks like this:

```kotlin:ank
val eitherTVal = 1.pure<EitherTKindPartial<ObservableKWHK, BizError>, Int>()
eitherTVal
```

And back to the `ObservableKW<Either<BizError, A>>` running the transformer

```kotlin:ank
eitherTVal.value()
```

So how would our function look if we implemented it with the EitherT monad transformer?

```kotlin
fun getCountryCode(personId: Int): ObservableKW<Either<BizError, String>> =
  EitherT.monadError<ObservableKWHK, BizError>().binding {
    val person = EitherT(findPerson(personId)).bind()
    val address = EitherT(ObservableKW.pure(
      person.address.toEither { AddressNotFound(personId) }
    )).bind()
    val country = EitherT(findCountry(address.id)).bind()
    yields(country.code)
  }.value()
```

Here we no longer have to deal with the `Left` cases, and the binding to the values on the left side are already the underlying values we want to focus on instead of the potential biz error values. We have automatically `flatMapped` through the `ObservableKW` and `Either` in a single expression reducing the boilerplate and encoding the effects concerns in the type signatures.

Available Instances:

```kotlin:ank
import kategory.debug.*

showInstances<EitherTKindPartial<ObservableKWHK, BizError>, BizError>()
```

Take a look at the [`OptionT` docs](/docs/datatypes/optiont) for an alternative version of this content with the `OptionT` monad transformer
 
## Credits
 
Contents partially adapted from [FP for the avg Joe at the 47 Degrees blog](https://www.47deg.com/blog/fp-for-the-average-joe-part-2-scalaz-monad-transformers/)
