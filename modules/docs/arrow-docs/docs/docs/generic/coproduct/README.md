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

```kotlin:ank:playground
import arrow.generic.*
import arrow.generic.coproduct3.*

fun toDisplayValues(items: List<Coproduct3<Car, Dealership, Salesperson>>): List<String> {
  return items.map {
    it.fold(
            { "Car: Speed: ${it.speed.kmh}" },
            { "Dealership: ${it.location}" },
            { "Salesperson: ${it.name}"}
    )
  }
}

fun main() {
    println(
        toDisplayValues(
            listOf<Coproduct3<Car, Dealership, Salesperson>>(
                Car(Speed(100)).first(),
                Dealership("Cedar Falls, Iowa").second(),
                Salesperson("Car McCarface").third()
            )
        )
    )
}
```

Let's say we have an api. Our api operates under the following conditions:
- Every endpoint could tell the client, `ServerError`, `UserUnauthorized` or `OverRequestLimit`.
- For specific endpoints, we may have specific results to the context of what they do, for example, an endpoint for registering a `Car` for some service. This endpoint could return to us `CarAlreadyRegistered`, `StolenCar` or `SuccessfullyRegistered`. For fun, let's say the `SuccessfullyRegistered` response also contains a `Registration` object with some data.

Because we have some common errors that every endpoint can return to us, we can define a sealed class of these and call them `CommonServerError` because they make sense to be sealed together for reusability. Likewise, we can logically group our specific errors into `RegistrationError` and we have `Registration` as our success type.

The most obvious approach would be to use Kotlin's `sealed class` to create a return type for this api call:

```kotlin:ank
sealed class ApiResult {
  data class CommonServerError(val value: CommonServerError): ApiResult()
  data class RegistrationError(val value: RegistrationError): ApiResult()
  data class Registration(val value: Registration): ApiResult()
}
```

Immediately we can observe there's boilerplate to this approach. We need to make a data class that just holds a single value to combine these results to a common type. Any time we need to add to ApiResult we need to go through and add another wrapping class to conform it to this type.

Upon using it we can also observe that there's unwrapping that needs to take place to actually use the values:

```kotlin:ank
fun handleResult(apiResult: ApiResult): String {
  return when (apiResult) {
    is ApiResult.CommonServerError -> "Common: ${apiResult.value}"
    is ApiResult.RegistrationError -> "RegistrationError: ${apiResult.value}"
    is ApiResult.Registration -> "Registration: ${apiResult.value}"
  }
}
```

With Coproducts, we're able to define a result for this api call as `typealias ApiResult = Coproduct3<CommonServerError, RegistrationError, Registration>`. We've been able to compose these results without having to write our own sealed class containing all the common errors for each endpoint. This lets us flatten a layer of boilerplate by abstracting the sealed hierarchy and lets us freely compose types from different domain types.

#### Constructors

So now that we've got our api response modeled, we need to be able to create an instance of `Coproduct3`.

```kotlin:ank:playground
import arrow.generic.*
import arrow.generic.coproduct3.First


fun main() {
    println(
        //sampleStart
        First<CommonServerError, RegistrationError, Registration>(ServerError)
        //sampleEnd
    )
}
```

Coproducts are backed by a sealed class hierarchy and we can use the data classes to create Coproducts. The class names resemble the index of the generic, for example, Coproduct3<A, B, C> has First, Second and Third. First references the `A`, Second references the `B` and so forth.

If we pass in a value that doesn't correspond to any types on the Coproduct, it won't compile:

```kotlin:ank:fail
import arrow.generic.*
import arrow.generic.coproduct3.First

fun main() {
    println(
        //sampleStart
        First<String, RegistrationError, Registration>(ServerError)
        //sampleEnd
    )
}
```

#### Extensions

##### constructors

You might be saying "That's great and all but passing in values as parameters is so Java, I want something more Kotlin!". Well look no further, just like [Either]({{ '/docs/arrow/core/either' | relative_url }})'s `left()` and `right()` extension methods, Coproducts can be created with an extension method on any type:

```kotlin:ank:playground
import arrow.generic.*
import arrow.generic.coproduct3.*

fun main() {
    //sampleStart
    println(ServerError.first<CommonServerError, RegistrationError, Registration>())
    println(CarAlreadyRegistered.second<CommonServerError, RegistrationError, Registration>())
    //sampleEnd
}
```

All we have to do is provide the type parameters and we can make a Coproduct using the extension methods. Just like the data classes, if the type of the value isn't in the type parameters of the method call, or it's not in the correct type parameter index, it won't compile:

```kotlin:ank:fail
import arrow.generic.*
import arrow.generic.coproduct3.first

fun main() {
    println(
        //sampleStart
        "String".first<CommonServerError, RegistrationError, Registration>()
        //sampleEnd
    )
}
```

##### fold

Obviously, we're not just modeling errors for fun, we're going to handle them! All Coproducts have `fold` which allows us to condense the Coproduct down to a single type. For example, we could handle errors as such in a UI:

```kotlin:ank:playground
import arrow.generic.*
import arrow.generic.coproduct3.*

fun handleCommonError(commonError: CommonServerError) {
    println("Encountered a common error $commonError")
}

fun showCarAlreadyRegistered() {
    println("Car is already registered")
}

fun callPolice() {
    println("That car is stolen!!!!1!")
}

fun showCarSuccessfullyRegistered(car: Car) {
    println("Successfully Registered!")
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

fun main() {
    renderApiResult(Registration(Car(Speed(100))).third())
}
```

This example returns `Unit` because all of these are side effects, let's say our application was built for a command line and we just have to show a `String` for the result of the call (if only it was always that easy):

```kotlin:ank:playground
import arrow.generic.*
import arrow.generic.coproduct3.*

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

fun main() {
    println(renderApiResult(Registration(Car(Speed(100))).third()))
}
```

Here we're able to return the result of the `fold` and since it's exhaustively evaluated, we're forced to handle all cases! Neat! Let's say we also want to store the `Registration` object into our database when we successfully register a car. We don't really want to have to `fold` over every single case just to handle something for the `Registration`, this is where `select<T>` comes to the rescue!

##### select

We're able to take a Coproduct and `select` the type we care about from it. `select` returns an `Option`, if the value of the Coproduct was for the type you're trying to `select`, you'll get `Some`, if it was not the type used with `select`, you'll get `None`.

```kotlin:ank:playground
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.select
import arrow.generic.coproduct3.first

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

fun main() {
    println(
        ServerError.first<CommonServerError, RegistrationError, Registration>()
            .select<RegistrationError>()
    )
}
```

`select` can only be called with a type that exists on the Coproduct, if the type doesn't exist, it won't compile:

```kotlin:ank:fail
import arrow.generic.*
import arrow.generic.coproduct3.Coproduct3
import arrow.generic.coproduct3.select

fun main() {
    println(
        //sampleStart
        ServerError.first<CommonServerError, RegistrationError, Registration>()
                    .select<String>()
        //sampleEnd
    )
}
```
