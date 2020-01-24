---
layout: docs-fx
title: Ref
permalink: /docs/effects/ref/
---

## Ref




`Ref` is an asynchronous, concurrent mutable reference. It provides safe concurrent access and modification of its content.
You could consider `Ref` a purely functional wrapper over an `AtomicReference` in context `F` that is always initialized to a value `A`.

## Constructing a Ref

There are several ways to construct a `Ref`, the easiest being the `of` factory method.
Since the allocation of mutable state is not referentially transparent, this side-effect is contained within `F`.

```kotlin:ank:silent
import arrow.fx.*
import arrow.fx.extensions.io.monadDefer.monadDefer

val ioRef: IO<Nothing, Ref<ForIO, Int>> = Ref(IO.monadDefer(), 1).fix()
```

In case you want the side-effect to execute immediately and return the `Ref` instance, you can use the `unsafe` function.

```kotlin:ank:silent
val unsafe: Ref<ForIO, Int> = Ref.unsafe(1, IO.monadDefer())
```

As you can see above, this fixed `Ref` to the type `Int` and initialized it with the value `1`.

If you want to create a `Ref` for `F`, but not fix the value type yet, you can use the `Ref` constructor.
This returns an interface `RefFactory` with a single method `later` to construct an actual `Ref`.

```kotlin:ank:silent
val ref: RefFactory<ForIO> = Ref.factory(IO.monadDefer())

val ref1: IO<Nothing, Ref<ForIO, String>> = ref.just("Hello, World!").fix()
val ref2: IO<Nothing, Ref<ForIO, Int>> = ref.just(2).fix()
```

## Working with Ref

Most operators found on `AtomicReference` can also be found on `Ref` within the context of `F`.

```kotlin:ank
ioRef.flatMap { ref ->
  ref.get()
}.unsafeRunSync()
```
```kotlin:ank
ioRef.flatMap { ref ->
  ref.updateAndGet { it + 1 }
}.unsafeRunSync()
```
```kotlin:ank
import arrow.core.toT
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monad.map

ioRef.flatMap { ref ->
  ref.getAndSet(5).flatMap { old ->
    ref.get().map { new -> old toT new }
  }
}.unsafeRunSync()
```
