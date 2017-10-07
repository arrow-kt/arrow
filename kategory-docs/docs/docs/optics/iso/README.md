---
layout: docs
title: Iso
permalink: /docs/optics/iso/
---

## Iso

An `Iso` is a loss less invertible optic that defines an isomorphism between a type `S` and `A` i.e. a data class and its properties represented by `TupleN`.

Isos can be seen as a pair of functions that represent an isomorphism, `get` and `reverseGet`. So an `Iso<S, A>` represents two getters: `get: (S) -> A` and `reverseGet: (A) -> S` where `S` is called the source of the `Iso` and `A` is called the focus or target of the `Iso`.

A simple structure `Point2D` is equivalent to `Tuple2<Int, Int>` so we can create an `Iso<Point2D, Tuple2<Int, Int>>`

```kotlin:ank
import kategory.*
import kategory.optics.*

data class Point2D(val x: Int, val y: Int)

val pointIsoTuple: Iso<Point2D, Tuple2<Int, Int>> = Iso(
    get = { point -> point.x toT point.y },
    reverseGet = { tuple -> Point2D(tuple.a, tuple.b) }
)

val point = Point2D(6, 10)
point
```
```kotlin:ank
val tuple = pointIsoTuple.get(point)
tuple
```
```kotlin:ank
pointIsoTuple.reverseGet(tuple)
```

Given an `Iso<Point2D, Tuple2<Int, Int>>` we also have an `Iso<Tuple2<Int, Int>, Point2D>` since it represents an isomorphism between equivalent structures we can reverse it.

```kotlin:ank:silent
val reversedIso: Iso<Tuple2<Int, Int>, Point2D> = pointIsoTuple.reverse()
```

Using an `Iso` we can modify our source `S` with a function that works on our focus `A`.

```kotlin:ank
val addFive: (Tuple2<Int, Int>) -> Tuple2<Int, Int> = { tuple2 -> (tuple2.a + 5) toT (tuple2.b + 5) }
pointIsoTuple.modify(point, addFive)
```

A function `(A) -> A` can be lifted to a function `(S) -> S`

```kotlin:ank
val liftedAddFive: (Point2D) -> Point2D = pointIsoTuple.lift(addFive)
liftedAddFive(point)
```

We can do the same with a Functor mapping

```kotlin:ank
pointIsoTuple.modifyF(Try.functor(), point) {
    Try { (tuple.a / 2) toT (tuple.b / 2) }
}
```

```kotlin:ank
val liftF: (Point2D) -> TryKind<Point2D> = pointIsoTuple.liftF(Try.functor()) {
    Try { (tuple.a / 2) toT (tuple.b / 0) }
}

liftF(point)
```

### Composition

By composing isos we can create additional isos without defining them. When dealing with different APIs or frameworks we frequently run into multiple equivalent but different structures like `Point2D`, `Tuple2`, `Pair`, `Coord`, etc.

```kotlin:ank
data class Coord(val xAxis: Int, val yAxis: Int)

val pairIsoCoord: Iso<Pair<Int, Int>, Coord> = Iso(
        get = { pair -> Coord(pair.first, pair.second) },
        reverseGet = { coord -> coord.xAxis to coord.yAxis }
)

val tupleIsoPair: Iso<Tuple2<Int, Int>, Pair<Int, Int>> = Iso(
        get = { tuple -> tuple.a to tuple.b },
        reverseGet = { pair -> pair.first toT pair.second }
)
```

By composing `pointIsoTuple`, `pairIsoCoord` and `tupleIsoPair` (and/or reversing) we can use `Point2D`, `Tuple2<Int, Int>`, `Pair<Int, Int>` and `Coord` interchangeably as we can lift functions to the required structure.

Composing an `Iso` with functions can also be useful to change input or output type of a function. In the `Iso<A?, Option<A>>` is available in `kategory-optics` as `nullableToOption()`.

```kotlin
val unknownCode: (String) -> String? = { value ->
    "unkown $value"
}

val nullableOptionIso: Iso<String?, Option<String>> = nullableToOption()
(unknownCode andThen nullableOptionIso::get)("Retrieve an Option")
```

`Iso` can be composed with all optics and composition them results in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Iso | Iso | Lens | Prism | Optional | Getter | Setter | Fold | Traversal |

### Generating isos

To avoid boilerplate, isos can be generated for a `data class` to `TupleN` with 2 to 10 parameters by the `@isos` annotation. The `Iso` will be generated in the same package as the `data class` and will be named `classnameIso()`.

```kotlin
@isos data class Point2D(val x: Int, val y: Int)

val iso: Iso<Point2D, Tuple2<Int, Int>> = point2dIso()
```

### Polymorphic isos
When dealing with polymorphic equivalent structures we can create polymorphic isos to allow us to morph the type of the focus (and as a result the constructed type) of our `PIso`.

Given our previous structures `Tuple2<A, B>` and `Pair<A, B>` we can create a polymorphic `PIso` that represents a `get: (Tuple2<A, B>) -> Pair<A, B>` and a `reverseGet: (Tuple2<C, D) -> Pair<C, D>`.

```kotlin:ank
fun <A, B, C, D> tuple2(): PIso<Tuple2<A, B>, Pair<C, D>, Pair<A, B>, Tuple2<C, D>> = PIso(
        { tuple -> tuple.a to tuple.b },
        { tuple -> tuple.a to tuple.b }
)
```

Above defined `PIso` can lift a `reverse` function of `(Pair<A, B>) -> Tuple2<B, A>` to a function `(Tuple2<A, B>) -> Pair<B, A>`.

```kotlin:ank
val reverseTupleAsPair: (Tuple2<Int, String>) -> Pair<String, Int> =
        tuple2<Int, String, String, Int>().lift { intStringPair -> intStringPair.second toT intStringPair.first }
val reverse: Pair<String, Int> = reverseTupleAsPair(5 toT "five")
reverse
```

### Laws

Kategory provides [`IsoLaws`](/docs/optics/laws#isolaws) in the form of test cases for internal verification of lawful instances and third party apps creating their own isos.