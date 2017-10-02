---
layout: docs
title: Prism
permalink: /docs/optics/prism/
---

## Prism

A `Prism` is a loss less invertible optic that can see into a structure and optionally find its focus. They're mostly used for structures that have a relationship only under a certain condition. I.e. a certain `sum` of a `sum type` (`sealed class`), the head of a list or all whole double values and integers (safe casting).

Since `Prism` has an optional focus it can be seen as a pair of functions `getOrModify` and `reverseGet`.

* `getOrModify: A -> Either<A, B>` meaning we can get the target of a `Prism` OR return the original value
* `reverseGet : B -> A` meaning we can construct the source type of a `Prism` from a `B`

Given a `Prism<S, A>` we can write functions that work on the focus `A` without having to worry if the focus can be seen in `S`.

For a sum type `NetworkResult` we can create a `Prism` that has a focus into `Success`

```kotlin:ank
import kategory.*
import kategory.optics.*

sealed class NetworkResult {
    data class Success(val content: String): NetworkResult()
    object Failure: NetworkResult()
}

val networkSuccessPrism: Prism<NetworkResult, NetworkResult.Success> = Prism(
        getOrModify = { networkResult ->
            when(networkResult) {
                is NetworkResult.Success -> networkResult.right()
                else -> networkResult.left()
            }
        },
        reverseGet = { networkResult -> networkResult } //::identity
)
```

As is clear from above `Prism` definition it gathers two concepts: pattern matching and constructor.

Since sealed classes enforce a certain relationship we can omit the `reverseGet` parameter to create a `Prism` for them. 

```kotlin:ank:silent
val networkSuccessPrism2: Prism<NetworkResult, NetworkResult.Success> = Prism { networkResult ->
    when (networkResult) {
        is NetworkResult.Success -> networkResult.right()
        else -> networkResult.left()
    }
}
```

Like mentioned we can now operate on `NetworkResult` as if it were `Success`

```kotlin:ank
val networkResult = NetworkResult.Success("content")

networkSuccessPrism.modify(networkResult) { success ->
    success.copy(content = "different content")
}
```

We can also lift such functions.

```kotlin:ank
val lifted: (NetworkResult) -> NetworkResult = networkSuccessPrism.lift { success ->
        success.copy(content = "different content")
}
lifted(NetworkResult.Failure)
```

We can also modify or lift functions using `Functors`

```kotlin:ank
networkSuccessPrism.modifyF(Option.applicative(), networkResult) { success ->
    success.some()
}
```
```kotlin:ank
val liftF = networkSuccessPrism.liftF(Option.applicative()) { none() }
liftF(networkResult)
```

`Prisms` can easily be created by using any of the already mentioned constructors although for a `sealed class` a `Prism` could easily be [generated](#generated-prisms). But we can also use a `PartialFuntion` to create a `Prism`.

```kotlin:ank
val doubleToInt: Prism<Double, Int> = Prism(
        partialFunction = case(
                { double: Double -> double.toInt().toDouble() == double }
                      toT Double::toInt
        ),
        reverseGet = Int::toDouble
)
```

## Composition

Nesting pattern matching blocks are tedious. We would prefer to define them seperately and compose them together. We can do that by composing mulitple `Prisms`.

Let's imagine from our previous example we want to retrieve an `Int` from the network. We get a `Success` OR a `Failure` from the network. In case of a `Success` we want to safely cast the `String` to an `Int`.

```kotlin:ank
val successToInt: Prism<NetworkResult.Success, Int> = Prism(
        partialFunction = case({ success: NetworkResult.Success -> Try { success.content.toInt() }.nonEmpty() }
                toT { success -> success.content.toInt() }
        ),
        reverseGet = NetworkResult::Success compose Int::toString
)

val networkInt: Prism<NetworkResult, Int> = networkSuccessPrism compose successToInt
```
```kotlin:ank
networkInt.getOption(NetworkResult.Success("invalid int"))
```
```kotlin:ank
networkInt.getOption(NetworkResult.Failure)
```
```kotlin:ank
networkInt.getOption(NetworkResult.Success("5"))
```

## Generated prisms <a id="generated-prisms"></a>

Prisms can be generated for `sealed classes` by the `@prisms` annotation. For every defined subtype a `Prism` will be generated. The prisms will be generated in the same package as the `sealed class` and will be named `parentnameSubtypename()`.

```kotlin
@prisms sealed class NetworkResult {
    data class Success(val content: String) : NetworkResult()
    object Failure : NetworkResult()
}

val networkSuccessPrism: Prism<NetworkResult, NetworkResult.Success> = networkResultSuccess()
val networkFailurePrism: Prism<NetworkResult, NetworkResult.Failure> = networkResultFailure()
```

### Laws

Kategory provides [`PrismLaws`](/docs/optics/laws#prismlaws) in the form of test cases for internal verification of lawful instances and third party apps creating their own prisms.
