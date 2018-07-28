---
layout: docs
title: Monad
permalink: /docs/typeclasses/monad/
---

## Monad

{:.intermediate}
intermediate

`Monad` is a typeclass that abstracts over sequential execution of code.
This doc focuses on the methods provided by the typeclass.
If you'd like a long explanation of its origins with simple examples with nullable, `Option` and `List`,
head to [The Monad Tutorial]({{ '/docs/patterns/monads' | relative_url }}).

### Main Combinators

`Applicative` includes all combinators present in [`Applicative`]({{ '/docs/typeclasses/applicative/' | relative_url }}).

#### Kind<F, A>#flatMap

Takes a continuation function from the value `A` to a new `Kind<F, B>`, and returns a `Kind<F, B>`.
Internally, `flatMap` unwraps the value inside the `Kind<F, A>` and applies the function to obtain the new `Kind<F, B>`.

Because `Kind<F, B>` cannot be created until `A` is unwrapped, it means that one cannot exists until the other has been executed, effectively making them a sequential chain of execution.

```kotlin:ank
import arrow.core.*
import arrow.instances.*

Some(1).flatMap { a ->
  Some(a + 1)
}
```

The improvement of `flatMap` over regular function composition is that `flatMap` understands about sealed datatypes, and allows for short-circuiting execution.

```kotlin:ank
None.flatMap { a: Int ->
  Some(a + 1)
}
```

```kotlin:ank
Right(1).flatMap { _ ->
  Left("Error")
}.flatMap { b: Int ->
  Right(b + 1)
}
```

Note that depending on the implementation of `Kind<F, A>`, this chaining function may be executed immediately, i.e. for `Option` or `Either`;
or lazily, i.e. `IO` or `ObservableK`.

#### Kind<F, Kind<F, A>>#flatten

Combines two nested elements into one `Kind<F, A>`

```kotlin:ank
ForOption extensions {
  Some(Some(1)).flatten()
}
```

```kotlin:ank
ForOption extensions {
  Some(None).flatten()
}
```

#### mproduct

Like `flatMap`, but it combines the two sequential elements in a `Tuple2`.

```kotlin:ank
ForOption extensions {
  Some(5).mproduct {
    Some(it * 11)
  }
}
```

#### followedBy/followedByEval

Executes sequentially two elements that are independent from one another.
The [`Eval`]({{ '/docs/datatypes/eval' | relative_url }}) variant allows you to pass lazily calculated values.

```kotlin:ank
ForOption extensions {
  Some(1).followedBy(Some(2))
}
```

#### forEffect/forEffectEval

Executes sequentially two elements that are independent from one another, ignoring the value of the second one.
The [`Eval`]({{ '/docs/datatypes/eval' | relative_url }}) variant allows you to pass lazily calculated values.

```kotlin:ank
ForOption extensions {
  Some(1).forEffect(Some(2))
}
```

### Laws

Arrow provides [`MonadLaws`][applicative_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Applicative instances.

#### Creating your own `Monad` instances

Arrow already provides `Monad` instances for most common datatypes both in Arrow and the Kotlin stdlib.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Monad` instances for custom datatypes.

### Data Types

The following data types in Arrow provide instances that adhere to the `Monad` type class.

- [Kleisli]({{ '/docs/datatypes/kleisli' | relative_url }})
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [OptionT]({{ '/docs/datatypes/optiont' | relative_url }})
- [SequenceK]({{ '/docs/datatypes/sequencek' | relative_url }})
- [State]({{ '/docs/datatypes/state' | relative_url }})
- [StateT]({{ '/docs/datatypes/statet' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [EitherT]({{ '/docs/datatypes/eithert' | relative_url }})
- [Eval]({{ '/docs/datatypes/eval' | relative_url }})
- [Id]({{ '/docs/datatypes/id' | relative_url }})
- [Observable]({{ '/docs/integrations/rx2' | relative_url }})
- [Flowable]({{ '/docs/integrations/rx2' | relative_url }})
- [Deferred]({{ '/docs/integrations/kotlinxcoroutines' | relative_url }})
- [Flux]({{ '/docs/integrations/reactor' | relative_url }})
- [Mono]({{ '/docs/integrations/reactor' | relative_url }})
- [IO]({{ '/docs/effects/io' | relative_url }})
