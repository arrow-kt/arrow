---
layout: docs-optics
title: Prism
permalink: /optics/prism/
---

## Prism


A `Prism` is a lossless invertible optic that can see into a structure and optionally find its focus, and construct a value given that piece of data. They're mostly used for structures that have a relationship only under a certain condition, i.e., a certain `sum` of a `sum type` (`sealed class`), the head of a list, or all whole double values and integers (safe casting).

Since `Prism` has an optional focus, it can be seen as a pair of functions: `getOrModify` and `reverseGet`.

* `getOrModify: A -> Either<A, B>`, meaning we can get the focus of a `Prism` OR return the original value.
* `reverseGet : B -> A`, meaning we can construct the source type of a `Prism` from a `B`.

Given a `Prism<S, A>`, we can write functions that work on the focus `A` without having to worry if the focus can be seen in `S`.

For a sum type `NetworkResult`, we can create a `Prism` that has a focus into `Success`.

```kotlin
import arrow.core.*
import arrow.optics.*

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

As is clear from above `Prism` definition, it gathers two concepts: pattern matching and constructor.

As mentioned, we can now operate on `NetworkResult` as if it were `Success`.

```kotlin
val networkResult = NetworkResult.Success("content")

networkSuccessPrism.modify(networkResult) { success ->
    success.copy(content = "different content")
}
```

We can also lift such functions.

```kotlin
val lifted: (NetworkResult) -> NetworkResult = networkSuccessPrism.lift { success ->
        success.copy(content = "different content")
}
lifted(NetworkResult.Failure)
```

`Prisms` can easily be created by using any of the already mentioned constructors, although, for a `sealed class`, a `Prism` could easily be [generated](#generated-prisms). But we can also use a `PartialFunction` to create a `Prism`.

```kotlin
val doubleToInt: Prism<Double, Int> = Prism(
  getOption = { double: Double ->
    val i = double.toInt()
    if (i.toDouble() == double) Some(i) else None
  },
  reverseGet = Int::toDouble
)
```

## Composition

Nesting pattern matching blocks are tedious. We would prefer to define them separately and compose them together. We can do that by composing multiple `Prisms`.

Let's imagine from our previous example that we want to retrieve an `Int` from the network. We get a `Success` OR a `Failure` from the network. In case of a `Success`, we want to safely cast the `String` to an `Int`.

```kotlin
import arrow.core.*

val successToInt: Prism<NetworkResult.Success, Int> = Prism(
  getOption = { success -> success.content.toIntOrNull().toOption() },
  reverseGet = NetworkResult::Success compose Int::toString
)

val networkInt: Prism<NetworkResult, Int> = networkSuccessPrism compose successToInt
```
```kotlin
networkInt.getOrNull(NetworkResult.Success("invalid int"))
```
```kotlin
networkInt.getOrNull(NetworkResult.Failure)
```
```kotlin
networkInt.getOrNull(NetworkResult.Success("5"))
```
`Prism` can be composed with all optics but `Getter`, and result in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Prism | Prism | Optional | Prism | Optional | X | Setter | Fold | Traversal |

## Generated prisms <a id="generated-prisms"></a>

Prisms can be generated for `sealed classes` by the `@optics` annotation. For every defined subtype, a `Prism` will be generated.
The prisms will be generated as extension properties on the companion object `val T.Companion.subTypeName`.

```kotlin
@optics sealed class Shape {
  companion object { }
  data class Circle(val radius: Double) : Shape()
  data class Rectangle(val width: Double, val height: Double) : Shape()
}
```
```kotlin
val circleShape: Prism<Shape, Shape.Circle> = Shape.circle
val rectangleShape: Prism<Shape, Shape.Rectangle> = Shape.rectangle
```

### Polymorphic prisms <a id="PPrism"></a>
When dealing with polymorphic sum types like `Option<A>`, we can also have polymorphic prisms that allow us to polymorphically change the type of the focus of our `PPrism`. The following method is also available as `PSome<A, B>()` in the `arrow.optics` package:

```kotlin
fun <A, B> optionSome(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
    getOrModify = { option -> option.fold({ Either.Left(None) }, { Either.Right(it) }) },
    reverseGet = { b -> Some(b) }
)

val liftSome: (Option<Int>) -> Option<String> = PSome<Int, String>().lift(Int::toString)
liftSome(Some(5))
```
```kotlin
liftSuccess(None)
```

### Laws

Arrow provides [`PrismLaws`][prism_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own prisms.

[prism_laws_source]: https://github.com/arrow-kt/arrow/blob/main/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/PrismLaws.kt
