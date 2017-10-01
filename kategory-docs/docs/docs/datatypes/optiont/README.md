---
layout: docs
title: OptionT
permalink: /docs/datatypes/optiont/
---

## OptionT

`OptionT` also known as the `Option` monad transformer allows to compute inside the context when `Option` is nested in a different monad.

One issue we face with monads is that they don't compose. This can cause your code to get really hairy when trying to combine structures like `ObservableKW` and `Option`. But there's a simple solution, and we're going to explain how you can use Monad Transformers to alleviate this problem.

For our purposes here, we're going to utilize a monad that serves as a container that may hold a value and where a computation can be performed.

Given that both `ObservableKW<A>` and `Option<A>` would be examples of datatypes that provide instances for the `Monad` typeclasses.

Because [monads don't compose](http://tonymorris.github.io/blog/posts/monads-do-not-compose/), we may end up with nested structures such as `ObservableKW<Option<ObservableKW<Option<A>>>` when using `ObservableKW` and `Option` together. Using Monad Transformers can help us to reduce this boilerplate.

In the most basic of scenarios, we'll only be dealing with one monad at a time making our lives nice and easy. However, it's not uncommon to get into scenarios where some function calls will return `ObservableKW<A>`, and others will return `Option<A>`.

So let's test this out with an example:

```kotlin:ank
import kategory.*
import kategory.Option.*

data class Country(val code: Option<String>)
data class Address(val id: Int, val country: Option<Country>)
data class Person(val name: String, val address: Option<Address>)

fun getCountryCode(maybePerson : Option<Person>): Option<String> =
  maybePerson.flatMap { person ->
    person.address.flatMap { address ->
      address.country.flatMap { country ->
        country.code
      }
    }
  }
```

Nested flatMap calls flatten the `Option` but the resulting function starts looking like a pyramid and can easily lead to callback hell.

We can further simplify this case by using Kategory `binding` facilities
that enables monad comprehensions for all datatypes for which a monad instance is available.

```kotlin:ank
fun getCountryCode(maybePerson : Option<Person>): Option<String> =
  Option.monad().binding {
    val person = maybePerson.bind()
    val address = person.address.bind()
    val country = address.country.bind()
    val code = country.code.bind()
    yields(code)
  }.ev()
```

Alright, a piece of cake right? That's because we were dealing with a simple type `Option`. But here's where things can get more complicated. Let's introduce another monad in the middle of the computation. For example what happens when we need to load a person by id, then their address and country to obtain the country code from a remote service?

Consider this simple database representation:

```kotlin:ank
val personDB: Map<Int, Person> = mapOf(
  1 to Person(
        name = "Alfredo Lambda",
        address = Some(
          Address(
            id = 1,
            country = Some(
              Country(
                code = Some("ES")
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
        code = Some("ES")
      )
    )
  )
)
```

Now we've got two new functions in the mix that are going to call a remote service, and they return a `ObservableKW`. This is common in most APIs that handle loading asynchronously.

```kotlin:ank
import kategory.effects.*

fun findPerson(personId : Int) : ObservableKW<Option<Person>> =
  ObservableKW.pure(Option.fromNullable(personDB.get(personId))) //mock impl for simplicity

fun findCountry(addressId : Int) : ObservableKW<Option<Country>> =
  ObservableKW.pure(
    Option.fromNullable(adressDB.get(addressId)).flatMap { it.country }
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


This isn't actually what we want since the inferred return type is `ObservableKW<Option<Option<ObservableKW<Option<Option<String>>>>>>`. We can't use flatMap in this case because the nested expression does not match the return type of the expression they're contained within. This is because we're not flatMapping properly over the nested types.

 Still not ideal. The levels of nesting are pyramidal with `flatMap` and `map` and are as deep as the number of operations that you have to perform.

Let's look at how a similar implementation would look like using monad comprehensions without transformers:

```kotlin:ank
fun getCountryCode(personId: Int): ObservableKW<Option<String>> =
      ObservableKW.monad().binding {
        val maybePerson = findPerson(personId).bind()
        val person = maybePerson.fold(
          { ObservableKW.raiseError<Person>(NoSuchElementException("...")) },
          { ObservableKW.pure(it) }
        ).bind()
        val address = person.address.fold(
          { ObservableKW.raiseError<Address>(NoSuchElementException("...")) },
          { ObservableKW.pure(it) }
        ).bind()
        val maybeCountry = findCountry(address.id).bind()
        val country = maybeCountry.fold(
          { ObservableKW.raiseError<Country>(NoSuchElementException("...")) },
          { ObservableKW.pure(it) }
        ).bind()
        yields(country.code)
      }.ev()
```

While we've got the logic working now, we're in a situation where we're forced to deal with the `None cases`. We also have a ton of boilerplate type conversion with `fold`. The type conversion is necessary because in a monad comprehension you can only use a type of Monad. If we start with `ObservableKW`, we have to stay in it’s monadic context by lifting anything we compute sequentially to a `ObservableKW` whether or not it's async.

This is a commonly encountered problem, especially in the context of async services. So how can we reconcile the fact that we're mixing `Option` and `ObservableKW`?

### Monad Transformers to the Rescue!

Monad Transformers enable you to combine two monads into a super monad. In this case, we're going to use `OptionT`
from Kategory to express the effect of potential absence inside our async computations.

`OptionT` has the form of `OptionT<F, A>`.

This means that for any monad `F` surrounding an `Option<A>` we can obtain an `OptionT<F, A>`.
So our specialization `OptionT<ObservableKWHK, A>` is the OptionT transformer around values that are of `ObservableKW<Option<A>>`.

We can now lift any value to a `OptionT<F, A>` which looks like this:

```kotlin:ank
val optTVal = 1.pure<OptionTKindPartial<ObservableKWHK>, Int>()
optTVal
```

or

```kotlin:ank
val optTVal = OptionT.fromOption<ObservableKWHK, Int>(1.some())
optTVal
```

And back to the `ObservableKW<Option<A>>` running the transformer

```kotlin:ank
optTVal.value()
```

So how would our function look if we implemented it with the OptionT monad transformer?

```kotlin
fun getCountryCode(personId: Int): ObservableKW<Option<String>> =
  OptionT.monad<ObservableKWHK>().binding {
    val person = OptionT(findPerson(personId)).bind()
    val address = OptionT(ObservableKW.pure(person.address)).bind()
    val country = OptionT(findCountry(address.id)).bind()
    val code = OptionT(ObservableKW.pure(country.code)).bind()
    yields(code)
  }.value().ev()
```

Here we no longer have to deal with the `None` cases, and the binding to the values on the left side are already the underlying values we want to focus on instead of the optional values. We have automatically `flatMapped` through the `ObservableKW` and `Option` in a single expression reducing the boilerplate and encoding the effects concerns in the type signatures.

Available Instances:

```kotlin:ank
import kategory.debug.*

showInstances<OptionTKindPartial<ObservableKWHK>, Unit>()
```

Take a look at the [`EitherT` docs]({{ '/docs/datatypes/eithert' | relative_url }}) for an alternative version of this content with the `EitherT` monad transformer

## Credits

Contents partially adapted from [FP for the avg Joe at the 47 Degrees blog](https://www.47deg.com/blog/fp-for-the-average-joe-part-2-scalaz-monad-transformers/)
