---
layout: docs
title: Generic
permalink: /docs/generic/coproduct/
---

## Arrow Generic

{:.beginner}
beginner

`arrow-generic` provides meta programming facilities over Product types (data classes, tuples, heterogeneous lists...). It also provides the Coproduct type, similar to sealed classes.

### Install

```groovy
compile 'io.arrow-kt:arrow-generic:$arrow_version'
```

### Features

#### Coproduct

Coproducts represent a sealed hierarchy of types where only one of the specified set of types exist every time. Conceptually, it's very similar to the stdlib `sealed` class, or to [Either]({{ '/docs/arrow/core/either' | relative_url }}) if we move on to Arrow data types. [Either]({{ '/docs/arrow/core/either' | relative_url }}) supports one of two values, an `Either<A, B>` has to contain an instance of `A` or `B`. We can extrapolate that concept to `N` number of types. So a `Coproduct5<A, B, C, D, E>` has to contain an instance of `A`, `B`, `C`, `D`, or `E`. For example, perhaps there's a search function for a Car Dealer app that can show `Dealership`s, `Car`s and `SalesPerson`s in a list, we could model that list of results as `List<Coproduct3<Dealership, Car, SalesPerson>`. The result would contain a list of heterogeneous elements but each element is one of `Dealership`, `Car` or `SalesPerson` and our UI can render the list elements based on those types.

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.fold

fun toDisplayValues(items: List<Coproduct3<Car, Dealership, Salesperson>>): List<String> {
  return items.map {
    it.fold(
            { "Car: Speed: ${it.speed.kmh}" },
            { "Dealership: ${it.location}" },
            { "Salesperson: ${it.name}"}
    )
  }
}
```

Let's say we have an api. Our api operates under the following conditions:
- Every endpoint could tell the client, `ServerError`, `UserUnauthorized` or `OverRequestLimit`.
- For specific endpoints, we may have specific results to the context of what they do, for example, an endpoint for registering a `Car` for some service. This endpoint could return to us `CarAlreadyRegistered`, `StolenCar` or `SuccessfullyRegistered`. For fun, let's say the `SuccessfullyRegistered` response also contains a `Registration` object with some data.

Because we have some common errors that every endpoint can return to us, we can define a sealed class of these and call them `CommonServerError` because they make sense to be sealed together for reusability. Likewise, we can logically group our specific errors into `RegistrationError` and we have `Registration` as our success type.

With Coproducts, we're able to define a result for this api call as `Coproduct3<CommonServerError, RegistrationError, Registration>`. We've been able to compose these results without having to write our own sealed class containing all the common errors for each endpoint.

#### Extensions

##### coproductOf

So now that we've got our api response modeled, we need to be able to create an instance of `Coproduct3`.

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.coproductOf

val apiResult = coproductOf<CommonServerError, RegistrationError, Registration>(ServerError) //Returns Coproduct3<CommonServerError, RegistrationError, Registration>
```

There are `coproductOf` constructor functions for each Coproduct regardless of the arity (number of types). All we have to do is pass in our value and have the correct type parameters on it, the value must be a type declared on the function call.

If we pass in a value that doesn't correspond to any types on the Coproduct, it won't compile:

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.coproductOf

//val apiResult = coproductOf<String, RegistrationError, Registration>(ServerError)
//error: type mismatch: inferred type is ServerError but String was expected
```

##### cop

You might be saying "That's great and all but passing in values as parameters is so Java, I want something more Kotlin!". Well look no further, just like [Either]({{ '/docs/arrow/core/either' | relative_url }})'s `left()` and `right()` extension methods, Coproducts can be created with an extension method on any type:

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.cop

val apiResult = ServerError.cop<CommonServerError, RegistrationError, Registration>() //Returns Coproduct3<CommonServerError, RegistrationError, Registration>
```

All we have to do is provide the type parameters and we can make a Coproduct using the `cop` extension method. Just like `coproductOf`, if the type of the value isn't in the type parameters of the method call, it won't compile:

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.cop

//val apiResult = ServerError.cop<String, RegistrationError, Registration>()
//error: type mismatch: inferred type is ServerError but String was expected
```

##### fold

Obviously, we're not just modeling errors for fun, we're going to handle them! All Coproducts have `fold` which allows us to condense the Coproduct down to a single type. For example, we could handle errors as such in a UI:

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.fold

fun handleCommonError(commonError: CommonServerError) {
}

fun showCarAlreadyRegistered() {
}

fun callPolice() {
}

fun showCarSuccessfullyRegistered(car: Car) {
}

fun renderApiResult(apiResult: Coproduct3<CommonServerError, RegistrationError, Registration>) = apiResult.fold(
            { commonError -> handleCommonError(commonError) },
            { registrationError ->
                when (registrationError) {
                    CarAlreadyRegistered -> showCarAlreadyRegistered()
                    StolenCar -> callPolice()
                }
            },
            { registration ->
                val registeredCar = registration.car

                showCarSuccessfullyRegistered(registeredCar)
            }
    )
```

This example returns `Unit` because all of these are side effects, let's say our application was built for a command line and we just have to show a `String` for the result of the call (if only it was always that easy):

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.fold

fun renderApiResult(apiResult: Coproduct3<CommonServerError, RegistrationError, Registration>): String = apiResult.fold(
            { commonError ->
                when (commonError) {
                    ServerError -> "Server error, try again later."
                    UserUnauthorized -> "Unauthorized!"
                    OverRequestLimit -> "Too many api requests, try again later."
                }
            },
            { registrationError ->
                when (registrationError) {
                    CarAlreadyRegistered -> "Car already registered."
                    StolenCar -> "Car reported stolen!"
                }
            },
            { registration ->
                "Successfully Registered: $registration.car"
            }
    )
```

Here we're able to return the result of the `fold` and since it's exhaustively evaluated, we're forced to handle all cases! Neat! Let's say we also want to store the `Registration` object into our database when we successfully register a car. We don't really want to have to `fold` over every single case just to handle something for the `Registration`, this is where `select<T>` comes to the rescue!

##### select

We're able to take a Coproduct and `select` the type we care about from it. `select` returns an `Option`, if the value of the Coproduct was for the type you're trying to `select`, you'll get `Some`, if it was not the type used with `select`, you'll get `None`.

```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.select

fun handleApiResult(
        database: Database,
        apiResult: Coproduct3<CommonServerError, RegistrationError, Registration>
): Unit {
    apiResult.select<Registration>()
            .fold(
                    {}, //Wasn't Registration, nothing to do here
                    { database.insertRegistration(it) }
            )
}
```

`select` can only be called with a type that exists on the Coproduct, if the type doesn't exist, it won't compile:
```kotlin:ank
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.select

fun handleApiResult(apiResult: Coproduct3<CommonServerError, RegistrationError, Registration>): Unit {
//    apiResult.select<String>()
//error: type mismatch: inferred type is Coproduct3<CommonServerError, RegistrationError, Registration> but Coproduct3<String, *, *> was expected
}
```
