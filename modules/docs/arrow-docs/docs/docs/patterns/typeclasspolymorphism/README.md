---
layout: docs
title: Monad Comprehensions
permalink: /docs/patterns/typeclass_polymorphism/
---

## TypeClass Polymorphism

{:.advanced}
advanced

What if we could write apps without caring about the runtime data types used but just about how the data is operated on? 
Let's say we have an  application working with RxJava's `Observable`. We'll end up having a bunch of chained call stacks 
based on that given data type. But at the end of the day, and for the sake of simplicity, wouldn't  Observable be just 
like a "container" with some extra powers?

And same story for other "containers" like `Flowable` , `Deferred` (coroutines), `Future`, `IO`, and many more. 
Conceptually, all those types represent an operation (already done or pending to be done), which could support things 
like mapping over the inner value, flatMapping to chain other operations of the same type, zipping it with other 
instances of the same type, and so on.

What if we could write our programs just based on those behaviours in such a declarative style? We could make them be 
agnostic from concrete data types like `Observable`. We would just need to be sure that the data types support a certain 
contract, so they are "mappable", "flatMappable", and so on.

This approach could sound a bit weird or smell to overengineering, but it has a bunch of benefits. Let's put our eyes on 
a simple example first and then we talk about those. Deal?

### A canonical problem

I'm grabbing the following code samples from my mate [Raúl Raja](https://twitter.com/raulraja) who helped to polish this 
post.

Let's say we have a **TODO** app, and we want  to fetch some user Tasks  from a local cache. In case we don't find them, 
we'll try to fetch them from a network service. We could have a simple contract for for both of the DataSources able to 
retrieve a List of Tasks for a given User, regardless of the source:

```kotlin
interface DataSource {
  fun allTasksByUser(user: User): Observable<List<Task>>
}
```

We'll return `Observable` here just for simplicity. It could be `Single`, `Maybe`, `Flowable`, `Deferred`, or anything 
else depending on our needs.

Now, let's add a couple of mocked implementations for it, mimicking what could be a **local** and a **remote** 
`DataSource`.

```Kotlin
class LocalDataSource : DataSource {
  private val localCache: Map<User, List<Task>> =
    mapOf(User(UserId("user1")) to listOf(Task("LocalTask assigned to user1")))

  override fun allTasksByUser(user: User): Observable<List<Task>> = 
    Observable.create { emitter ->
      val cachedUser = localCache[user]
      if (cachedUser != null) {
        emitter.onNext(cachedUser)
      } else {
        emitter.onError(UserNotInLocalStorage(user))
      }
    }
}

class RemoteDataSource : DataSource {
  private val internetStorage: Map<User, List<Task>> =
    mapOf(User(UserId("user2")) to listOf(Task("Remote Task assigned to user2")))

  override fun allTasksByUser(user: User): Observable<List<Task>> = 
    Observable.create { emitter ->
      val networkUser = internetStorage[user]
      if (networkUser != null) {
        emitter.onNext(networkUser)
      } else {
        emitter.onError(UserNotInRemoteStorage(user))
      }
    }
}
```

It's clearly rusty and both implementations are the same 😆. It's a fairly simple mocked version of both  data sources 
that would ideally retrieve the data from a local cache or a network API.

Here, we're just using an in memory `Map<User, List<Task>>` for each one to hold the data. It's a simple shortcut for 
the example.

Since we got two `DataSources`, we'll need a way to coordinate both. Let's add the following `TaskRepository`:

```kotlin
class TaskRepository(private val localDS: DataSource, 
                     private val remoteDS: RemoteDataSource) {

  fun allTasksByUser(user: User): Observable<List<Task>> =
    localDS.allTasksByUser(user)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .onErrorResumeNext { _: Throwable -> remoteDS.allTasksByUser(user) }
}
```

It basically tries to return the `List<Task>` from the `LocalDataSource`, and if it's not found, it'll try to fetch it 
from Network using the `RemoteDataSource`, as required by our specs.

Let's add a simple dependency provisioning Module. We'll use it to get all our instances up and running in a nested way. 
We'll avoid using any Dependency Injection frameworks :

```kotlin
class Module {
  private val localDataSource: LocalDataSource = LocalDataSource()
  private val remoteDataSource: RemoteDataSource = RemoteDataSource()
  val repository: TaskRepository = TaskRepository(localDataSource, remoteDataSource)
}
```

And finally, a simple test to run the whole chain:

