---
layout: docs-incubator
title: StateT
permalink: /docs/arrow/mtl/statet/
redirect_from:
  - /docs/datatypes/statet/
video: 1MJBXKaq0Hc
---

## StateT




`StateT`, also known as the `State` monad transformer, allows computation inside the context when `State` is nested in a different monad.

One issue we face with monads is that they don't compose. This can cause your code to get really hairy when trying to combine structures like `Either` and `State`. But there's a simple solution, and we're going to explain how you can use Monad Transformers to alleviate this problem.

For our purposes here, we're going to transform a monad that serves as a container that represents branching as an an error (left) or state (right) where computation can be performed. Both `State<S, A>` and `Either<L, R>` would be examples of datatypes that provide instances for the `Monad` typeclasses.

Because [monads don't compose](http://tonymorris.github.io/blog/posts/monads-do-not-compose/), we may end up with nested structures such as `Either<Error, State<Either<Error, State<S, Unit>>, Unit>>` when using `Either` and `State` together. Using Monad Transformers can help us reduce this boilerplate.

In the most basic of scenarios, we'll only be dealing with one monad at a time, making our lives nice and easy. However, it's not uncommon to get into scenarios where some function calls will return `Either<Error, A>`, and others will return `State<S, A>`.

So let's rewrite the example of [`State` docs]({{ '/docs/arrow/data/state' | relative_url }}), but instead of representing the `Stack` as an optional `NonEmptyList`, let's represent it as a `List`.

```kotlin:ank
import arrow.core.Tuple2
import arrow.core.toT

typealias Stack = List<String>

fun pop(stack: Stack): Tuple2<Stack, String> = stack.first().let {
  stack.drop(1) toT it
}

fun push(s: String, stack: Stack): Tuple2<Stack, Unit> =
  listOf(s, *stack.toTypedArray()) toT Unit

fun stackOperations(stack: Stack): Tuple2<Stack, String> {
  val (s1, _) = push("a", stack)
  val (s2, _) = pop(s1)
  return pop(s2)
}
```

```kotlin:ank:playground
import arrow.core.Tuple2
import arrow.core.toT

typealias Stack = List<String>

fun pop(stack: Stack): Tuple2<Stack, String> = stack.first().let {
  stack.drop(1) toT it
}

fun push(s: String, stack: Stack): Tuple2<Stack, Unit> =
  listOf(s, *stack.toTypedArray()) toT Unit

fun stackOperations(stack: Stack): Tuple2<Stack, String> {
  val (s1, _) = push("a", stack)
  val (s2, _) = pop(s1)
  return pop(s2)
}

fun main() {
  //sampleStart
  val value = stackOperations(listOf("hello", "world", "!"))
  //sampleEnd
  println("value=$value")
}
```

But if we now `pop` an empty `Stack`, it will result in `java.util.NoSuchElementException: List is empty.`.

```kotlin:ank:fail
import arrow.core.Tuple2
import arrow.core.toT

typealias Stack = List<String>

fun pop(stack: Stack): Tuple2<Stack, String> = stack.first().let {
  stack.drop(1) toT it
}

fun push(s: String, stack: Stack): Tuple2<Stack, Unit> =
  listOf(s, *stack.toTypedArray()) toT Unit

fun stackOperations(stack: Stack): Tuple2<Stack, String> {
  val (s1, _) = push("a", stack)
  val (s2, _) = pop(s1)
  return pop(s2)
}

stackOperations(listOf())
```

Luckily, Arrow offers some nice solutions [`Functional Error Handling` docs]({{ '/docs/patterns/error_handling' | relative_url }}).
Now we can model our error domain with ease.

```kotlin:ank
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.core.toT

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}

fun popE(stack: Stack): Either<StackError, Tuple2<Stack, String>> =
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()

fun pushE(s: String, stack: Stack): Either<StackError, Tuple2<Stack, Unit>> =
  (listOf(s, *stack.toTypedArray()) toT Unit).right()

fun stackOperationsE(stack: Stack): Either<StackError, Tuple2<Stack, String>> {
  return pushE("a", stack).flatMap { (s1, _) ->
    popE(s1).flatMap { (s2, _) ->
      popE(s2)
    }
  }
}
```
```kotlin:ank:playground
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.core.toT

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}

fun popE(stack: Stack): Either<StackError, Tuple2<Stack, String>> =
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()

fun pushE(s: String, stack: Stack): Either<StackError, Tuple2<Stack, Unit>> =
  (listOf(s, *stack.toTypedArray()) toT Unit).right()

fun stackOperationsE(stack: Stack): Either<StackError, Tuple2<Stack, String>> {
  return pushE("a", stack).flatMap { (s1, _) ->
    popE(s1).flatMap { (s2, _) ->
      popE(s2)
    }
  }
}

fun main() {
  val value =
    //sampleStart
    stackOperationsE(listOf("hello", "world", "!"))
  //sampleEnd
  println(value)
}
```
```kotlin:ank:playground
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.core.toT

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}

fun popE(stack: Stack): Either<StackError, Tuple2<Stack, String>> =
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()

fun pushE(s: String, stack: Stack): Either<StackError, Tuple2<Stack, Unit>> =
  (listOf(s, *stack.toTypedArray()) toT Unit).right()

fun stackOperationsE(stack: Stack): Either<StackError, Tuple2<Stack, String>> {
  return pushE("a", stack).flatMap { (s1, _) ->
    popE(s1).flatMap { (s2, _) ->
      popE(s2)
    }
  }
}

fun main() {
  val value =
    //sampleStart
    stackOperationsE(listOf())
  //sampleEnd
  println(value)
}
```

As is immediately clear, this code, while properly modelling the errors, has become more complex. But our signature now represents a simple `Stack` as a `List` with an error domain.
Let's refactor our manual state management in the form of `(S) -> Tuple2<S, A>` to `State`.

So what we want is a return type that represents `Either` a `StackError` or a certain `State` of `Stack.` When working with `State`, we don't pass around `Stack` anymore, so there is no parameter to check if the `Stack` is empty.

```kotlin:ank:silent
import arrow.core.ForId
import arrow.mtl.StateT

fun _popS(): Either<StackError, StateT<ForId, Stack, String>> = TODO()
```

The only thing we can do is handle this with `StateT`. We want to wrap `State` with `Either`.
`EitherKindPartial` is an alias that helps us fix `StackError` as the left type parameter for `Either<L, R>`.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.mtl.StateT
import arrow.mtl.runM
import arrow.mtl.fix

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}
//sampleStart
fun popS() = StateT<EitherPartialOf<StackError>, Stack, String>(Either.monad()) { stack: Stack ->
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()
}

