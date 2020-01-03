---
layout: docs-optics
title: Traversal
permalink: /docs/optics/traversal/
---

## Traversal


A `Traversal` is an optic that can see into a structure and get, set, or modify 0 to N foci.

It is a generalization of [`Traverse#traverse`](/docs/arrow/typeclasses/traverse). Given a `Traverse<F>`, we can apply a function `(A) -> Kind<G, B>` to `Kind<F, A>` and get `Kind<G, Kind<F, B>>`.
We can think of `Kind<F, A>` as a structure `S` that has a focus `A`. So, given a `PTraversal<S, T, A, B>`, we can apply a function `(A) -> Kind<F, B>` to `S` and get `Kind<F, T>`.

 - `Traverse.traverse(fa: Kind<F, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Kind<F, B>>`
 - `PTraversal.modifyF(s: S, f: (A) -> Kind<F, B>, GA: Applicative<F>): Kind<F, T>`

You can get a `Traversal` for any existing `Traverse`.

```kotlin:ank
import arrow.*
import arrow.optics.*
import arrow.core.*
import arrow.mtl.*
import arrow.core.extensions.listk.traverse.*
import arrow.core.extensions.`try`.applicative.*

val listTraversal: Traversal<ListKOf<Int>, Int> = Traversal.fromTraversable(ListK.traverse())

listTraversal.modifyF(Try.applicative(), listOf(1, 2, 3).k()) {
    Try { it / 2 }
}
```
```kotlin:ank
listTraversal.modifyF(Try.applicative(), listOf(0, 2, 3).k()) {
    Try { throw TryException.UnsupportedOperationException("Any arbitrary exception") }
}
```

Or by using any of the constructors of `Traversal`.

```kotlin:ank
fun <A> traversalTuple2Example(): Traversal<Tuple2<A, A>, A> = Traversal(
        get1 = { it.a },
        get2 = { it.b },
        set = { a, b, _ -> Tuple2(a, b) }
)
```

Arrow optics also provides a number of predefined `Traversal` optics.

```kotlin:ank
import arrow.core.extensions.*
import arrow.optics.extensions.*

Tuple2.traversal<String>().combineAll(String.monoid(), "Hello, " toT "World!")
```
```kotlin:ank
Tuple10.traversal<Int>().getAll(Tuple10(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
```

There are also some convenience methods to make working with [State]({{ '/docs/apidocs/arrow-mtl-data/arrow.mtl/-state.html' | relative_url }}) easier.
This can make working with nested structures in stateful computations significantly more elegant.

```kotlin:ank
import arrow.optics.mtl.*

data class Enemy(val health: Int)
val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()

val dropBomb = ListK.traversal<Enemy>().update { it.copy(health = it.health - 50) }

dropBomb.run(battlefield)
```

```kotlin:ank
val finishingMove = ListK.traversal<Enemy>().assign(Enemy(0))

finishingMove.run(battlefield)
```

## Composition

Composing `Traversal` can be used for accessing and modifying foci in nested structures.

```kotlin:ank
val listOfPairTraversal: Traversal<ListKOf<Tuple2<String, String>>, Tuple2<String, String>> = Traversal.fromTraversable(ListK.traverse())
val nestedStrings = listOfPairTraversal compose Tuple2.traversal<String>()

nestedStrings.fold(String.monoid(), listOf("Hello, " toT "World ", "from " toT "nested structures!").k())
```

`Traversal` can be composed with all optics, and results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Traversal | Traversal | Traversal | Traversal | Traversal | Fold | Setter | Fold | Traversal |

### Polymorphic Traversal

When dealing with polymorphic types, we can also have polymorphic `Traversal`s that allow us to morph the type of the foci.
Previously, we used a `Traversal<ListKOf<Int>, Int>`; it was able to morph the `Int` values in the constructed type `ListK<Int>`.
With a `PTraversal<ListKOf<Int>, ListKOf<String>, Int, String>`, we can morph an `Int` to a `String`, and thus, also morph the type from `ListK<Int>` to `ListK<String>`.

```kotlin:ank
val pTraversal: PTraversal<ListKOf<Int>, ListKOf<String>, Int, String> = PTraversal.fromTraversable(ListK.traverse())

pTraversal.set(listOf(1, 2, 3, 4).k(), "Constant")
```
```kotlin:ank
pTraversal.modify(listOf(1, 2, 3, 4).k()) {
    "At position $it"
}
```

### Laws

Arrow provides [`TraversalLaws`][traversal_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own traversal.

[traversal_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/TraversalLaws.kt