```kotlin
object test {

  @JvmStatic
  fun main(args: Array<String>): Unit {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))

    val dependenciesModule = Module()
    dependenciesModule.run {
      repository.allTasksByUser(user1).subscribe({ println(it) }, { println(it) })
      repository.allTasksByUser(user2).subscribe({ println(it) }, { println(it) })
      repository.allTasksByUser(user3).subscribe({ println(it) }, { println(it) })
    }
  }
}
```

This test runs the complete execution chain for three users. Now, let's bring all the mentioned pieces together, in case 
you need to copy/paste anything:

```kotlin
interface DataSource {
  fun allTasksByUser(user: User): Observable<List<Task>>
}

class LocalDataSource : DataSource {
  private val localCache: Map<User, List<Task>> =
    mapOf(User(UserId("user1")) to listOf(Task("LocalTask assigned to user1")))

  override fun allTasksByUser(user: User): Observable<List<Task>> = Observable.create { emitter ->
    val cachedUser = localCache[user]
    if (cachedUser != null) {
      emitter.onNext(cachedUser)
    } else {
      emitter.onError(UserNotInLocalStorage(user))
    }
  }
}

class RemoteDataSource : DataSource {
  private val internetStorage: Map<User, List<Task>> =
    mapOf(User(UserId("user2")) to listOf(Task("Remote Task assigned to user2")))

  override fun allTasksByUser(user: User): Observable<List<Task>> = Observable.create { emitter ->
    val networkUser = internetStorage[user]
    if (networkUser != null) {
      emitter.onNext(networkUser)
    } else {
      emitter.onError(UserNotInRemoteStorage(user))
    }
  }
}

class TaskRepository(private val localDS: DataSource, private val remoteDS: RemoteDataSource) {

  fun allTasksByUser(user: User): Observable<List<Task>> =
    localDS.allTasksByUser(user)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .onErrorResumeNext { _: Throwable -> remoteDS.allTasksByUser(user) }
}

class Module {
  private val localDataSource: LocalDataSource = LocalDataSource()
  private val remoteDataSource: RemoteDataSource = RemoteDataSource()
  val repository: TaskRepository = TaskRepository(localDataSource, remoteDataSource)
}

object test {

  @JvmStatic
  fun main(args: Array<String>): Unit {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))

    val dependenciesModule = Module()
    dependenciesModule.run {
      repository.allTasksByUser(user1).subscribe({ println(it) }, { println(it) })
      repository.allTasksByUser(user2).subscribe({ println(it) }, { println(it) })
      repository.allTasksByUser(user3).subscribe({ println(it) }, { println(it) })
    }
  }
}
```

This program composes the execution chain for three different users, and then subscribes to the resulting `Observable`.
 
The first two `Users` are available, lucky of us. `User1` is available on the local DataSource, and `User2` is available 
on the remote one.

For `User3` we don't have such luck. It's not found on the local one, so it will try to load it from the remote service, 
where it's not found either. The search will fail and we'll print the corresponding error on the subscription.

This is what we get printed for the three cases:

```
> [Task(value=LocalTask assigned to user1)]
> [Task(value=Remote Task assigned to user2)]
> UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user)))
```

This is all what is worth for our canonical example. Let's try to encode it now using a **Functional Programming 
polymorphic style**.

### Abstracting out the data types

Our `DataSource` will look like this from now on:

```kotlin
interface DataSource<F> {
  fun allTasksByUser(user: User): Kind<F, List<Task>>
}
```

It's fairly similar, but it has two important differences. First one, it depends on an `F` generic type. Second one, the 
return type: `Kind<F, List<Task>>`.

`Kind` is basically the way Arrow encodes something called **Higher Kinded Types**. Let's learn the concept pretty 
rapidly with a very basic example.

On `Observable<A>`, we have 2 parts:

* `Observable`: The "witness" type, the container. A fixed type.
* `A`: The generic type argument. An abstraction, and we can pass any types for it.

We're used to abstract over generic types, like `A`. We are familiarized with it. Truth is we can also abstract over 
type containers like `Observable`. That's why "**Higher Kinds**" (Higher Kinded Types) exist.

