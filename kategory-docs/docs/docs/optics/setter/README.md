---
layout: docs
title: Setter
permalink: /docs/optics/setter/
---

## Setter

A `Setter` is an optic that can see into a structure and set or modify its focus.

It is a generalisation of `Functor#map`. Given a `Functor<F>` we can apply a function `(A) -> B` to `HK<F, A>` and get `HK<F, B>`. We can think of `HK<F, A>` as a structure `S` that has a focus `A`.
So given a `PSetter<S, T, A, B>` we can apply a function `(A) -> B` to `S` and get `T`.

- `Functor.map(fa: HK<F, A>, f: (A) -> B) -> HK<F, B>`
- `PSetter.modify(s: S, f: (A) -> B): T`

You can get a `Setter` for any existing `Functor`.

```kotlin:ank
import kategory.*
import kategory.optics.*

val setter: Setter<ListKWKind<Int>, Int> = Setter.fromFunctor()
setter.set(listOf(1, 2, 3, 4).k(), 5)
```
```kotlin:ank
setter.modify(listOf(1, 2, 3, 4).k()) { int -> int + 1 }
```

To create your own `Setter` you need to define how to apply `(A) -> B` to `S`.

A `Setter<Foo, String>` can set and modify the value of `Foo`. So we need to define how to apply a function `(String) -> String` to `Foo`.

```kotlin:ank
data class Foo(val value: String)

val fooSetter: Setter<Foo, String> = Setter { f: (String) -> String ->
    { foo: Foo ->
        val fValue = f(foo.value)
        foo.copy(value = fValue)
    }
}
```
```kotlin:ank
val uppercase: (String) -> String = String::toUpperCase
fooSetter.modify(Foo("foo"), uppercase)
```
```kotlin:ank
val lift = fooSetter.lift(uppercase)
lift(Foo("foo"))
```

## Composition

Unlike a regular `set` function a `Setter` composes. Similar to a `Lens` we can compose `Setter`s to focus into nested structures and set or modify a value.

```kotlin:ank
data class Bar(val foo: Foo)

val barSetter: Setter<Bar, Foo> = Setter { modifyFoo ->
    { bar ->
        val modifiedFoo = modifyFoo(bar.foo)
        bar.copy(foo = modifiedFoo)
    }
}

(barSetter compose fooSetter).modify(Bar(Foo("some value")), String::toUpperCase)
```

`Setter` can be composed with all optics but `Getter` and `Fold`. It results in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Setter | Setter | Setter | Setter | Setter | X | Setter | X | Setter |

### Polymorphic setter

When dealing with polymorphic types we can also have polymorphic setters that allow us to morph the type of the focus.
Previously we used a `Setter<ListKWKind<Int>, Int>` it was able to morph the `Int` values in the constructed type `ListKW<Int>`.
With a `PSetter<ListKWKind<Int>, ListKWKind<String>, Int, String>` we can morph an `Int` value to a `String` value and thus also morph the type from `ListKW<Int>` to `ListKW<String>`.

```kotlin:ank
val pSetter: PSetter<ListKWKind<Int>, ListKWKind<String>, Int, String> = PSetter.fromFunctor()
pSetter.set(listOf(1, 2, 3, 4).k(), "Constant")
```
```kotlin:ank
pSetter.modify(listOf(1, 2, 3, 4).k()) {
    "Value at $it"
}
```

### Laws

Kategory provides [`SetterLaws`][setter_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own setters.

[setter_laws_source]: https://github.com/kategory/kategory/blob/master/kategory-test/src/main/kotlin/kategory/laws/SetterLaws.kt