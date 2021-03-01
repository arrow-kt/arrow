---
layout: docs-optics
title: Traversal
permalink: /optics/traversal/
---

## Traversal


A `Traversal` is an optic that can see into a structure and get, set, or modify 0 to N foci.

It is a generalization of `map`.
A structure `S` that has a focus `A` to which we can apply a function `(A) -> B` to `S` and get `T`.
For example, `S == List<Int>` to which we apply `(Int) -> String` and we get `T == List<String>`

A `Traversal` can simply be created by providing the `map` function.

```kotlin:ank:playground
import arrow.optics.*

fun main(): Unit {
  //startSample
  val traversal: PTraversal<List<Int>, List<String>, Int, String> =
    PTraversal { s, f -> s.map(f) }
  
  val source = listOf(1, 2, 3, 4)
  val target = traversal.modify(source, Int::toString)
  //endSample
  println(target)
} 
```

Or by using any of the constructors of `Traversal`.

Arrow optics also provides a number of predefined `Traversal` optics.

## Composition

Composing `Traversal` can be used for accessing and modifying foci in nested structures.

`Traversal` can be composed with all optics, and results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Traversal | Traversal | Traversal | Traversal | Traversal | Fold | Setter | Fold | Traversal |

### Polymorphic Traversal

When dealing with polymorphic types, we can also have polymorphic `Traversal`s that allow us to morph the type of the foci.
Previously, we used a `Traversal<List<Int>, Int>`; it was able to morph the `Int` values in the constructed type `List<Int>`.
With a `PTraversal<List<Int>, List<String>, Int, String>`, we can morph an `Int` to a `String`, and thus, also morph the type from `List<Int>` to `List<String>`.

### Laws

Arrow provides [`TraversalLaws`][traversal_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own traversal.

[traversal_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/TraversalLaws.kt
