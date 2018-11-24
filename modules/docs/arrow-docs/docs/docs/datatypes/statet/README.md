---
layout: docs
title: StateT
permalink: /docs/datatypes/statet/
video: 1MJBXKaq0Hc
---

## StateT

{:.advanced}
advanced

`StateT` also known as the `State` monad transformer allows to compute inside the context when `State` is nested in a different monad.

One issue we face with monads is that they don't compose. This can cause your code to get really hairy when trying to combine structures like `Either` and `State`. But there's a simple solution, and we're going to explain how you can use Monad Transformers to alleviate this problem.

For our purposes here, we're going to transform a monad that serves as a container that represents branching as an an error (left) or state (right) where computation can be performed. Given that both `State<S, A>` and `Either<L, R>` would be examples of datatypes that provide instances for the `Monad` typeclasses.

Because [monads don't compose](http://tonymorris.github.io/blog/posts/monads-do-not-compose/), we may end up with nested structures such as `Either<Error, State<Either<Error, State<S, Unit>>, Unit>>` when using `Either` and `State` together. Using Monad Transformers can help us to reduce this boilerplate.

In the most basic of scenarios, we'll only be dealing with one monad at a time making our lives nice and easy. However, it's not uncommon to get into scenarios where some function calls will return `Either<Error, A>`, and others will return `State<S, A>`.

So let's rewrite the example of [`State` docs]({{ '/docs/datatypes/state' | relative_url }}), but instead of representing the `Stack` as an optional `NonEmptyList` let's represent it as a `List`.

```kotlin:ank:silent
import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

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

```kotlin:ank
stackOperations(listOf("hello", "world", "!"))
```

But if we now `pop` an empty `Stack` it will result in `java.util.NoSuchElementException: List is empty.`.

```kotlin
_stackOperations(listOf()) //java.util.NoSuchElementException: List is empty.
```

Luckily Arrow offers some nice solutions [`Functional Error Handling` docs]({{ '/docs/patterns/error_handling' | relative_url }}).
Now we can model our error domain with ease.

```kotlin:ank:silent
sealed class StackError {
    object StackEmpty : StackError()
}

typealias StackEmpty = StackError.StackEmpty

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
```kotlin:ank
stackOperationsE(listOf("hello", "world", "!"))
```
```kotlin:ank
stackOperationsE(listOf())
```

As is immediately clear, this code while properly modelling the errors, has become more complex but our signature now represents a simple `Stack` as a `List` with an error domain.
Let's refactor our manual state management in the form of `(S) -> Tuple2<S, A>` to `State`.

So what we want is a return type that represents `Either` a `StackError` or a certain `State` of `Stack.` When working with `State` we don't pass around `Stack` anymore, so there is no parameter to check if the `Stack` is empty.

```kotlin:ank:silent
fun _popS(): Either<StackError, StateT<ForId, Stack, String>> = TODO()
```

The only thing we can do is handle this with `StateT`. We want to wrap `State` with `Either`.
`EitherKindPartial` is an alias that helps us to fix `StackError` as the left type parameter for `Either<L, R>`.

```kotlin:ank
import arrow.instances.either.monad.*

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

stackOperationsS().runM(Either.monad<StackError>(), listOf("hello", "world", "!"))
```
```kotlin:ank
stackOperationsS().runM(Either.monad<StackError>(), listOf())
```

While our code looks very similar to what we had before there are some key advantages. State management is now contained within `State` and we are dealing only with 1 monad instead of 2 nested monads so we can use monad bindings!

```kotlin:ank
import arrow.typeclasses.*
import arrow.instances.*
import arrow.instances.either.monadError.*

fun stackOperationsS2() = 
 ForStateT<EitherPartialOf<StackError>, Stack, StackError>(Either.monadError<StackError>()) extensions {
  binding {
    pushS("a").bind()
    popS().bind()
    val string = popS().bind()
    string
  }.fix()
 }

stackOperationsS2().runM(Either.monad<StackError>(), listOf("hello", "world", "!"))
```

```kotlin:ank
stackOperationsS2().runM(Either.monad<StackError>(), listOf())
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.data.*
import arrow.core.*

DataType(StateT::class).tcMarkdownList()
```

Take a look at the [`EitherT` docs]({{ '/docs/datatypes/eithert' | relative_url }}) or [`OptionT` docs]({{ '/docs/datatypes/optiont' | relative_url }}) for an alternative version monad transformer for achieving different goals.
