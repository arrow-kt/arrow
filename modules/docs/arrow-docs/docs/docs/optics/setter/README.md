---
layout: docs
title: Setter
permalink: /docs/optics/setter/
---

## Setter

A `Setter` is an optic that can see into a structure and set or modify its focus.

It is a generalisation of [`Functor#map`](/docs/typeclasses/functor). Given a `Functor<F>` we can apply a function `(A) -> B` to `Kind<F, A>` and get `Kind<F, B>`. We can think of `Kind<F, A>` as a structure `S` that has a focus `A`.
So given a `PSetter<S, T, A, B>` we can apply a function `(A) -> B` to `S` and get `T`.

- `Functor.map(fa: Kind<F, A>, f: (A) -> B) -> Kind<F, B>`
- `PSetter.modify(s: S, f: (A) -> B): T`

You can get a `Setter` for any existing `Functor`.

```kotlin:ank
import arrow.*
import arrow.optics.*
import arrow.data.*

val setter: Setter<ListKWOf<Int>, Int> = Setter.fromFunctor()
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

Unlike a regular `set` function a `Setter` composes. Similar to a [`Lens`](/docs/optics/lens) we can compose `Setter`s to focus into nested structures and set or modify a value.

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
Previously when we used a `Setter<ListKWOf<Int>, Int>` it was able to morph the `Int` values in the constructed type `ListKW<Int>`.
With a `PSetter<ListKWOf<Int>, ListKWOf<String>, Int, String>` we can morph an `Int` value to a `String` value and thus also morph the type from `ListKW<Int>` to `ListKW<String>`.

```kotlin:ank
val pSetter: PSetter<ListKWOf<Int>, ListKWOf<String>, Int, String> = PSetter.fromFunctor()
pSetter.set(listOf(1, 2, 3, 4).k(), "Constant")
```
```kotlin:ank
pSetter.modify(listOf(1, 2, 3, 4).k()) {
    "Value at $it"
}
```

### Laws

Arrow provides [`SetterLaws`][setter_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own setters.

[setter_laws_source]: https://github.com/arrow-kt/arrow/blob/master/arrow-test/src/main/kotlin/arrow/laws/SetterLaws.kt