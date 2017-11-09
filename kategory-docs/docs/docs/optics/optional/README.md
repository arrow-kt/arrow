---
layout: docs
title: Optional
permalink: /docs/optics/optional/
---

## Optional

An `Optional` is an optic that allows to see into a structure and getting, setting or modifying an optional focus.
It combines the properties of a `Lens` (getting, setting and modifying) with the properties of a `Prism` (an optional focus).

`Optional` combines their weakest functions: `set` and `getOrModify`

* `set: (S, A) -> S` meaning we can look into `S` and set a value for an optional focus `A` and obtain the modified source.
* `getOrModify: (S) -> Either<S, A>` meaning we can get the focus OR return the original value

For a structure `List<Int>` we can create an `Optional` to focus an optional head `Int`.

```kotlin:ank
import kategory.*
import kategory.optics.*

val optionalHead: Optional<List<Int>, Int> = Optional(
    getOrModify = { list -> list.firstOrNull()?.right() ?: list.left() },
    set = { int -> { list -> list.mapIndexed { index, value -> if (index == 0) int else value } } }
)
```

Our `optionalHead` allows us to operate on the head of `List<Int>` without having to worry if it is available. The `optionalHead` optic is by default available as `listHead<Int>()`

```kotlin:ank
listHead<Int>().set(listOf(1, 3, 6), 5)
```
```kotlin:ank
listHead<Int>().modify(listOf(1, 3, 6)) { head -> head * 5 }
```

We can also lift such functions.

```kotlin:ank
val lifted = listHead<Int>().lift { head -> head * 5 }
lifted(emptyList())
```

Or modify or lift functions using `Applicative`

```kotlin:ank
listHead<Int>().modifyF(Try.applicative(), listOf(1, 3, 6)) { head ->
    Try { head / 2 }
}
```
```kotlin:ank
val liftedF = listHead<Int>().liftF(Try.applicative()) { head ->
    Try { head / 0 }
}
liftedF(listOf(1, 3, 6))
```

An `Optional` instance can be manually constructed from any default or custom `Iso`, `Lens` or `Prism` instance by calling their `asOptional()` or by creating a custom `Optional` instance as shown above.

### Composition

We can compose `Optional`s to build telescopes with an optional focus. Imagine we try to retrieve a `User` his email from a backend. The result of our call is `Try<User>`. So we first want to look into `Try` which **optionally** could be a `Success` and then we want to look into `User` which optionally filled in his email.

```kotlin:ank
data class Participant(val name: String, val email: String?)

val participantEmail: Optional<Participant, String> = Optional(
        getOrModify = { participant -> participant.email?.right() ?: participant.left() },
        set = { email -> { participant -> participant.copy(email = email) } }
)

val triedEmail: Optional<Try<Participant>, String> = trySuccess<Participant>() compose participantEmail

triedEmail.getOption(Try.Success(Participant("test", "email")))
```
```kotlin:ank
triedEmail.getOption(Try.Failure(IllegalStateException("Something wrong with network")))
```

`Optional` can be composed with all optics, resulting in the following optics.

|   | Iso | Lens | Prism | Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Optional | Optional | Optional | Optional | Optional | Fold | Setter | Fold | Traversal |

### Polymorphic optional

A `POptional` is very similar to [PLens](docs/optics/Lens#Plens) and [PPrism](docs/optics/Prism/Prism#PPrism) so lets see if we can combine both examples shown in their documentation.

Given a `PPrism` with a focus into `Success` of `Try<Tuple2<Int, String>>` that can polymorphically change its content to `Tuple2<String, String>` and a `PLens` with a focus into the `Tuple2<Int, String>` that can morph the first parameter from `Int` to `String`. We can compose them together build an `Optional` that can look into `Try` and morph the first type of the `Tuple2` within.

```kotlin:ank
val pprism = pTrySuccess<Tuple2<Int, String>, Tuple2<String, String>>()
val plens = pFirstTuple2<Int, String, String>()

val successTuple2: POptional<Try<Tuple2<Int, String>>, Try<Tuple2<String, String>>, Int, String> =
        pprism compose plens

val lifted: (Try<Tuple2<Int, String>>) -> Try<Tuple2<String, String>> = successTuple2.lift { _ -> "Hello, " }
lifted(Try.Success(5 toT "World!"))
```
```kotlin:ank
lifted(Try.Failure<Tuple2<Int, String>>(IllegalStateException("something went wrong")))
```

### Laws

Kategory provides [`OptionalLaws`][optional_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own optionals.

[optional_laws_source]: https://github.com/kategory/kategory/blob/master/kategory-test/src/main/kotlin/kategory/laws/OptionalLaws.kt