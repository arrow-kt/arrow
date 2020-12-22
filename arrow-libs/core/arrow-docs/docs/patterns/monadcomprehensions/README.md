---
layout: docs-core
title: Monad Comprehensions
permalink: /patterns/monad_comprehensions/
---

## Monad Comprehensions

Monad comprehensions is the name for a programming idiom available in multiple languages like JavaScript, F#, Scala, or Haskell.
The purpose of monad comprehensions is to compose sequential chains of actions in a style that feels natural for programmers of all backgrounds.
They're similar to coroutines or async/await, but extensible to existing and new types!

Let's walk through the evolution of how code was written, up to where comprehensions are today.
It'll take a couple of sections to get there, so if you're familiar with `flatMap`, feel free to skip to [Comprehensions over coroutines]({{ '/patterns/monad_comprehensions/#comprehensions-over-coroutines' | relative_url }}).

### Synchronous sequences of actions

A typical coding class starts teaching new programmers to think like an ideal computer. The computer is fed instructions one by one, and executed one after another.
The instructions modify the internal registers of this ideal computer to store and operate on values. As the values change over time, a result is returned and the program completes.

Let's see one example:

```
int number = 0;
number += 1;
print(number);
return number;
```

This style of programming is what's usually called "imperative programming" because the computer is told what to do.
This style has grown over time through many programming languages with new paradigms, all with this underlying base.
It scaled through decades thanks to Moore's Law, where computers became faster and faster at alarming paces. At some point in the past decade, Moore's Law of vertical scaling plateaued near its theoretical limit.
Scaling started to become horizontal, with multiple cores working in parallel to achieve increasingly complex tasks.
This physical representation of cores became more apparent in software with the increase of multi-threading programs.

Mathematical laws for parallel programming have been known for decades, and applied in multiple languages.
They allow us to write sequenced code that can be run asynchronously over multiple threads, with assurances for completion.

### Asynchronous sequences of actions

The abstraction of sequencing execution of code is summarized in a single function that, in Arrow, is called `invoke`,
although you may find it referred to in other languages as `andThen`, `then`, `bind`, `flatMap` or `SelectMany`. Arrow chooses `invoke` over functions like flatMap because Kotlin is able to perform monad bind in place thanks to its continuation system.

Arrow provides concrete Monad impls for all data types that can support `F<A> -> A` The [typeclass]({{ '/typeclasses/intro' | relative_url }}) interface that abstracts Delimited Scopes and allows us to implement the `suspend operator fun <A> F<A>.invoke(): A` sequenced execution of code via `fold`, `flatMap` and others is called a [`Effect`]({{ '/arrow/continuations/effect' | relative_url }}),
for which we also have a [tutorial]({{ '/patterns/monads' | relative_url }}).

Implementations of [`Effect`]({{ '/arrow/continuations/effect' | relative_url }}) are available for internal types like `Either`, `Option` and others.
Let's see one example of the block `either` that uses [`Effect`]({{ '/arrow/continuations/effect' | relative_url }}) to implement monad `invoke` over `Either`. Here we fetch from a database the information about the dean of a university some student attend:

```kotlin:ank:playground
import arrow.core.computations.either
import arrow.core.Either
import arrow.core.Right
import arrow.core.Left
import arrow.core.flatMap

/* A simple model of student and a university */
object NotFound
data class Name(val value: String)
data class UniversityId(val value: String)
data class University(val name: Name, val deanName: Name)
data class Student(val name: Name, val universityId: UniversityId)
data class Dean(val name: Name)

/* in memory db of students */
private val students = mapOf(
 Name("Alice") to Student(Name("Alice"), UniversityId("UCA"))
)

/* in memory db of universities */
private val universities = mapOf(
 UniversityId("UCA") to University(Name("UCA"), Name("James"))
)

/* in memory db of deans */
private val deans = mapOf(
 UniversityId("UCA") to Dean(Name("James"))
)

/* gets a student by name */
suspend fun student(name: Name): Either<NotFound, Student> = 
  students[name]?.let(::Right) ?: Left(NotFound)
  
/* gets a university by id */
suspend fun university(id: UniversityId): Either<NotFound, University> = 
  universities[id]?.let(::Right) ?: Left(NotFound)
  
/* gets a university by id */
suspend fun dean(name: Name): Either<NotFound, Dean> = 
  deans[name]?.let(::Right) ?: Left(NotFound)

suspend fun main(): Unit {
  //sampleStart
  val dean = student(Name("Alice")).flatMap { alice ->
    university(alice.universityId).flatMap { university ->
      dean(university.deanName)    
    }
  }
  //sampleEnd
  println(dean)
}
```

The sequence of events is assured in that `university` will not be called until `student` returns a result.

If `student` returns `Left(NotFound)`, then `university` and consequently `dean` will never be called.

While this coding style based on flatMap is an improvement for domains like asynchrony in other langs, the readability for users accustomed to traditional imperative code suffers and this style is innecesary in languages like Kotlin with native support for continuations.

