---
layout: docs
title: Monad Comprehensions
permalink: /docs/patterns/monad_comprehensions/
---

## Monad Comprehensions

Monad comprehension is the name for a programming idiom available in multiple languages.
The purpose of monad comprehensions is to compose sequential chains of actions in a style that feels natural for programmers of all backgrounds.

### Synchronous sequences of actions

A typical coding class starts teaching new programmers to think like an ideal computer. The computer is fed instructions one by one, and executed one after another.
The instructions modify the internal registers of this ideal computers to store and operate on values. As the values change over time, a result is returned and the program completes.

Let's see one example:

```
int number = 0;
number += 1;
print(number);
return number;
```

This style of programming is what's usually called "imperative programming" after the fact of telling the computer what to do.
This style has grown over time over many programming languages with new paradigms, all with this underlying base.
It scaled during decades thanks to Moore's law, where computer became faster and faster at alarming paces. At some point in the past decade, Moore's law of vertical scaling plateaued near its theoretical limit.
Scaling started to become horizontal, with multiple cores working in parallel to achieve increasingly complex tasks.
This physical representation of cores became more apparent in software with the increase of multi-threading programs.

Mathematical laws for parallel programming have been known for decades, and applied in multiple languages.
They allow us to write sequenced code that can be run asynchronously over multiple threads, with assurances for completion.

### Asynchronous sequences of actions

The general representation of sequenced execution in code is called a [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}). This typeclass is a short API for sequencing code, summarised in a single function `flatMap`.
It takes as a parameter one function to be called after the current operation completes, and that function has to return another [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) to continue the operation with.
A common renaming of `flatMap` is `andThen`. Go to the documentation page to see a deep dive on the Monad API.

With knowledge of `flatMap` we can write sequential expressions that are ran asynchronously, even over multiple threads.
Implementations of `Monad` are available for internal types like `Try` and also integrations like [RxJava 2]({{ '/docs/integrations/rx2' | relative_url }}) and [kotlinx.coroutines]({{ '/docs/integrations/kotlinxcoroutines' | relative_url }}). 
Let's see one example using a [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) called [`IO`]({{ '/docs/effects/io' | relative_url }}), where we fetch from a database the information about the dean of university some student attends:

```kotlin
val university: IO<University> = 
  getStudentFromDatabase("Bob Roxx").flatMap { student ->
      getUniversityFromDatabase(student.universityId).flatMap { university ->
        getDeanFromDatabase(university.deanId)
      }
  }
```

The sequence of events is assured in that `getUniversityFromDatabase` will not be called until `getStudentFromDatabase` returns a result.
If `getStudentFromDatabase` fails, then `getUniversityFromDatabase` will never be called.
That same way, if `getUniversityFromDatabase` then `getDeanFromDatabase` will never be called. Error handling propagates through the chain.

While this coding style is an improvement for asynchrony, the readability for users accustomed to traditional imperative code suffers.
Computer science can bring us a construct to get the best of both styles.

### Comprehensions over coroutines

This feature is known with multiple names: async/await, coroutines, do notation, for comprehensions...each version contains certain unique points but all derive from the same principles.
In Kotlin, coroutines (introduced in version 1.1 of the language) make the compiler capable of rewriting seemingly synchronous code intro asynchronous sequences.
Kategory uses this capability of the compiler to bring you coroutines-like notation to all instances of the [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) typeclass.

Every instance of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) contains a method `binding` that receives a suspended function as a parameter.
This functions must return the last element of the sequence of operations.
`yields()` is a helper function that takes any one value and constructs a correct return.
Let's see a minimal example.

```kotlin:ank
import kategory.*
import kategory.effects.*

IO.monad().binding {
  yields(1)
}.ev().unsafeRunSync()
```

Anything in the function inside `binding` can be imperative and sequential code that'll be executed when the data type decides.
In the case of [`IO`]({{ '/docs/effects/io' | relative_url }}), it is immediately run blocking the current thread using `unsafeRunSync()`. Let's expand the example by adding a second operation:

```kotlin
IO.monad().binding {
  val a = IO.invoke { 1 }
  yields(a + 1)
}.ev().unsafeRunSync()
// Compiler error: the type of a is IO<Int>, cannot add 1 to it
```

