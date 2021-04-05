---
layout: docs-optics
title: Iso
permalink: /optics/iso/
---

## Iso


An `Iso` is a lossless invertible optic that defines an isomorphism between a type `S` and `A` (i.e., a data class and its properties represented by `TupleN`).

Isos can be seen as a pair of functions that represent an isomorphism, `get`, and `reverseGet`. So, an `Iso<S, A>` represents two getters: `get: (S) -> A` and `reverseGet: (A) -> S`, where `S` is called the source of the `Iso`, and `A` is called the focus or target of the `Iso`.

A simple structure `Point2D` is equivalent to `Pair<Int, Int>`, so we can create an `Iso<Point2D, Pair<Int, Int>>`

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.optics.*

data class Point2D(val x: Int, val y: Int)

val pointIsoPair: Iso<Point2D, Pair<Int, Int>> = Iso(
    get = { point -> point.x to point.y },
    reverseGet = { (a, b) -> Point2D(a, b) }
)

val point = Point2D(6, 10)
point
```
```kotlin:ank
val pair = pointIsoPair.get(point)
pair
```
```kotlin:ank
pointIsoPair.reverseGet(pair)
```

Given an `Iso<Point2D, Pair<Int, Int>>`, we also have an `Iso<Pair<Int, Int>, Point2D>`. Since it represents an isomorphism between equivalent structures, we can reverse it.

```kotlin:ank:silent
val reversedIso: Iso<Pair<Int, Int>, Point2D> = pointIsoPair.reverse()
```

Using an `Iso`, we can modify our source `S` with a function that works on our focus `A`.

```kotlin:ank
val addFive: (Pair<Int, Int>) -> Pair<Int, Int> = { (a, b) -> (a + 5) to (b + 5) }
pointIsoPair.modify(point, addFive)
```

A function `(A) -> A` can be lifted to a function `(S) -> S`

```kotlin:ank
val liftedAddFive: (Point2D) -> Point2D = pointIsoPair.lift(addFive)
liftedAddFive(point)
```

### Composition

By composing Isos, we can create additional Isos without defining them. When dealing with different APIs or frameworks, we frequently run into multiple equivalent but different structures like `Point2D`, `Pair`, `Coord`, etc.

```kotlin:ank
data class Coord(val xAxis: Int, val yAxis: Int)

val pairIsoCoord: Iso<Pair<Int, Int>, Coord> = Iso(
        get = { pair -> Coord(pair.first, pair.second) },
        reverseGet = { coord -> coord.xAxis to coord.yAxis }
)
```

By composing `pointIsoPair` and `pairIsoCoord` (and/or reversing), we can use `Point2D`, `Pair<Int, Int>`, and `Coord` interchangeably as we can lift functions to the required structure.

Composing an `Iso` with functions can also be useful for changing the input or output type of a function. The `Iso<A?, Option<A>>` is available in `arrow-optics` as `PIso.nullableToOption()`.

```kotlin:ank
val unknownCode: (String) -> String? = { value ->
    "unknown $value"
}

val nullableOptionIso: Iso<String?, Option<String>> = PIso.nullableToOption()
(unknownCode andThen nullableOptionIso::get)("Retrieve an Option")
```

`Iso` can be composed with all optics, and composing them results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Iso | Iso | Lens | Prism | Optional | Getter | Setter | Fold | Traversal |

### Generating isos

To avoid boilerplate, Isos can be generated for a `data class` to `TupleN` with two to 10 parameters by the `@optics` annotation.
The `Iso` will be generated as a extension property on the companion object `val T.Companion.iso`.

```kotlin
@optics data class Pos(val x: Int, val y: Int) {
  companion object
}
```
```kotlin:ank:silent
val iso: Iso<Pos, Pair<Int, Int>> = Pos.iso
```

### Polymorphic Isos
When dealing with polymorphic equivalent structures, we can create polymorphic Isos allowing us to morph the type of the focus (and, as a result, the constructed type) of our `PIso`.

Given our previous structures `Pair<A, B>` and a structure `Tuple2<A, B>`, we can create a polymorphic `PIso` that represents a `get: (Pair<A, B>) -> Tuple2<A, B>` and a `reverseGet: (Tuple2<C, D) -> Pair<C, D>`.

```kotlin:ank
data class Tuple2<A, B>(val a: A, val b: B) {
  fun reversed(): Tuple2<B, A> =
    Tuple2(b, a)
}

fun <A, B, C, D> pair(): PIso<Pair<A, B>, Pair<C, D>, Tuple2<A, B>, Tuple2<C, D>> = PIso(
  { (a, b) -> Tuple2(a, b) },
  { (a, b) -> a to b }
)
```

`PIso` (defined above) can lift a `reverse` function of `(Tuple2<A, B>) -> Tuple2<B, A>` to a function `(Pair<A, B>) -> Pair<B, A>`,
this allows us to use functions defined for `Tuple2` for a value of type `Pair`.

```kotlin:ank
val reverseTupleAsPair: (Pair<Int, String>) -> Pair<String, Int> =
  pair<Int, String, String, Int>().lift(Tuple2<Int, String>::reversed)

val reverse: Pair<String, Int> = reverseTupleAsPair(5 to "five")
reverse
//(five, 5)
```

### Laws

Arrow provides [`IsoLaws`][iso_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own isos.

[iso_laws_source]: https://github.com/arrow-kt/arrow/blob/main/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/IsoLaws.kt
