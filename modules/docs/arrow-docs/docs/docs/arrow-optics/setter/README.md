---
layout: docs-optics
title: Setter
permalink: /docs/optics/setter/
---

## Setter


A `Setter` is an optic that can see into a structure and set or modify its focus.

It is a generalization of [`Functor#map`](/docs/arrow/typeclasses/functor). Given a `Functor<F>`, we can apply a function `(A) -> B` to `Kind<F, A>` and get `Kind<F, B>`. We can think of `Kind<F, A>` as a structure `S` that has a focus `A`.
So, given a `PSetter<S, T, A, B>`, we can apply a function `(A) -> B` to `S` and get `T`.

- `Functor.map(fa: Kind<F, A>, f: (A) -> B) -> Kind<F, B>`
- `PSetter.modify(s: S, f: (A) -> B): T`

You can get a `Setter` for any existing `Functor`.

```kotlin:ank
import arrow.*
import arrow.optics.*
import arrow.core.*
import arrow.mtl.*
import arrow.core.extensions.listk.functor.*

val setter: Setter<ListKOf<Int>, Int> = Setter.fromFunctor(ListK.functor())
setter.set(listOf(1, 2, 3, 4).k(), 5)
```
```kotlin:ank
setter.modify(listOf(1, 2, 3, 4).k()) { int -> int + 1 }
```

To create your own `Setter`, you need to define how to apply `(A) -> B` to `S`.

A `Setter<Player, Int>` can set and modify the value of `Player`. So we need to define how to apply a function `(Int) -> Int` to `Player`.

```kotlin:ank
data class Player(val health: Int)

val playerSetter: Setter<Player, Int> = Setter { player: Player, f: (Int) -> Int ->
  val fHealth= f(player.health)
  player.copy(health = fHealth)
}
```
```kotlin:ank
val increment: (Int) -> Int = Int::inc
playerSetter.modify(Player(75), increment)
```
```kotlin:ank
val lift = playerSetter.lift(increment)
lift(Player(75))
```

There are also some convenience methods to make working with [State]({{ '/docs/arrow/data/state' | relative_url }}) easier.
This can make working with nested structures in stateful computations significantly more elegant.

```kotlin:ank
import arrow.optics.mtl.*

val takeDamage = playerSetter.update_ { it - 15 }
takeDamage.run(Player(75))
```

```kotlin:ank
val restoreHealth = playerSetter.assign_(100)
restoreHealth.run(Player(75))
```

## Composition

Unlike a regular `set` function, a `Setter` composes. Similar to a [`Lens`](/docs/optics/lens), we can compose `Setter`s to focus into nested structures and set or modify a value.

```kotlin:ank
data class Bar(val player: Player)

val barSetter: Setter<Bar, Player> = Setter { bar, modifyPlayer ->
  val modifiedPlayer = modifyPlayer(bar.player)
  bar.copy(player = modifiedPlayer)
}

(barSetter compose playerSetter).modify(Bar(Player(75)), Int::inc)
```

`Setter` can be composed with all optics but `Getter` and `Fold`. It results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Setter | Setter | Setter | Setter | Setter | X | Setter | X | Setter |

### Polymorphic setter

When dealing with polymorphic types, we can also have polymorphic setters that allow us to morph the type of the focus.
Previously, when we used a `Setter<ListKOf<Int>, Int>`, it was able to morph the `Int` values in the constructed type `ListK<Int>`.
With a `PSetter<ListKOf<Int>, ListKOf<String>, Int, String>`, we can morph an `Int` value to a `String` value and thus also morph the type from `ListK<Int>` to `ListK<String>`.

```kotlin:ank
val pSetter: PSetter<ListKOf<Int>, ListKOf<String>, Int, String> = PSetter.fromFunctor(ListK.functor())
pSetter.set(listOf(1, 2, 3, 4).k(), "Constant")
```
```kotlin:ank
pSetter.modify(listOf(1, 2, 3, 4).k()) {
    "Value at $it"
}
```

### Laws

Arrow provides [`SetterLaws`][setter_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own setters.

[setter_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/SetterLaws.kt
