---
layout: docs
title: Monad
permalink: /docs/typeclasses/monad/
---

## Monad

Monad extends the `Applicative` type class with a new function flatten. Flatten takes a value in a nested context 
(eg. `F[F[A]`] where F is the context) and "joins" the contexts together so that we have a single context 
(ie. `F[A]`).

Here same examples on `Option` and `List`

```kotlin:ank
import kategory.*

Option.monad()
Option.Some(Option.Some(1)).flatten()
```

```kotlin:ank
Option.Some(Option.None).flatten()
```

```kotlin:ank
listOf(listOf(1), listOf(2, 3)).flatten()
```

## FLATMAP

`flatMap` is often considered to be the core function of `Monad`, and `kategory` follows this tradition 
by providing implementations of `flatten` and `map` derived from `flatMap` and `pure`.

```kotlin
fun <B> flatMap(f: (A) -> ListKWKind<B>): ListKW<B> = this.ev().list.flatMap { f(it).ev().list }.k()

fun <A> pure(a: A): ListKW<A> = listOf(a).k()
```

Part of the reason for this is that name `flatMap` has special significance in `kategory`, as for-comprehensions 
rely on this method to chain together operations in a monadic context.

```kotlin:ank
ListKW.monad().flatMap(ListKW(listOf(1, 2, 3))) { x ->
    ListKW(listOf(x, x))
}
```

## IFM

`Monad` provides the ability to choose later operations in a sequence based on the results of earlier ones. 
This is embodied in `ifM`, which lifts an `if` statement into the monadic context.

```kotlin:ank
Option.monad().ifM(Option(true), {Option("truthy")}, {Option("falsy")})
```

```kotlin:ank
ListKW.monad().ifM(ListKW(listOf(true, false, true)), {ListKW(listOf(1, 2))}, {ListKW(listOf(3, 4))})
```

## COMPOSITION

Unlike `Functor`s and `Applicative`s, you cannot derive a monad instance for a generic `M[N[_]]` where 
both `M[_]` and `N[_]` have an instance of a monad.

However, it is common to want to compose the effects of both `M[_]` and `N[_]`. One way of 
expressing this is to provide instructions on how to compose any outer monad (`F` in the following 
example) with a specific inner monad (`Option` in the following example).

```kotlin
@higherkind data class OptionT<F, A>(val MF: Monad<F>, val value: HK<F, Option<A>>) : OptionTKind<F, A>
```

This sort of construction is called a monad transformer. Cats already provides a monad transformer 
for Option called `OptionT`.

```kotlin:ank
 OptionT.monad(Try.monad()).pure(42)
```
