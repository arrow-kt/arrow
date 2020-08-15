# Arrow Fx Coroutines

Arrow Fx Coroutines offers a suspend DSL with the same semantics and API you find in Arrow Fx's IO, this allows for writing programs using suspend fun directly instead of wrapping in IO.
It's a functional implementation of Kotlin's coroutine system while providing support for cancellation, error handling, resource handling and all goodies you might be familiar with from other functional effect systems.

Arrow Fx aims to be a battery included functional effects framework, below you can find a small overview of the library.
A full coverage of all data types and functions can be found [here](LINK DOCS).

## IO\<A\> vs suspend () -> A

Let's look at two simple concurrent examples that switch context and print the `Thread.currentThread().name`.

```kotlin:ank
suspend fun getThreadName(): String =
  Thread.currentThread().name

suspend fun main(): Unit = IO.fx {
  val t1 = !effect { getThreadName() }.evalOn(Computation) 
  val t2 = !effect { getThreadName() }.evalOn(BlockingIO)
  val t3 = !effect { getThreadName() }.evalOn(UI)
  !effect { println("$t1 ~> $t2 ~> $t3") }
}.suspended()
```

becomes

```kotlin:ank
suspend fun getThreadName(): String =
  Thread.currentThread().name

suspend fun main(): Unit {
  val t1 = evalOn(Computation) { getThreadName() }
  val t2 = evalOn(BlockingIO) { getThreadName() }
  val t3 = evalOn(UI) { getThreadName() }
  println("$t1 ~> $t2 ~> $t3")
}
```

As you can see we can directly apply `evalOn` to our `suspend fun`, this eliminates the need to wrap explicitly in `IO`.
Both programs are equivalent in semantics and guarantees, the later will however perform better since it eliminates the wrapping `IO` requires.
This is possible without losing cancellation support, as explained in the detail here [anchor cancellation].

## IO<Either<E, A>> vs suspend () -> Either<E, A>

When writing functional code style we often want to express our domain errors as clearly as possible, a popular pattern is to return `Either<DomainError, SuccessValue>`.
Let's assume following domain, and compare two snippets one using `IO<Either<E, A>>` and another `suspend () -> Either<E, A>`.

```kotlin:ank
inline class Id(val id: Long)
object PersistenceError

data class User(val email: String, val name: String)
data class ProcessedUser(val id: Id, val email: String, val name: String)

suspend fun fetchUser(): Either<PersistenceError, User> =
    Right(User("simon@arrow-kt.io", "Simon"))

suspend fun User.process(): Either<PersistenceError, ProcessedUser> =
    if (email.contains(Regex("^(.+)@(.+)$"))) Right(ProcessedUser(UUID.V4.squuid(), email, name))
    else Left(PersistenceError)
```

### IO<Either<E, A>>

 ```kotlin:ank
fun ioProgram(): IO<Either<PersistenceError, ProcessedUser>> =
  IO.fx {
    val res = !IO.effect { fetchUser() }

    !res.fold({ error ->
      IO.just(Either.Left(error))
    }, { user ->
      IO.effect { user.process() }
    })
  }

// Or unwrapped in `suspend`
suspend suspendedIOProgram(): Either<PersistenceError, ProcessedUser> =
  ioProgram().suspended()
 ```

### suspend () -> Either<E, A>

```kotlin:ank
suspend fun either(): Either<PersistenceError, ProcessedUser> =
  Either.fx {
    val user = !fetchUser()
    val processed = !user.process()
    processed
  }
```

### suspend R.() -> A

We can use extension functions to do functional dependency injection with similar semantics as `Reader` or `Kleisli`.
They allow us to elegantly define `syntax` for a certain type. Let's see a simple example.

Let's reuse our previous domain of`User`, `ProcessedUser`, but let's introduce `Repo` and `Persistence` layers to mimick what could be a small app with a couple layers.