This is our first challenge. We've created an instance of [`IO`]({{ '/docs/effects/io' | relative_url }}) that'll run a block asynchronously, and we cannot get the value from inside it.
From the previous snippet the first intuition would be to call `unsafeRunSync()` on `a` to get the value.
This will block the current thread until the operation completes. What we want is to, instead, run and await until `a` completes before yielding the result.
For that we have two flavors of the function `bind()`, which is a function only available inside the function passed to `binding()`.

```kotlin:ank
IO.monad().binding {
  val a = IO.invoke { 1 }.bind()
  yields(a + 1)
}.ev().unsafeRunSync()
```

```kotlin:ank
IO.monad().binding {
  val a = bind { IO.invoke { 1 } }
  yields(a + 1)
}.ev().unsafeRunSync()
```

What `bind()` does is use the rest of the sequential operations as the function you'd normally past to `flatMap`.
The equivalent code without using comprehensions would look like:

```kotlin:ank
IO.invoke { 1 }
  .flatMap { result ->
    IO.pure(result + 1)
  }
.ev().unsafeRunSync()
```

With this new style we can rewrite our original example of database fetching as:

```kotlin
val university: IO<University> = 
  IO.monad().binding {
    val student = getStudentFromDatabase("Bob Roxx").bind()
    val university = getUniversityFromDatabase(student.universityId).bind()
    val dean = getDeanFromDatabase(university.deanId).bind()
    yields(dean)
  }
```

And you can still write your usual imperative code in the binding block, interleaved with code that returns instances of [`IO`]({{ '/docs/effects/io' | relative_url }}).

```kotlin
fun getNLines(path: FilePath, count: Int): IO<List<String>> = 
  IO.monad().binding {
    val file = getFile(path).bind()
    val lines = file.readLines().bind()
    if (lines.length < count) {
      IO.raiseError(RuntimeException("File has fewer lines than expected"))
    } else {
      yields(lines.take(count))
    }
  }
```

While this looks like a great improvement to manually raise errors sometimes you will encounter unexpected behavior and exceptions in seemingly normal code.

### Error propagation in comprehensions

While [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) represents sequential code, it doesn't account for an existing execution flow pattern: exceptions.
Exceptions work like old goto that can happen at any point during execution and stop the current block to jump to a catch block.

Let's take a somewhat common mistake and expand on it:

```kotlin
fun getLineLengthAverage(path: FilePath): IO<List<String>> = 
  IO.monad().binding {
    val file = getFile(path).bind()
    val lines = file.readLines().bind()
    val count = lines.map { it.length }.foldL(0) { acc, lineLength -> acc + lineLength }
    val average = count / lines.length
    yields(average)
  }
```

What would happen if the file contains 0 lines? The chain throws ArithmeticException with a division by 0!
This exception goes uncaught and finalizes the program with a crash. Knowing this it is obvious we can do better.

