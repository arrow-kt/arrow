---
layout: docs
title: MonadError
permalink: /docs/typeclasses/monaderror/
---

## MonadError

WIP

### Main Combinators

`MonadError` inherits all the combinators available in [`ApplicativeError`]({{ '/docs/typeclasses/applicativeerror' | relative_url }}) and [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}).

#### raiseError

Inherited from [`ApplicativeError`]({{ '/docs/typeclasses/applicativeerror' | relative_url }}). A constructor function.
It lifts an exception into the computational context of a type constructor.

```kotlin:ank
Either.monadError().raiseError(RuntimeException("Paco"))
```

```kotlin:ank
IO.monadError().raiseError(RuntimeException("Paco"))
```

#### ensure

Tests a predicate against the object, and if it fails it executes a function to create an error.

```kotlin:ank
val ME = Either.monadError()

val either: Either<String, Int> = Either.Right(1)

ME.ensure(either, { RuntimeException("Failed predicate") }, { it > 0 })
```

```kotlin:ank
ME.ensure(either, { RuntimeException("Failed predicate") }, { it < 0 })
```

### Comprehensions

#### bindindCatch

It starts a [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }}) that wraps any exception thrown in the block inside `raiseError()`.

### Laws

Arrow provides [`MonadErrorLaws`]({{ '/docs/typeclasses/laws#monaderrorlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `MonadError` instances.

### Data types

The following datatypes in Arrow provide instances that adhere to the `MonadError` typeclass.

- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [Kleisli]({{ '/docs/datatypes/kleisli' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [EitherT]({{ '/docs/datatypes/eithert' | relative_url }})
- [StateT]({{ '/docs/datatypes/statet' | relative_url }})
- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [DeferredKW]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})
