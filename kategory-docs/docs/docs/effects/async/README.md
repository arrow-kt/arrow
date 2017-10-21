---
layout: docs
title: Async
permalink: /docs/effects/async/
---

## AsyncContext

Being able to run code in a different context of execution (i.e. thread) than the current one implies that, even if it's part of a sequence, the code will have to be asynchronous.
Running asynchronous code always requires a callback after completion on error capable of returning to the current thread.

The same way the typeclass [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) represents a sequence of events, and [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) a sequence that can fail, the typeclass `AsyncContext` represents asynchronous code with a callback.
Examples of that can run code asynchronously are typically datatypes that can suspend effects, and delay evaluation.

```kotlin:ank
import kategory.effects.*

IO.asyncContext()
  .runAsync { callback: (Either<Throwable, Int>) -> Unit -> 
    callback(1.right()) 
  }
```

### Main Combinators

#### runAsync

Receives a function returning unit with a callback as a parameter.
The function is responsible of calling the callback once it obtains a result.
The callback accepts `Either<Throwable, A>` as possible results, where the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution and the right side is the correct result type.

### Syntax

#### (() -> A)#runAsync

Runs the current function in the AsyncContext passed as a parameter.

Note that there is no automatic error handling or wrapping of exceptions.

#### (() -> Either<Throwable, A>)#runAsyncUnsafe

Runs the current function in the AsyncContext passed as a parameter.

While there is no wrapping of exceptions, the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution.

#### binding#bindAsync

While in a binding block of a Monadic Comprehension, bindAsync runs a function parameter in the AsyncContext passed as a parameter,
and then awaits for the result before continuing the execution.

Note that there is no automatic error handling or wrapping of exceptions.

#### binding#bindAsyncUnsafe

While in a binding block of a Monadic Comprehension, bindAsync runs a function parameter in the AsyncContext passed as a parameter,
and then awaits for the result before continuing the execution.

While there is no wrapping of exceptions, the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution.

### Laws

Kategory provides [`AsyncLaws`]({{ '/docs/typeclasses/laws#asynclaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `AsyncContext` instances.

### Data types

The following datatypes in Kategory provide instances that adhere to the `AsyncContext` typeclass.

- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableKW]({{ '/docs/integrations/rx2' | relative_url }})