fun pushS(s: String) = StateT<EitherPartialOf<StackError>, Stack, Unit>(Either.monad()) { stack: Stack ->
  (listOf(s, *stack.toTypedArray()) toT Unit).right()
}

fun stackOperationsS(): StateT<EitherPartialOf<StackError>, Stack, String> =
  pushS("a").flatMap(Either.monad()) { _ ->
    popS().flatMap(Either.monad()) { _ ->
      popS()
    }
  }.fix()

fun main() {
  val value = stackOperationsS().runM(Either.monad<StackError>(), listOf("hello", "world", "!"))
  println(value)
}
//sampleEnd
```
```kotlin:ank:playground
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.mtl.StateT
import arrow.mtl.runM
import arrow.mtl.fix

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}

fun popS() = StateT<EitherPartialOf<StackError>, Stack, String>(Either.monad()) { stack: Stack ->
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()
}

fun pushS(s: String) = StateT<EitherPartialOf<StackError>, Stack, Unit>(Either.monad()) { stack: Stack ->
  (listOf(s, *stack.toTypedArray()) toT Unit).right()
}

fun stackOperationsS(): StateT<EitherPartialOf<StackError>, Stack, String> =
  pushS("a").flatMap(Either.monad()) { _ ->
    popS().flatMap(Either.monad()) { _ ->
      popS()
    }
  }.fix()

fun main() {
  val value =
    //sampleStart
    stackOperationsS().runM(Either.monad<StackError>(), listOf())
  //sampleEnd
  println(value)
}
```

While our code looks very similar to what we had before, there are some key advantages. State management is now contained within `State`, and we are dealing with only 1 monad instead of 2 nested monads, so we can use monad bindings!

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.mtl.StateT
import arrow.mtl.extensions.fx

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}

fun popS() = StateT<EitherPartialOf<StackError>, Stack, String>(Either.monad()) { stack: Stack ->
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()
}

fun pushS(s: String) = StateT<EitherPartialOf<StackError>, Stack, Unit>(Either.monad()) { stack: Stack ->
  (listOf(s, *stack.toTypedArray()) toT Unit).right()
}
//sampleStart
fun stackOperationsS2() =
  StateT.fx<EitherPartialOf<StackError>, Stack, String>(Either.monadError<StackError>()) {
    !pushS("a")
    !popS()
    val (string) = popS()
    string
  }

fun main() {
  val value = stackOperationsS2().runM(Either.monad<StackError>(), listOf("hello", "world", "!"))
  println(value)
}
//sampleEnd
```

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.mtl.StateT
import arrow.mtl.extensions.fx

typealias Stack = List<String>
typealias StackEmpty = StackError.StackEmpty

sealed class StackError {
  object StackEmpty : StackError()
}

fun popS() = StateT<EitherPartialOf<StackError>, Stack, String>(Either.monad()) { stack: Stack ->
  if (stack.isEmpty()) StackEmpty.left()
  else stack.first().let {
    stack.drop(1) toT it
  }.right()
}

fun pushS(s: String) = StateT<EitherPartialOf<StackError>, Stack, Unit>(Either.monad()) { stack: Stack ->
  (listOf(s, *stack.toTypedArray()) toT Unit).right()
}
//sampleStart
fun stackOperationsS2() =
  StateT.fx<EitherPartialOf<StackError>, Stack, String>(Either.monadError<StackError>()) {
    !pushS("a")
    !popS()
    val (string) = popS()
    string
  }

fun main() {
  val value =
    //sampleStart
    stackOperationsS2().runM(Either.monad<StackError>(), listOf())
  //sampleEnd
  println(value)
}
```

Take a look at the [`EitherT` docs]({{ '/docs/arrow/mtl/eithert' | relative_url }}) or [`OptionT` docs]({{ '/docs/arrow/mtl/optiont' | relative_url }}) for an alternative version monad transformer for achieving different goals.
