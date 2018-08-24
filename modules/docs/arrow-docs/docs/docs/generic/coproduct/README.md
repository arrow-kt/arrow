---
layout: docs
title: Generic
permalink: /docs/generic/coproduct/
---

## Arrow Generic

{:.beginner}
beginner

`arrow-generic` provides meta programming facilities over Product types like data classes, tuples, and heterogeneous lists; and Coproduct types like sealed classes.

### Install

```groovy
compile 'io.arrow-kt:arrow-generic:$arrow_version'
```

### Features

#### Coproduct

Coproducts represent a container in which only one of the specified set of types exist. It's very similar in `Either` in that respect. `Either` supports one of two values, an `Either<A, B>` has to contain an instance of `A` or `B`. We can extrapolate that concept to `N` number of types. So a `Coproduct5<A, B, C, D, E>` has to contain an instance of `A`, `B`, `C`, `D`, or `E`. For example, perhaps there's a search function for a Car Dealer app that can show `Dealership`s, `Car`s and `SalesPerson`s, we could model that list of results as `List<Coproduct3<Dealership, Car, SalesPerson>`. The result would contain a list of heterogeneous elements but each element is one of `Dealership`, `Car` or `SalesPerson` and our UI can render the list elements based on those types.

Let's say we have an api. Our api operates under the following conditions:
- Every endpoint could tell the client, `ServerError`, `UserUnauthorized` or `OverRequestLimit`.
- For specific endpoints, we may have specific results to the context of what they do, for example, an endpoint for registering a `Car` for some service. This endpoint could return to us `CarAlreadyRegistered`, `StolenCar` or `SuccessfullyRegistered`. For fun, let's say the `SuccessfullyRegistered` response also contains a `Registration` object with some data.

Because we have some common errors that every endpoint can return to us, we can define a sealed class of these and call them `CommonServerError` because they make sense to be sealed together for reusability. Likewise, we can logically group our specific errors into `RegistrationError` and we have `Registration` as our success type.

With Coproducts, we're able to define a result for this api call as `Coproduct3<CommonServerError, RegistrationError, Registration>`. We've been able to compose these results without having to write our own sealed class containing all the common errors for each endpoint.

#### Extensions

##### coproductOf

So now that we've got our api response modeled, we need to be able to create an instance of `Coproduct3`.

```kotlin:ank
coproductOf<CommonServerError, RegistrationError, Registration>(ServerError) //Returns Coproduct3<CommonServerError, RegistrationError, Registration>
```

There are `coproductOf` constructor functions for each Coproduct. All we have to do is pass in our value and have the correct type parameters on it, the value must be a Type declared on the function call.

If we pass in a value that doesn't correspond to any types on the Coproduct, it won't compile:
```kolint:ank
coproductOf<String, RegistrationError, Registration>(ServerError) //Doesn't compile
```

##### cop

You might be saying "That's great and all but passing in values as parameters is so Java, I want something more Kotlin!". Well look no further, just like `Either`'s `left()` and `right()` extension methods, Coproducts can be created with an extension method on any type:

```kotlin:ank
ServerError.cop<CommonServerError, RegistrationError, Registration>() //Returns Coproduct3<CommonServerError, RegistrationError, Registration>
```

All we have to do is provide the type parameters we can make a Coproduct using the `cop` extension method. Just like `coproductOf`, if the Type of the value isn't in the Type parameters of the method call, it won't compile:

```kotlin:ank
ServerError.cop<String, RegistrationError, Registration>() //Doesn't compile
```

##### fold

Obviously, we're not just modeling errors for fun, we're going to handle them! All Coproducts have `fold` which allows us to condense the Coproduct down to a single type. For example, we could handle errors as such in a UI:

```kotlin:ank
fun renderApiResult(apiResult: Coproduct3<CommonServerError, RegistrationError, Registration>) = apiResult.fold(
            { commonError ->
                when (commonError) {
                    ServerError -> //Show error
                    UserUnauthorized -> //Log out user
                    OverRequestLimit -> //Show error
                }
            },
            { registrationError ->
                when (registrationError) {
                    RegistrationAlreadyExists -> //Show that the car is already registered
                    CarStolen -> //Call the police!
                }
            },
            { registration ->
                val registeredCar = registration.car

                //Render success! Yay!
            }
    )
```

This example likely would return `Unit` from the `fold` as there's likely not a common type to be returned for showing various UI elements and this is likely all side effects, let's say our application was built for a command line and we just have to show a `String` for the result of the call (if only it was always that easy):

```kotlin:ank

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
                    RegistrationAlreadyExists -> "Car already registered."
                    CarStolen -> "Car reported stolen!"
                }
            },
            { registration ->
                "Successfully Registered: $registration.car"
            }
    )
```

Here we're able to return the result of the `fold` and we're forced to handle all cases! Neat!

##### select

Let's say we also want to store the `Registration` object into our database when we successfully register a car, because we're a good offline first application like that. We don't really want to have to `fold` over every single case just to handle something for the `Registration`, this is where `select<T>` comes to the rescue! We're able to take a Coproduct and `select` the type we care about from it. `select` returns an `Option`, if the value of the Coproduct was for the type you're trying to `select`, you'll get `Some`, if it was not the type used with `select`, you'll get `None`.

```kotlin:ank
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
fun handleApiResult(apiResult: Coproduct3<CommonServerError, RegistrationError, Registration>): Unit {
    apiResult.select<String>() //Doesn't compile
}
```
