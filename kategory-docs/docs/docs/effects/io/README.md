---
layout: docs
title: IO
permalink: /docs/effects/io/
---

## IO

IO is the most common data type used to represent side-effects in functional languages.
This means IO is the data type of choice when interacting with the external environment: databases, network, operative systems, files...

IO is used to represent operations that can be executed lazily and are capable of failing, generally with exceptions.
This means that code wrapped inside IO will not throw exceptions until it is run, and those exceptions can be captured inside IO for the user to check.

The first challenge for someone new to effects with IO is evaluating its result. Given that IO is used to wrap operations with the environment, 
the return value after completion is commonly used in another part of the program.
Coming from an OOP background the simplest way to use the return value of IO is to consider it as an asynchronous operation you can register a callback for.

## Running IO

IO objects can be constructed passing them functions that will not be immediately called. This is called lazy evaluation.
Running in this context means evaluating the content of an IO object, and propagating its result in a synchronous, asynchronous, or deferred way.

Note that IO objects can be run multiple times, and depending on how they are constructed they will evaluate its content again every time they're run.

### attempt

Executes and defers the result into a new IO that has captured any exceptions inside `Either<Throwable, A>`.
Running this new IO will work over its result, rather than on the original IO content where `attempt()` was called on.

### runAsync

Takes as a parameter a callback from a result of `Either<Throwable, A>` to a new `IO<Unit>` instance.
All exceptions that would happen on the function parameter are automatically captured and propagated to the `IO<Unit>` return.

It runs the current IO asynchronously, calling the callback parameter on completion and returning its result.

### unsafeRunAsync

Takes as a parameter a callback from a result of `Either<Throwable, A>`.
This callback is assumed to never throw any internal exceptions.

It runs the current IO asynchronously, calling the callback parameter on completion.

### unsafeRunSyncTimed

To be use with SEVERE CAUTION, it runs IO synchronously and returns an `Option<A>` blocking the current thread. It requires a timeout parameter.
If the any non-blocking operation performed inside IO lasts longer than the timeout, `unsafeRunSyncTimed` returns `Option.None`.

If your program has crashed, this function call is a good suspect. To avoid crashing use `attempt()` first.

If your multithreaded program deadlocks, this function call is a good suspect.

If your multithreaded program halts and never completes, this function call is a good suspect.

### unsafeRunSync

To be use with SEVERE CAUTION, it runs IO synchronously and returning its result blocking the current thread.

If your program has crashed, this function call is a good suspect. To avoid crashing use `attempt()` first.

If your multithreaded program deadlocks, this function call is a good suspect.

If your multithreaded program halts and never completes, this function call is a good suspect.

## Constructors

Constructing an IO 

### pure

Used to wrap single values. It creates an IO that returns an existing value.

### raiseError

Used to notify of errors during execution. It creates an IO that returns an existing exception.

### invoke

Generally used to wrap existing blocking functions. Creates an IO that invokes one lambda function when run.

Note that this function is evaluated every time IO is run.

### merge

Commonly used to aggregate the results of multiple independent blocking functions. Creates an IO that invokes 2-10 functions when run. Their results are accumulated on a TupleN, where N is the size.

### suspend

Used to defer the evaluation of an existing IO.

### runAsync

Mainly used to integrate with existing frameworks that have asynchronous calls.

It requires a function that provides a callback parameter and it expects for the user to start an operation using the other framework.
The callback parameter has to be invoked with an `Either<Throwable, A>` once the other framework has completed its execution.

## Syntax

### A#liftIO

Puts the value `A` inside an `IO<A>` using `pure`.

## Common operators

IO implements all the operators common to all instances of `MonadError`. Those include `map`, `flatMap`, and `handleErrorWith`.

You can see all the type classes `IO` implements below:

```kotlin:ank
import kategory.debug.*

showInstances<IOHK, Unit>()
```