```kotlin:ank:playground
interface Repo {
    suspend fun fetchUsers(): List<User>
}

interface Persistence {
    suspend fun User.process(): Either<ProcessingError, ProcessedUser>

    suspend fun List<User>.process(): Either<ProcessingError, List<ProcessedUser>> =
        Either.fx { map { !it.process() } }
}
```

Given the above defined layers we can easily compose them by creating a product which implements the dependencies by delegation.
This can easily be done manually or with the help of a framework like Dagger to do this wiring automatically.

```kotlin:ank
class DataModule(
    persistence: Persistence,
    repo: Repo
) : Persistence by persistence, Repo by repo
```

We can also define top-level functions based on constraints on the receiver.
Here we define `getProcessUsers` which can only be called where `R` is both `Repo` and `Persistence`.

```kotlin:ank
/**
 * Generic top-level function based on syntax enabled by [Persistence] & [Repo] constraint
 */
suspend fun <R> R.getProcessUsers(): Either<ProcessingError, List<ProcessedUser>>
        where R : Repo,
              R : Persistence = fetchUsers().process()
```

## Cancellation

The cancellation system exists out of a few simple building blocks.

All operators found in Arrow Fx check for cancellation. In the small example of an infinite sleeping loop below `sleep` checks for cancellation and thus this function also check for cancellation before/and while sleeping.

```kotlin:ank
tailrec suspend fun sleeper(): Unit {
  println("I am sleepy. I'm going to nap")
  sleep(1.seconds)                                     // <-- cancellation check-point
  println("1 second nap.. Going to sleep some more")
  sleeper()
}
```

### cancelBoundary()

Calling `suspend fun cancelBoundary()` will check for cancellation, and will gracefully exit in case the effect was cancelled. An example.

```
suspend fun loop(): Unit {
  while(true) { 
	 cancelBoundary() // cancellable computation loop
    println("I am getting dizzy...")
  }
}
```

This `while` will `loop` until the cancel signal is triggered. Once the cancellation is trigger, this task will gracefully exit through `cancelBoundary()`.

In case you don't want to check for cancellation so often, you can also only install a `cancelBoundary` every n batches.
The example below defines `repeat` which checks cancellation every `10` repetition.

```kotlin:ank
tailrec suspend fun repeat(n: Int): Unit {
  if (n % 10 == 0) cancelBoundary()
  if (n == 0) Unit
  else repeat(n - 1)
}
```

### Parallel operations cancellation

All parallel `suspend` operators in Arrow Fx behave in the following way.

1. When one of the parallel task fails, the others are also cancelled since a result cannot be determined. This will allow the other parallel operations to gracefully exit and close their resources before returning.

2. When the resulting `suspend` operation is cancelled than all running fibers inside will also be cancelled so that all paralell running task can gracefully exit and close their resources before returning.

For more documentation on parallel operations see below.

### Uncancellable

So how can you execute of `suspend fun` with guarantee that it cannot be cancelled. You simply `wrap` it in the `uncancelable` builder and the function will guarantee not to be cancelled. If the progam is already cancelled before, this block will not run and if it gets cancelled during the execution of this block it will exit immediately after.

```kotlin:ank
suspend fun uncancellableSleep(duration: Duration): Unit =
  uncancellable { sleep(duration) }
```

If we now re-implement our previous `sleeper`, than it will behave a little different from before. The cancellation check before and after `uncancellableSleep` but note that the `sleep` istelf will not be cancelled.

```kotlin:ank
tailrec suspend fun sleeper(): Unit {
  println("I am sleepy. I'm going to nap")
   // <-- cancellation check-point
  uncancellableSleep(1.seconds)
   // <-- cancellation check-point
  println("1 second nap.. Going to sleep some more")
  sleeper()
}
```

This also means that our new sleep can back-pressure `timeOutOrNull`.

```kotlin:ank
import arrow.fx.coroutines.*

suspend fun main(): Unit {
  val r = timeOutOrNull(1.seconds) {
    uncancellable { sleep(2.seconds) }
  } // r is null, but took 2 seconds.
}
```