The overall idea is that we can have constructs as `F<A>` , where both `F` and `A` can be generics. That syntax is not 
supported by the Kotlin compiler ([yet?](https://github.com/Kotlin/KEEP/pull/87)), so we need to mimic it using other 
strategies.

Arrow adds supports to this by using an intermediate meta interface called `Kind<F, A>` that holds references to both 
types and also generates converters at compile time on both directions, so we can go from `Kind<Observable, List<Task>>` 
to `Observable<List<Task>>` and vice versa. Not ideal, but works for what it's worth.

So, if we take a look at our snippet again:

```kotlin
interface DataSource<F> {
  fun allTasksByUser(user: User): Kind<F, List<Task>>
}
```

The `DataSource` function **returns a higher kind**: `Kind<F, List<Task>>` . It translates to `F<List<Task>>`, where `F` 
stays generic.

We're just fixing the `List<Task>` type here, which is already concrete. In other words, we don't care about what's the 
container type (`F`), as long as it keeps  a `List<Task>` inside. a.k.a: We leave the slot open **for passing in 
different containers**. Clear enough? Let's keep moving.

Let's take a look at the `DataSource` implementations, but this time separately for a more gradual learning. The local 
one first:






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
Arrow uses this capability of the compiler to bring you coroutines-like notation to all instances of the [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) typeclass.

Every instance of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) contains a method `binding` that receives a suspended function as a parameter.
This functions must return the last element of the sequence of operations.
Let's see a minimal example.

```kotlin:ank
import arrow.*
import arrow.effects.*
import arrow.typeclasses.*

IO.monad().binding {
  1
}.fix().unsafeRunSync()
```

Anything in the function inside `binding` can be imperative and sequential code that'll be executed when the data type decides.
In the case of [`IO`]({{ '/docs/effects/io' | relative_url }}), it is immediately run blocking the current thread using `unsafeRunSync()`. Let's expand the example by adding a second operation:

```kotlin
IO.monad().binding {
  val a = IO.invoke { 1 }
  a + 1
}.fix().unsafeRunSync()
// Compiler error: the type of a is IO<Int>, cannot add 1 to it
```

This is our first challenge. We've created an instance of [`IO`]({{ '/docs/effects/io' | relative_url }}) that'll run a block asynchronously, and we cannot get the value from inside it.
From the previous snippet the first intuition would be to call `unsafeRunSync()` on `a` to get the value.
This will block the current thread until the operation completes. What we want is to, instead, run and await until `a` completes before yielding the result.
For that we have two flavors of the function `bind()`, which is a function only available inside the function passed to `binding()`.

```kotlin:ank
IO.monad().binding {
  val a = IO.invoke { 1 }.bind()
  a + 1
}.fix().unsafeRunSync()
```

```kotlin:ank
IO.monad().binding {
  val a = bind { IO.invoke { 1 } }
  a + 1
}.fix().unsafeRunSync()
```

What `bind()` does is use the rest of the sequential operations as the function you'd normally pass to `flatMap`.
The equivalent code without using comprehensions would look like:

```kotlin:ank
IO.invoke { 1 }
  .flatMap { result ->
    IO.just(result + 1)
  }
.fix().unsafeRunSync()
```

With this new style we can rewrite our original example of database fetching as:

```kotlin
val university: IO<University> = 
  IO.monad().binding {
    val student = getStudentFromDatabase("Bob Roxx").bind()
    val university = getUniversityFromDatabase(student.universityId).bind()
    val dean = getDeanFromDatabase(university.deanId).bind()
    dean
  }
```

And you can still write your usual imperative code in the binding block, interleaved with code that returns instances of [`IO`]({{ '/docs/effects/io' | relative_url }}).

```kotlin
fun getNLines(path: FilePath, count: Int): IO<List<String>> = 
  IO.monad().binding {
    val file = getFile(path).bind()
    val lines = file.readLines().bind()
    if (lines.length < count) {
      IO.raiseError(RuntimeException("File has fewer lines than expected")).bind()
    } else {
      lines.take(count)
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
    val count = lines.map { it.length }.foldLeft(0) { acc, lineLength -> acc + lineLength }
    val average = count / lines.length
    average
  }
```

What would happen if the file contains 0 lines? The chain throws ArithmeticException with a division by 0!
This exception goes uncaught and finalizes the program with a crash. Knowing this it is obvious we can do better.

Our next approach can do automatic wrapping of unexpected exceptions to return them inside the operation sequence.
For this purpose, the typeclass [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) was created.
[`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) allows us to raise and recover from errors.
It also contains a version of comprehensions that automatically wraps exceptions, called `bindingCatch`.

```kotlin
fun getLineLengthAverage(path: FilePath): IO<List<String>> = 
  IO.monadError().bindingCatch {
    val file = getFile(path).bind()
    val lines = file.readLines().bind()
    val count = lines.map { it.length }.foldLeft(0) { acc, lineLength -> acc + lineLength }
    val average = count / lines.length
    average
  }
```

With a small change we get handling of exceptions even within the binding block.
This wrapping works the same way as if we raised an error as the return from `getFile()` or `readLines()`, short-circuiting and stopping the sequence early.

Note that while most data types include an instance of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}), [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) is somewhat less common.

### What about those threads?

Arrow uses the same abstraction as coroutines to group threads and other contexts of execution: `CoroutineContext`.
There are multiple default values and wrappers for common cases in both the standard library, and the extension library [kotlinx.coroutines](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-coroutine-dispatcher/index.html).

#### Blocking thread jumps

NOTE: This blocking approach to thread jumping will change before the 1.0 release, as it's causing non-blocking datatypes to behave as if they were blocking.

In any `binding()` block there is a helper function `bindIn()` that takes a `CoroutineContext` as a parameter and can return any value.
This value will be lifted into a data type using `just()`.

The functions will cause a new coroutine to start on the `CoroutineContext` passed as a parameter to then `bind()` to await for its completion.

```kotlin
val ioThreadContext = newSingleThreadContext("IO")
val computationThreadContext = newSingleThreadContext("Computation")

fun getLineLengthAverage(path: FilePath): IO<List<String>> = 
  IO.monadError().bindingCatch {
    
    // Implicitly wrap the result of a synchronous operation into IO.just() using bindIn
    val file = bindIn(ioThreadContext) { getFile(path) }    
    val lines = bindIn(computationThreadContext) { file.readLines() }
    
    val count = lines.map { it.length }.foldLeft(0) { acc, lineLength -> acc + lineLength }
    val average = count / lines.length
    average
  }
```

Note that `bindIn()`assures that the execution will return to the same thread where the binding started after the `bindIn` block executes.

There is also a version of `bindIn` called `bindDeferredIn` that allows deferred construction.
It's available for `bindingCancellable` comprehensions over instances of [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}).

#### Non-blocking thread jumps

Any datatype that allows asynchronous execution has to be abstracted by the typeclass [`Async`]({{ '/docs/effects/aync' | relative_url }}).
Thus, it's only logical that these datatypes allow for non-blocking thread jumping.

The typeclass [`Async`]({{ '/docs/effects/aync' | relative_url }}) defines an extension function for comprehensions that enables continuing the execution on a new thread.
This function is called `continueOn()`, takes a `CoroutineContext` and applies the effect of jumping to it, without any value returned.
The rest of the continuation will be executed on that `CoroutineContext`. Simple as that.

Let's see an example:

```
IO.async().run {
  binding {
    // In current thread
    val id = createIdFromNumber(762587).bind()
    continueOn(CommonPool)

    // In CommonPool now!
    val result = request(id).bind()
    continueOn(Ui)

    // In Ui now!
    showResult(result)
  }
}
```

Behind the scenes `continueOn()` starts a new coroutine and passes the rest of the execution as the block to execute.
It's worth reminding that this means that you have to precompute thread local values like a thread name before doing the jump.

### What if I'd like to run multiple operations independently from each other, in a non-sequential way?

You can check the section on the Applicative Builder pattern for them!

### Cancellation and cleanup of resources

In some environments that have resources with their own lifecycle (i.e. Activity in Android development) retaining these values in operations that can run indefinitely may cause large memory leaks and lead to undefined behavior.
As cleanup is important in these restricted environments, any instance of [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}) provides the function `bindingCancellable`, which allows for comprehensions to be finished early by throwing an `BindingCancellationException` at the beginning of the next `bind()` step.

```kotlin
val (binding: IO<List<User>>, unsafeCancel: Disposable) =
  ioSync.bindingCancellable {
    val userProfile = bindDefer { getUserProfile("123") }
    val friendProfiles = userProfile.friends().map { friend ->
        bindDefer { getProfile(friend.id) }
    }
    listOf(userProfile) + friendProfiles
  }

binding.unsafeRunAsync { result ->
  result.fold({ println("Boom! caused by $it") }, { println(it.toString()) })
}

unsafeCancel()
// Boom! caused by BindingCancellationException
```

Note that the cancellation happens on the `bind()` step, so any currently running operations before `bind()` will have to complete first, even those that are scheduled for threading.
