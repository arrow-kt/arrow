---
layout: docs
title: Ref
permalink: /docs/effects/ref/
---

## Ref

{:.intermediate}
intermediate

`Ref` is an asynchronous, concurrent mutable reference. It provides safe concurrent access and modification of its content.
You could consider `Ref` a purely functional wrapper over an `AtomicReference` in context `F`, that is always initialised to a value `A`.

## Constructing a Ref

There are several ways to construct a `Ref`, the easiest the `of` factory method.
Since the allocation of mutable state is not referentially transparent this side-effect is contained within `F`.

```kotlin:ank:silent
import arrow.effects.*
import arrow.effects.instances.io.monadDefer.monadDefer

val ioRef: IO<Ref<ForIO, Int>> = Ref.of(1, IO.monadDefer()).fix()
```

In case you want the side-effect to execute immediately and return the `Ref` instance you can use the `unsafe` function.

```kotlin:ank:silent
val unsafe: Ref<ForIO, Int> = Ref.unsafe(1, IO.monadDefer())
```

As you can see above this fixed `Ref` to the type `Int` and initialised it with the value `1`.

In the case you want to create a `Ref` for `F` but not fix the value type yet you can use the `Ref` constructor.
This returns an interface `PartiallyAppliedRef` with a single method `of` to construct an actual `Ref`.

```kotlin:ank:silent
val ref: PartiallyAppliedRef<ForIO> = Ref(IO.monadDefer())

val ref1: IO<Ref<ForIO, String>> = ref.of("Hello, World!").fix()
val ref2: IO<Ref<ForIO, Int>> = ref.of(2).fix()
```

## Working with Ref

Most operators found on `AtomicReference` can also be found on `Ref` within the context of `F`.

```kotlin:ank
ioRef.flatMap { ref ->
  ref.get
}.unsafeRunSync()
```
```kotlin:ank
ioRef.flatMap { ref ->
  ref.updateAndGet { it + 1 }
}.unsafeRunSync()
```
```kotlin:ank
import arrow.core.toT
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.instances.io.monad.map

ioRef.flatMap { ref ->
  ref.getAndSet(5).flatMap { old ->
    ref.get.map { new -> old toT new }
  }
}.unsafeRunSync()
```