## Resource Safety

To ensure resource safety we need to take care with cancellation since we don't wont our process to be cancelled but our resources to remain open.

There Arrow Fx offers 2 tools `Resource` and `suspend fun bracketCase`. Any `resource` operations exists out of 3 steps.

1. Acquiring the resource
2. Using the resource
3. Releasing the resource with either a result, a `Throwable` or `Cancellation`.

To ensure the resource can be correctly acquired we make the `acquire` & `release` step `uncancelable`.
If the `bracketCase` was cancelled during `acquire` it'll immediately go to `release`, skipping the `use` step.

`bracketCase` is defined below, in the `release` step you can inspect the `ExitCase` of the `acquire`/`use`.

```
sealed ExitCase {
  object Completed: ExitCase()
  object Cancelled: ExitCase()
  data class Error(val error: Throwable): ExitCase()
}

suspend fun <A, B> bracketCase(acquire: suspend () -> A, use: suspend (A) -> B, release: (a, ExitCase) -> B): B
```

`bracket` is an overload of `bracketCase` that ignores the `ExitCase` value, a simple example.
We want to create a function to safely create and consume a `DatabaseConnection` that always needs to be closed no matter what the _ExitCase_.

```kotlin:ank
class DatabaseConnection {
  suspend fun open(): String = println("Database connection opened")
  suspend fun close(): String = println("Database connection closed")
}

suspend fun <A> DatabaseConnection.use(f: suspend (DatabaseConnection) -> A): A =
  bracket(
    acquire = { DatabaseConnection().apply { open() } },
    use = f,
    release = DatabaseConnection::close
  )
```

The difference between `Resource` is that `bracketCase` is simple function, while `Resource` is a data type, both ensure that resources are `acquire`d and `release`d correctly.
It also forms a `Monad` so you can use it to safely compose `Resource`s, map them or safely traverse `Resource`s.

```kotlin:ank
suspend fun service(nThreads: Int, name: String): ExecutorService {
  val threadN = AtomicReference(1)
  return Executors.newScheduledThreadPool(nThreads) { r ->
    Thread(r, if (nThreads == 1) name else "$name-${threadNo.incrementAndGet()}")
    .apply { isDaemon = true}
  }
}

suspend fun shutdown(service: ExecutorService): Unit =
  service.shutdown()

suspend fun singleThreadContext(name: String): Resource<CoroutineContext> =
  Resource({ service(1, name) }, ::shutdown)
    .map(ScheduledExecutorService::asCoroutineContext)

suspend fun main(): Unit {
  singleThreadContext("example-pool").use { ctx ->
     evalOn(ctx) {
        println("Running on example-pool: $Thread.currenThread().name")
     }
  }
}
```

A more advanced `Resource` example that reads 3 `File`s in parallel

```kotlin:ank
class File(url: String) {
  fun open(): File = this
  suspend fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

suspend fun openFile(uri: String): File = File(uri).open()

val resources: List<Resource<File>> =
 listOf(
   Resource({ openFile("path1") }, File::close),
   Resource({ openFile("path2") }, File::close),
   Resource({ openFile("path3") }, File::close)
 )

val resource: Resource<List<File>> =
  resources.sequence(Resource.applicative())

suspend fun main(): Unit {
  resource.use { files ->
    files.parTraverse(IODispatchers.IOPool) { file ->
       file.toString()
    }
  }
}
```

## Concurrency Helpers

### Ref vs Atomic

`Ref` has been renamed `Atomic` in the new API; and, it provides the same level of service as `Ref` 
(i.e. `Atomic` provides a safe concurrent API to access to a mutable reference).

For example

```kotlin
fun factorial(n: Int) = IO.fx {
    val res = Ref(1L).bind()

    (1 until n+1).parTraverse {
        res.update(it::times)
    }.bind()
    res.get().bind()
}
```

becomes
 