Our next approach can do automatic wrapping of unexpected exceptions to return them inside the operation sequence.
For this purpose, the typeclass [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) was created.
[`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) allows us to raise and recover from errors.
It also contains a version of comprehensions that automatically wraps exceptions, called `bindingE`.

```kotlin
fun getLineLengthAverage(path: FilePath): IO<List<String>> = 
  IO.monadError().bindingE {
    val file = getFile(path).bind()
    val lines = file.readLines().bind()
    val count = lines.map { it.length }.foldL(0) { acc, lineLength -> acc + lineLength }
    val average = count / lines.length
    yields(average)
  }
```

With a small change we get handling of exceptions even within the binding block.
This wrapping works the same way as if we raised an error as the return from `getFile()` or `readLines()`, short-circuiting and stopping the sequence early.

Note that while most data types include an instance of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}), [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) is somewhat less common.

### What about those threads?

Kategory uses the same abstraction as coroutines to group threads and other contexts of execution: `CoroutineContext`.
There are multiple default values and wrappers for common cases in both the standard library, and the extension library [kotlinx.coroutines](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-coroutine-dispatcher/index.html).

In any `binding()` block there is a helper function `bindIn()` that takes a `CoroutineContext` as a parameter and can return any value. This value will be lifted into a data type using `pure`.
A second version called `bindInM()` requires returning an instance of a data type the same way `binding()` does.

The functions will cause a new coroutine to start on the `CoroutineContext` passed as a parameter to then `bind()` to await for its completion.

```kotlin
val ioThreadContext = newSingleThreadContext("IO")
val computationThreadContext = newSingleThreadContext("Computation")

fun getLineLengthAverage(path: FilePath): IO<List<String>> = 
  IO.monadError().bindingE {
    
    // Wrapping the operation into a suspended asynchronous IO then using bindInM to bind it
    val file = bindInM(ioThreadContext) { IO.pure(getFile(path)) }
    
    // Implicitly wrap the result of a synchronous operation into IO.pure() using bindIn
    val lines = bindIn(computationThreadContext) { file.readLines() }
    
    val count = lines.map { it.length }.foldL(0) { acc, lineLength -> acc + lineLength }
    val average = count / lines.length
    yields(average)
  }
```

Note that `bindIn()` and `bindInM()` don't assure that the execution will return to the same thread where the binding started, as it depends on the implementation of the data type.
This means that for the previous snippet [`IO`]({{ '/docs/effects/io' | relative_url }}) may calculate count and average on different threads than what [`Option`]({{ '/docs/datatypes/option' | relative_url }}) or [`Try`]({{ '/docs/datatypes/try' | relative_url }}) would.

#### Gotchas: lazy evaluation and suspension

It is important to understand that only the block inside `bindInM()` will run on a background thread.
It could be the case that deferred operations using lambdas inside the block would only run those lambdas on the final execution thread.
See this example:

```kotlin
val tc1 = newSingleThreadContext("T1")
val tc2 = newSingleThreadContext("T2")

IO.monad().binding {
  val a = bindInM(tc1){ IO { Thread.currentThread().name } }
  val b = bindIn(tc2) { "in ${Thread.currentThread().name} after $a"}
  yields(b)
}.ev().unsafeRunSync()
//in T2 after main
```

this is caused by `IO` suspending the function `Thread.currentThread().name` in `a`.
The function isn't executed until `unsafeRunSync()` is called in the current main thread.
The fix is trivial simply by capturing the thread name from the outer scope

```kotlin
IO.monad().binding {
    val a = bindInM(tc1){ 
      val name = Thread.currentThread().name
      IO { name } 
    }
    val b = bindIn(tc2) { "in ${Thread.currentThread().name} after $a"}
    yields(b)
}.ev().unsafeRunSync()
//in T2 after T1
```

or making the evaluation happen eagerly

```kotlin
IO.monad().binding {
    val a = bindInM(tc1){ IO.pure(Thread.currentThread().name) }
    val b = bindIn(tc2) { "in ${Thread.currentThread().name} after $a"}
    yields(b)
}.ev().unsafeRunSync()
//in T2 after T1
```

which makes it very visual to understand that `bindIn()` is just an alias for a `bindInM()` that calls the constructor `pure()`.

### What if I'd like to run multiple operations independently from each other, in a non-sequential way?

You can check the section on the [Applicative Builder]({{ '/docs/patterns/applicative_builder' | relative_url }}) pattern for them!

### Cancellation and cleanup of resources

In some environments that have resources with their own lifecycle (i.e. Activity in Android development) retaining these values in operations that can run indefinitely may cause large memory leaks and lead to undefined behavior.
As cleanup is important in these restricted environments, the function `bindingECancellable` allows for comprehensions to be finished early by throwing an `InterruptedException` at the beginning of the next `bind()` step.

```kotlin
val (binding: IO<List<User>>, unsafeCancel: Disposable) =
  ioMonadError.bindingECancellable {
    val userProfile = bindAsync(ioAsync) { getUserProfile("123") }
    val friendProfiles = userProfile.friends().map { friend ->
        bindAsync(ioAsync) { getProfile(friend.id) }
    }
    yields(listOf(userProfile) + friendProfiles)
  }

binding.unsafeRunAsync { result ->
  result.fold({ println("Boom! caused by $it") }, { println(it.toString()) })
}

unsafeCancel()
// Boom! caused by InterruptedException
```

Note that the cancellation happens on the `bind()` step, so any currently running operations before `bind()` will have to complete first, even those that are scheduled for threading.