### Comprehensions over coroutines

This feature is known with multiple names: async/await, coroutines, do notation, for comprehensions, etc. Each version contains certain unique points, but all derive from the same principles.
In Kotlin, coroutines (introduced in version 1.1 of the language) make the compiler capable of rewriting seemingly synchronous code into asynchronous sequences.
Arrow uses this capability of the compiler to bring you coroutines-like notation to all instances of the [`Effect`]({{ '/arrow/continuations/Effect' | relative_url }}) interface.

This means that comprehensions are available for `Option`, `Either`, `Eval`, and other datatypes.
In the following examples, we'll use `Either`, as it's a simple datatype that thanks to its inlined api and suspended comprehensions can be inter mixed with concurrency and async behaviors in the same scope.

Most instances of [`Effect`]({{ '/arrow/continuations/effect' | relative_url }}) contain a method `invoke` brings the ability to extract in place a type `<A>` from a `F<A>` where F is the implementing data-type of the Effect interface.

The [`Effect`]({{ '/arrow/continuations/effect' | relative_url }}) interface is itself exposed as receiver functions which projects its scope including the ability to perform monad bind via the `invoke` operator.

Let's see a minimal example.

```kotlin:ank:playground
import arrow.core.computations.either

//sampleStart
suspend fun test(): Either<String, Int> =
  either { 1 }
//sampleEnd

suspend fun main() {
  println(test())
}
```

Anything in the function inside `either` can be imperative and sequential code that'll be executed when the data type decides.

In the case of [`Either`]({{ '/arrow-core-data/arrow.core/-either/' | relative_url }}), it is strictly running and implemented in terms of fold. Let's expand the example by adding a second operation:

```kotlin
import arrow.core.computations.either

either {
  val one = Right(1) 
  1 + one 
}
// Compiler error: the type of one is Either<Nothing, Int>, cannot add 1 to it
```

This is our first challenge. We've created an instance of [`Right`]({{ '/arrow-core-data/arrow.core/-either/' | relative_url }}), and we cannot get the value from inside it.
From the previous snippet, the first intuition would be to call `fold` on `one` to get the value and otherwise throw an exception if it was a `Left`.
This will blow up the stack and won't be obvious to users that our method can fail with an exceptions. What we want instead is to suspend and short-circuit on Left values and continue computing over Right values.

```kotlin:ank:playground
import arrow.core.computations.either
import arrow.core.Right

suspend fun test(): Either<String, Int> =
 either {
   val one = Right(1)()
   1 + one 
 }
 
suspend fun main() {
  println(test())
}
```

What `invoke()` does is use the rest of the sequential operations as the function you'd normally pass to `flatMap` and it does so internally using the kotlin suspension system and support for continuations.

The equivalent code without using comprehensions would look like:

```kotlin:ank:playground
import arrow.core.flatMap
import arrow.core.Right

//sampleStart
val x: Either<String, Int> = Right(1)
val result = x.flatMap { one ->
    Right(one + 1)
}
//sampleEnd
suspend fun main() {
  println(result)
}
```

With this new style, we can rewrite our original example of database fetching as:

```kotlin:ank:playground
import arrow.core.computations.either
import arrow.core.Either
import arrow.core.Right
import arrow.core.Left
import arrow.core.flatMap

/* A simple model of student and a university */
object NotFound
data class Name(val value: String)
data class UniversityId(val value: String)
data class University(val name: Name, val deanName: Name)
data class Student(val name: Name, val universityId: UniversityId)
data class Dean(val name: Name)

/* in memory db of students */
private val students = mapOf(
 Name("Alice") to Student(Name("Alice"), UniversityId("UCA"))
)

/* in memory db of universities */
private val universities = mapOf(
 UniversityId("UCA") to University(Name("UCA"), Name("James"))
)

/* in memory db of deans */
private val deans = mapOf(
 UniversityId("UCA") to Dean(Name("James"))
)

/* gets a student by name */
suspend fun student(name: Name): Either<NotFound, Student> = 
  students[name]?.let(::Right) ?: Left(NotFound)
  
/* gets a university by id */
suspend fun university(id: UniversityId): Either<NotFound, University> = 
  universities[id]?.let(::Right) ?: Left(NotFound)
  
/* gets a university by id */
suspend fun dean(name: Name): Either<NotFound, Dean> = 
  deans[name]?.let(::Right) ?: Left(NotFound)

suspend fun main(): Unit {
  //sampleStart
  val dean = either<NotFound, Dean> {
    val alice = student(Name("Alice"))()
    val uca = university(alice.universityId)()
    val james = dean(uca.deanName)()
    james
  }
  //sampleEnd
  println(dean)
}
```

We can observe comparing the original `flatMap` version that thanks to monad the `invoke` operator we can simplify callback nesting and turn code that previously relied on higher order functions such as flatMap into an imperative sequence of commands while preserving the semantics of the data type, in this case `Either`.