```kotlin:ank
 suspend fun factorial(n: Int)  {
    val res = Atomic(1L)

    (1 until n+1).parTraverse {
        res.update(it::times)
    }
    res.get()
}
```

This code snippet isn’t very useful, except to show how `Ref/Atomic` can be used with concurrent access. 

### MVar vs ConcurrentVar

`MVar` is now called `ConcurrentVar`; and, it provides the same level of service as `MVar` 
(i.e. a `ConcurrentVar` is a mutable concurrent safe variable which is either `empty` or contains a typed `single value`).

```kotlin:ank
tailrec
fun sum(state: MVar<ForIO, Int>, list: List<Int>) : IO<Int> =
    IO.fx {
        when {
            list.isEmpty() -> state.take().bind()
            else -> state.take().flatMap { cur ->
                state.put(cur + list[0]).flatMap { _ -> sum(state, list.tail()) }
            }.bind()
        }
    }
```

becomes
 
```kotlin:ank
tailrec
suspend fun sum(state: ConcurrentVar<Int>, list: List<Int>) {
    when {
        list.isEmpty() -> state.take()
        else -> {
            val cur = state.take()
            state.put(cur + list[0])
            sum(state, list.tail())
        }
    }
}
```

This code snippet isn’t very useful, except to show how the atomic calls of `MVar/ConcurrentVar` can be used.

## Error Handling

In Kotlin with suspend `try/catch` can safely be used to recover from exceptions.

Simple constructs like `suspend fun Either.catch(f: () -> A): Either<Throwable, A>` are available for nicer syntax when and lifting errors in your domain.

## Retrying and repeating effects

`Schedule` allows you to define and compose powerful yet simple policies, which can be used to either repeat or retry computation. 

A simple example might be to repeat an action `n` times, similar to the `repeat` function in the standard library.

```kotlin:ank
suspend fun main(): Unit {
  repeat(Schedule.recurs<A>(n)) {
    println("Hello")
  }
}
```

Alternatively we can re-use this `Schedule` to `retry` a `suspend fun` `n` times when it fails.

```kotlin:ank
suspend fun main(): Unit {
  retry(Schedule.recurs<A>(n)) {
    println("I am going to do nothing but throw a tantrum!")
    throw RuntimeException("Boom!")
  }
}
```

Additionally a `Schedule` can also produce an output, let's compose our simple `recurs` Schedule to `recur` every `10.seconds` in a `spaced` fashion and collect all outputs along the way.

```kotlin:ank
fun <A> schedule(): Schedule<A, List<A>> = Schedule {
  (recurs<A>(10) and spaced(10.seconds)) zipRight collect()
}

suspend fun main(): Unit {
  val count = Atomic(0)

  val history: List<Int> = repeat(schedule<Int>()) {
    println("Incrementing the ref")
    count.update(Int::inc)
  }
}
```

## Parallel Ops


### ForkConnected

`forkConnected` launches a `suspend () -> A` function on a given `CoroutineContext` inside a `Fiber`, the lifecycle of this `Fiber` is connected to its parent.
If the parent cancels that this operation will also be cancelled.

A `Fiber` is a data type that represents a `suspend fun join(): A` and `suspend fun cancel(): Unit` handle.

### ForkScoped

`forkScoped` is a similar construct as `ForkConnected` but instead of automatically connecting to its parent it takes a `suspend () -> Unit` op which triggers the cancellation.
This can be used as a lower level construct to signal cancellation on a trigger, and wire cancellation with 3rd party frameworks.

```kotlin:ank
tailrec fun fun sleeper(): Unit {
  sleep(1.seconds)
  return sleeper()
}


class AndroidFragment : Fragment {

  val cancelPromise = Promise.unsafe<Unit>()
  
  override fun onCreateView(bundle: SavedBundleInstance): Unit {
     suspend {
       ::sleeper.forkScoped(ComputationPool, cancelPromise::get)
     }.startCoroutine(Continuation(EmptyCoroutineContext) { })
  }
  
  override fun onDestroyView(): Unit {
    suspend { cancelPromise.complete(Unit) }
      .startCoroutine(Continuation(EmptyCoroutineContext) { })
  }
}
```

