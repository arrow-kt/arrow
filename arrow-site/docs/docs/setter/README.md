---
layout: docs-optics
title: Setter
permalink: /optics/setter/
---

## Setter


A `Setter` is an optic that can see into a structure and set or modify its focus.

It is a generalization of `map`.
A structure `S` that has a focus `A` to which we can apply a function `(A) -> B` to `S` and get `T`.
For example, `S == List<Int>` to which we apply `(Int) -> String` and we get `T == List<String>`.

- `List<Int>.map(f: (Int) -> String): List<String>`
- `PSetter#modify(s: List<Int>, f: (Int) -> String): List<String>`

You can get a `Setter` for any existing `map`.

To create your own `Setter`, you need to define how to apply `(A) -> B` to `S`.

A `Setter<Player, Int>` can set and modify the value of `Player`. So we need to define how to apply a function `(Int) -> Int` to `Player`.

## Composition

Unlike a regular `set` function, a `Setter` composes. Similar to a [`Lens`]({{'/optics/lens' | relative_url }}), we can compose `Setter`s to focus into nested structures and set or modify a value.

`Setter` can be composed with all optics but `Getter` and `Fold`. It results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Setter | Setter | Setter | Setter | Setter | X | Setter | X | Setter |

### Polymorphic setter

When dealing with polymorphic types, we can also have polymorphic setters that allow us to morph the type of the focus.
Previously, when we used a `Setter<List<Int>, Int>`, it was able to morph the `Int` values in the constructed type `List<Int>`.
With a `PSetter<List<Int>, List<String>, Int, String>`, we can morph an `Int` value to a `String` value and thus also morph the type from `List<Int>` to `List<String>`.

### Laws

Arrow provides [`SetterLaws`][setter_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own setters.

[setter_laws_source]: https://github.com/arrow-kt/arrow/blob/main/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/SetterLaws.kt
