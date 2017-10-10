---
layout: docs
title: State
permalink: /docs/datatypes/state/
---

## State 

`State` is a structure that provides a functional approach to handling application state. `State<S, A>` is basically a function `S -> Tuple2(S, A)`, where `S` is the type that represents your state and `A` is the result the function produces. In addition to returning the result of type `A`, the function returns a new `S` value, which is the updated state.

## Stack

Let's build a simple Stack using an immutable data structure like Kotlin's `List`:

```kotlin:ank:silent
typealias Stack = Option<NonEmptyList<String>>
```

Now we only need the push and pop methods, as follows:

```kotlin:ank:silent
fun pop(stack: Stack) = stack.fold({
    Option.None toT Option.None
}, {
    NonEmptyList.fromList(it.tail) toT it.head.some()
})

fun push(stack: Stack, s: String) = stack.fold({
    NonEmptyList.of(s).some() toT Unit
}, {
    NonEmptyList(s, it.all).some() toT Unit
})

fun stackOperations(stack: Stack): Tuple2<Stack, Option<String>> {
    val (s1, _) = push(stack, "a")
    val (s2, _) = pop(s1)
    return pop(s2)
}
```

```kotlin:ank
stackOperations(NonEmptyList.of("hello", "world", "!").some())
```

```kotlin:ank
stackOperations(NonEmptyList.of("hello").some())
```

As you can see, since we cannot modify the immutable Stack, we need to create a new instance every time we push or pop values from it. For that same reason we have to return the newly created Stack with every operation.

However, it is a bit cumbersome to explicitly pass around all of this intermediate state. It's also a bit error-prone. It would have been easy to accidentally return `pop(s1)`, for example.

## Cleaning it up with State

State's special power is keeping track of state and passing it along. Recall the description of `State` at the beginning of this document. It is basically a function `S -> Tuple2(S, A)`, where `S` is a type representing state.

Our `pop` function takes a `Stack` and returns an updated `Stack` and a `String`. It can be represented as `Stack -> Tuple2(Stack, String)`, and therefore matches the pattern `S -> Tuple2(S, A)` where `S` is `Stack` and `A` is `String`.

Let's write a new version of `pop` and `push` using `State`:

```kotlin:ank:silent
import kategory.*

fun pop() = State<Stack, Option<String>>({ stack ->
    stack.fold({
        Option.None toT Option.None
    }, {
        NonEmptyList.fromList(it.tail) toT it.head.some()
    })
})

fun push(s: String) = State<Stack, Unit>({ stack ->
    stack.fold({
        NonEmptyList.of(s).some() toT Unit
    }, {
        NonEmptyList(s, it.all).some() toT Unit
    })
})
```

The `flatMap` method on `State<S, A>` lets you use the result of one `State` in a subsequent `State`. The updated state (`S`) after the first call is passed into the second call. These `flatMap` and `map` methods allow us to use `State` in for-comprehensions:

```kotlin:ank:silent
fun stackOperations() = StateT.monad<IdHK, Stack>().binding {
    val a = push("a").bind()
    val b = pop().bind()
    val c = pop().bind()

    yields(c)
}.ev()
```

At this point, we have not yet interacted with any Stack; we have written instructions to operate one. We need to pass in an initial stack value, and then we actually apply our operations to it:

```kotlin:ank
stackOperations().run(NonEmptyList.of("hello", "world", "!").some())
```

```kotlin:ank
stackOperations().run(NonEmptyList.of("hello").some())
```

If we only care about the resulting String and not the final state, then we can use `runA`:

```kotlin:ank
stackOperations().runA(NonEmptyList.of("hello", "world", "!").some())
```

Available Instances:

```kotlin:ank
import kategory.debug.*

showInstances<StateHK, Unit>()
```

# Credits

Contents partially adapted from [Cats State](https://typelevel.org/cats/datatypes/state.html)