### ParMapN/parTupledN

`parMapN` combines two `suspend` functions given a pure function `(A, B) -> C` in parallel on a given `CoroutineContext`. Both `suspend fun` will guarantee to dispatch on the given `CoroutineContext` before they start running.

It also wires their respective cancellation. That means that cancelling the resulting `suspend fun` will cancel both functions running in parallel inside.
Additionally, the function does not return until both tasks are finished and their results combined by `f: (A, B) -> C`.


### ParTraverse/parSequence

`parTraverse` can be used to traverse any data type that supports `Traverse` such as `List`, `Validated`, `Either`, `Option`, etc.
It is used to map elements of the same type `A` in parallel. Cancelling the caller `suspend fun` will cancel all running `Fiber`s inside `parTraverse` gracefully. 

### RaceN

`raceN` is similar to `parMapN` except for it only returns the winner of the race, and cancels the loser as soon as the first fiber finishes the race.

#### RacePair/RaceTriple/ForkAndForget

There are a couple of low level constructs available, and they should be used with care!

- racePair/raceTriple like `raceN` races 2 operations, but instead of cancelling the loser it returns the `Fiber` of the loser task

- ForkAndForget launches a task `fa` as a `Fiber` but doesn't connect it's cancellation system

Both operators can safely capture the running `Fiber` and _guarantee_ not leaking using `bracketCase`.
An example, writing `raceN` in terms of `racePair`.

```kotlin:ank
suspend fun <A, B> raceN(ctx: CoroutineContext, fa: suspend () -> A, fb: suspend () -> B): Either<A, B> =
  bracket(
    acquire = { racePair(ctx, fa, fb) },
    use = { it.bimap({ (a, _) -> a }, { (_, b) -> b }) },
    release = {
      it.bimap(
        { (_, fiber) -> fiber.cancel() },
        { (fiber, _) -> fiber.cancel() }
      )
    }
  )
```

## And more…

Arrow Fx Coroutines also provides, for complex/specific use cases, services such as :  
- an immutable queue (`IQueue` datatype),  
- a purely functional, effectful stream processing with the new `Stream`API,  
- The new service `CircuitBreaker`aims to to make our distributed systems more reliable.  
This list is not exhaustive.

## Arrow Fx Coroutines, KotlinX Coroutines & Kotlin Standard Library
 
### Demystify Coroutine 

Kotlin's standard library defines a `Coroutine` as an instance of a suspendable computation.

In other words, a `Coroutine` is a compiled `suspend () -> A` program wired to a `Continuation`.

Which can be created by using [`kotlin.coroutines.intrinsics.createCoroutineUnintercepted`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.intrinsics/create-coroutine-unintercepted.html).

So let's take a quick look at an example.

```kotlin:ank
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted

suspend fun one(): Int = 1

val cont: Continuation<Unit> = ::one
  .createCoroutineUnintercepted(Continuation(EmptyCoroutineContext, ::println))

cont.resume(Unit)
```

As you can see here above we create a `Coroutine` using `createCoroutineUnintercepted` which returns us `Continuation<Unit>`.
Strange, you might've expected a `Coroutine` type but a `Coroutine` is represented by `Continuation<Unit>`.

This `typealias Coroutine = Contination<Unit>` will start running every time you call `resume(Unit)`, which allows you to run the suspend program N times.

### Arrow Fx Coroutines & KotlinX Coroutines

Both Arrow Fx Coroutines & KotlinX Coroutines independently offer an implementation for Kotlin's coroutine system.

As explained in the document above, Arrow Fx Coroutines offers a battery-included functional IO with cancellation support.
Where KotlinX Coroutines offers an implementation that offers light-weight futures with cancellation support.
