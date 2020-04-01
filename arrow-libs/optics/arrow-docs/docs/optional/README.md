---
layout: docs-optics
title: Optional
permalink: /optics/optional/
---

## Optional


An `Optional` is an optic that allows seeing into a structure and getting, setting, or modifying an optional focus.
It combines the properties of a `Lens` (getting, setting, and modifying) with the properties of a `Prism` (an optional focus).

`Optional` combines their weakest functions: `set` and `getOrModify`.

* `set: (S, A) -> S`, meaning we can look into `S`, set a value for an optional focus `A`, and obtain the modified source.
* `getOrModify: (S) -> Either<S, A>`, meaning we can get the focus OR return the original value.

For a structure `List<Int>`, we can create an `Optional` to focus an optional head `Int`.

```kotlin:ank
import arrow.core.*
import arrow.optics.*

val optionalHead: Optional<ListK<Int>, Int> = Optional(
    getOption = { list -> list.firstOrNull().toOption() },
    set = { list, int -> list.mapIndexed { index, value -> if (index == 0) int else value }.k() }
)
```

Our `optionalHead` allows us to operate on the head of `List<Int>` without having to worry if it is available. You can find `optionalHead` in the optics library: `ListK.head<Int>()`.

```kotlin:ank
import arrow.optics.extensions.*

ListK.head<Int>().set(listOf(1, 3, 6).k(), 5)
```
```kotlin:ank
ListK.head<Int>().modify(listOf(1, 3, 6).k()) { head -> head * 5 }
```

We can also lift such functions.

```kotlin:ank
val lifted = ListK.head<Int>().lift { head -> head * 5 }
lifted(emptyList<Int>().k())
```

Or modify or lift functions using `Applicative`.

```kotlin:ank
import arrow.fx.IO
import arrow.core.extensions.option.applicative.*

ListK.head<Int>().modifyF(Option.applicative(), listOf(1, 3, 6).k()) { head ->
    Option.just(head/2)
}
```
```kotlin:ank
import arrow.fx.extensions.io.applicative.*
import arrow.fx.fix
import arrow.fx.unsafeRunSync

val liftedFO = ListK.head<Int>().liftF(IO.applicative()) { head ->
    IO.effect { head / 0 }
}
liftedFO(listOf(1, 3, 6).k()).fix().attempt().unsafeRunSync()
```

An `Optional` instance can be manually constructed from any default or custom `Iso`, `Lens`, or `Prism` instance by calling their `asOptional()` or by creating a custom `Optional` instance as shown above.

### Composition

We can compose `Optional`s to build telescopes with an optional focus. Imagine we try to retrieve a `User`'s email from a backend. The result of our call is `Option<User>`. So, we first want to look into `Option`, which **optionally** could be a `Some`. And then we want to look into `User`, which optionally filled in his email.

```kotlin:ank
import arrow.optics.some

data class Participant(val name: String, val email: String?)

val participantEmail: Optional<Participant, String> = Optional(
        getOrModify = { participant -> participant.email?.right() ?: participant.left() },
        set = { participant, email -> participant.copy(email = email) }
)

val optEmail: Optional<Option<Participant>, String> = Option.some<Participant>() compose participantEmail

optEmail.getOption(Some(Participant("test", "email")))
```
```kotlin:ank
optEmail.getOption(None)
```
```kotlin:ank
optEmail.getOption(Some(Participant("test", null)))
```

`Optional` can be composed with all optics, resulting in the following optics:

|   | Iso | Lens | Prism | Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Optional | Optional | Optional | Optional | Optional | Fold | Setter | Fold | Traversal |

### Generating optional

To avoid boilerplate, optionals can be generated for `A?`, and `Option<A>` fields for a `data class`.
The `Optionals` will be generated as extension properties on the companion object `val T.Companion.paramName`.

```kotlin
@optics data class Person(val age: Int?, val address: Option<Address>) {
  companion object
}
```
```kotlin:ank:silent
val optionalAge: Optional<Person, Int> = Person.age
val optionalAddress: Optional<Person, Address> = Person.address
```

### Polymorphic optional

A `POptional` is very similar to [PLens]({{'/optics/lens#Plens' | relative_url }}) and [PPrism]({{'/optics/prism#PPrism' | relative_url }}). So let's see if we can combine both examples shown in their documentation.

Given a `PPrism` with a focus into `Some` of `Option<Tuple2<Int, String>>` that can polymorphically change its content to `Tuple2<String, String>` and a `PLens` with a focus into the `Tuple2<Int, String>` that can morph the first parameter from `Int` to `String`, we can compose them together building an `Optional` that can look into `Option` and morph the first type of the `Tuple2` within.

```kotlin:ank
val pprism = Option.PSome<Tuple2<Int, String>, Tuple2<String, String>>()
val plens = Tuple2.pFirst<Int, String, String>()

val someTuple2: POptional<Option<Tuple2<Int, String>>, Option<Tuple2<String, String>>, Int, String> =
    pprism compose plens

val lifted: (Option<Tuple2<Int, String>>) -> Option<Tuple2<String, String>> = someTuple2.lift { _ -> "Hello, " }
```
```kotlin:ank
lifted(None)
```

### Laws

Arrow provides [`OptionalLaws`][optional_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own optionals.

[optional_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/OptionalLaws.kt
