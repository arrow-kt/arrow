---
layout: docs
title: Monad
permalink: /docs/arrow/typeclasses/monad/
redirect_from:
  - /docs/typeclasses/monad
---

## Monad

{:.intermediate}
intermediate

`Monad` is a typeclass that abstracts over sequential execution of code.
This doc focuses on the methods provided by the typeclass.
If you'd like a long explanation of its origins with simple examples with nullable, `Option` and `List`,
head to [The Monad Tutorial]({{ '/docs/patterns/monads' | relative_url }}).

### Main Combinators

`Monad` includes all combinators present in [`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }}).

#### Kind<F, A>#flatMap

Takes a continuation function from the value `A` to a new `Kind<F, B>`, and returns a `Kind<F, B>`.
Internally, `flatMap` unwraps the value inside the `Kind<F, A>` and applies the function to obtain the new `Kind<F, B>`.

Because `Kind<F, B>` cannot be created until `A` is unwrapped, it means that one cannot exists until the other has been executed, effectively making them a sequential chain of execution.

```kotlin:ank
import arrow.core.*
import arrow.instances.*
import arrow.effects.*

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
The [`Eval`]({{ '/docs/arrow/core/eval' | relative_url }}) variant allows you to pass lazily calculated values.

```kotlin:ank
ForOption extensions {
  Some(1).followedBy(Some(2))
}
```

#### effectM

Executes two elements sequentially and ignores the result of the second. This is useful for effects like logging.

```kotlin:ank
import arrow.effects.instances.io.monad.*

fun logValue(i: Int): IO<Unit> = IO { /* println(i) */ }

IO.just(1).effectM(::logValue).fix().unsafeRunSync()
```

#### forEffect/forEffectEval

Executes sequentially two elements that are independent from one another, ignoring the value of the second one.
The [`Eval`]({{ '/docs/arrow/core/eval' | relative_url }}) variant allows you to pass lazily calculated values.

```kotlin:ank
import arrow.instances.option.monad.*

Some(1).forEffect(Some(2))
```

### Laws

Arrow provides [`MonadLaws`][monad_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Applicative instances.

#### Creating your own `Monad` instances

Arrow already provides `Monad` instances for most common datatypes both in Arrow and the Kotlin stdlib.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Monad` instances for custom datatypes.

### Data types

The following data types in Arrow provide instances that adhere to the `Monad` type class.

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Monad

TypeClass(Monad::class).dtMarkdownList()
```

<canvas id="hierarchy-diagram" style="margin-top:120px"></canvas>

<script>
  drawNomNomlDiagram('hierarchy-diagram', 'monad.nomnol')
</script>

```kotlin:ank:outFile(monad.nomnol)
import arrow.reflect.*
import arrow.typeclasses.Monad

TypeClass(Monad::class).hierarchyGraph()
```

[monad_law_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/MonadLaws.kt
