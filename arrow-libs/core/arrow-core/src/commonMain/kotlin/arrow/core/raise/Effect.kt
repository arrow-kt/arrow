@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class)
package arrow.core.raise

import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * [Effect] represents a function of `suspend Raise<R>.() -> A`, that short-circuit with a value of `R` or `Throwable`, or completes with a value of `A`.
 *
 * So [Effect] is defined by `suspend fun <B> fold(recover: suspend (Throwable) -> B, resolve: suspend (R) -> B, transform: suspend (A) -> B): B`,
 * to map all values of `R`, `Throwable` and `A` to a value of `B`.
 *
 * <!--- TOC -->

      * [Writing a program with Effect<R, A>](#writing-a-program-with-effect<r-a>)
      * [Handling errors](#handling-errors)
        * [recover](#recover)
        * [catch](#catch)
      * [Structured Concurrency](#structured-concurrency)
        * [Arrow Fx Coroutines](#arrow-fx-coroutines)
          * [parZip](#parzip)
          * [parTraverse](#partraverse)
          * [raceN](#racen)
          * [bracketCase / Resource](#bracketcase--resource)
        * [KotlinX](#kotlinx)
          * [withContext](#withcontext)
          * [async](#async)
          * [launch](#launch)
          * [Strange edge cases](#strange-edge-cases)

 * <!--- END -->
 *
 * ## Writing a program with Effect<R, A>
 *
 * Let's write a small program to read a file from disk, and instead of having the program work exception based we want to
 * turn it into a polymorphic type-safe program.
 *
 * We'll start by defining a small function that accepts a [String], and does some simply validation to check that the path
 * is not empty. If the path is empty, we want to program to result in `EmptyPath`. So we're immediately going to see how
 * we can raise an error of any arbitrary type `R` by using the function `raise`. The name `raise` comes raising an intterupt, or
 * changing, especially unexpectedly, away from the computation and finishing the `Continuation` with `R`.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.Effect
 * import arrow.core.raise.effect
 * import arrow.core.raise.ensureNotNull
 * import arrow.core.raise.ensure
 * -->
 * ```kotlin
 * object EmptyPath
 *
 * fun readFile(path: String): Effect<EmptyPath, Unit> = effect {
 *   if (path.isEmpty()) raise(EmptyPath) else Unit
 * }
 * ```
 *
 * Here we see how we can define an `Effect<R, A>` which has `EmptyPath` for the raise type `R`, and `Unit` for the success type `A`.
 *
 * Patterns like validating a [Boolean] is very common, and the [Effect] DSL offers utility functions like [kotlin.require]
 * and [kotlin.requireNotNull]. They're named [ensure] and [ensureNotNull] to avoid conflicts with the `kotlin` namespace.
 * So let's rewrite the function from above to use the DSL instead.
 *
 * ```kotlin
 * fun readFile2(path: String?): Effect<EmptyPath, Unit> = effect {
 *   ensureNotNull(path) { EmptyPath }
 *   ensure(path.isNotEmpty()) { EmptyPath }
 * }
 * ```
 * <!--- KNIT example-raise-01.kt -->
 *
 * Now that we have the path, we can read from the `File` and return it as a domain model `Content`.
 * We also want to take a look at what exceptions reading from a file might occur `FileNotFoundException` & `SecurityError`,
 * so lets make some domain errors for those too. Grouping them as a sealed interface is useful since that way we can resolve *all* errors in a type safe manner.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.Ior
 * import arrow.core.None
 * import arrow.core.raise.Effect
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.core.raise.toEither
 * import arrow.core.raise.toIor
 * import arrow.core.raise.toOption
 * import arrow.core.raise.ensureNotNull
 * import arrow.core.raise.ensure
 * import io.kotest.matchers.collections.shouldNotBeEmpty
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeInstanceOf
 * import java.io.File
 * import java.io.FileNotFoundException
 * -->
 * ```kotlin
 * @JvmInline
 * value class Content(val body: List<String>)
 *
 * sealed interface FileError
 * @JvmInline value class SecurityError(val msg: String?) : FileError
 * @JvmInline value class FileNotFound(val path: String) : FileError
 * object EmptyPath : FileError {
 *   override fun toString() = "EmptyPath"
 * }
 * ```
 *
 * We can finish our function, but we need to refactor the return type from `Unit` to `Content` and the error type from `EmptyPath` to `FileError`.
 *
 * ```kotlin
 * fun readFile(path: String?): Effect<FileError, Content> = effect {
 *   ensureNotNull(path) { EmptyPath }
 *   ensure(path.isNotEmpty()) { EmptyPath }
 *   try {
 *     val lines = File(path).readLines()
 *     Content(lines)
 *   } catch (e: FileNotFoundException) {
 *     raise(FileNotFound(path))
 *   } catch (e: SecurityException) {
 *     raise(SecurityError(e.message))
 *   }
 * }
 * ```
 *
 * The `readFile` function defines a `suspend fun` that will return:
 *
 * - the `Content` of a given `path`
 * - a `FileError`
 * - An unexpected fatal error (`OutOfMemoryException`)
 *
 * Since these are the properties of our `Effect` function, we can turn it into a value.
 *
 * ```kotlin
 * suspend fun main() {
 *    readFile("").toEither() shouldBe Either.Left(EmptyPath)
 *    readFile("gradle.properties").toIor() shouldBe Ior.Left(FileNotFound("gradle.properties"))
 *    readFile("README.MD").toOption { None } shouldBe None
 *
 *    readFile("build.gradle.kts").fold({ _: FileError -> null }, { it })
 *      .shouldBeInstanceOf<Content>()
 *       .body.shouldNotBeEmpty()
 * }
 * ```
 * <!--- KNIT example-raise-02.kt -->
 *
 * The functions above are available out of the box, but it's easy to define your own extension functions in terms
 * of `fold`. Implementing the `toEither()` operator is as simple as:
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.raise.Effect
 * import arrow.core.raise.fold
 * import arrow.core.identity
 * -->
 * ```kotlin
 * suspend fun <R, A> Effect<R, A>.toEither(): Either<R, A> =
 *   fold({ Either.Left(it) }) { Either.Right(it) }
 *
 * suspend fun <A> Effect<None, A>.toOption(): Option<A> =
 *   fold(::identity) { Some(it) }
 * ```
 * <!--- KNIT example-raise-03.kt -->
 *
 * Adding your own syntax to `Raise<R>` is not advised, yet, but will be easy once "Multiple Receivers" become available.
 *
 * ```
 * context(Raise<R>)
 * suspend fun <R, A> Either<R, A>.bind(): A =
 *   when (this) {
 *     is Either.Left -> raise(value)
 *     is Either.Right -> value
 *   }
 *
 * context(Raise<None>)
 * fun <A> Option<A>.bind(): A =
 *   fold({ raise(it) }, ::identity)
 * ```
 *
 * ## Handling errors
 *
 * An Effect<R, A> has 2 error channels: `Throwable` and `R`
 * There are two separate handlers to transform either of the error channels.
 *
 * - `recover` to handle, and transform any error of type `R`.
 * - `catch` to handle, and transform and error of type `Throwable`.
 *
 * ### recover
 *
 * `recover` handles the error of type `R`,
 * by providing a new value of type `A`, raising a different error of type `E`, or throwing an exception.
 *
 * Let's take a look at some examples:
 *
 * We define a `val failed` of type `Effect<String, Int>`, that represents a failed effect with value "failed".
 *
 * <!--- INCLUDE
 * import arrow.core.raise.Effect
 * import arrow.core.raise.effect
 * import arrow.core.raise.recover
 * import arrow.core.raise.catch
 * -->
 * ```kotlin
 * val failed: Effect<String, Int> =
 *   effect { raise("failed") }
 * ```
 *
 * We can `recover` the failure, and resolve it by providing a default value of `-1` or the length of the `error: String`.
 *
 * ```kotlin
 * val default: Effect<Nothing, Int> =
 *   failed.recover { -1 }
 *
 * val resolved: Effect<Nothing, Int> =
 *   failed.recover { it.length }
 * ```
 *
 * As you can see the resulting `error` is now of type `Nothing`, since we did not raise any new errors.
 * So our `Effect` knows that no short-circuiting will occur during execution. Awesome!
 * But it can also infer to any other error type that you might want instead, because it's never going to occur.
 * So as you see below, we can even assign our `Effect<Nothing, A>` to `Effect<E, A>`, where `E` can be any type.
 *
 * ```kotlin
 * val default2: Effect<Double, Int> = default
 * val resolved2: Effect<Unit, Int> = resolved
 * ```
 *
 * `recover` also allows us to _change_ the error type when we resolve the error of type `R`.
 * Below we handle our error of `String` and turn it into `List<Char>` using `reversed().toList()`.
 * This is a powerful operation, since it allows us to transform our error types across boundaries or layers.
 *
 * ```kotlin
 * val newError: Effect<List<Char>, Int> =
 *   failed.recover { str ->
 *     raise(str.reversed().toList())
 *   }
 * ```
 *
 * Finally, since `recover` supports `suspend` we can safely call other `suspend` code and throw `Throwable` into the `suspend` system.
 * This is typically undesired, since you should prefer lifting `Throwable` into typed values of `R` to make them compile-time tracked.
 *
 * ```kotlin
 * val newException: Effect<Nothing, Int> =
 *   failed.recover { str -> throw RuntimeException(str) }
 * ```
 *
 * ### catch
 *
 * `catch` gives us the same powers as `recover`, but instead of resolving `R` we're recovering from any unexpected `Throwable`.
 * Unexpected, because the expectation is that all `Throwable` get turned into `R` unless it's a fatal/unexpected.
 * This operator is useful when you need to work/wrap foreign code, especially Java SDKs or any code that is heavily based on exceptions.
 *
 * Below we've defined a `foreign` value that represents wrapping a foreign API which might throw `RuntimeException`.
 *
 * ```kotlin
 * val foreign = effect<String, Int> {
 *   throw RuntimeException("BOOM!")
 * }
 * ```
 *
 * We can `catch` to run the effect recovering from any exception,
 * and recover it by providing a default value of `-1` or the length of the [Throwable.message].
 *
 * ```kotlin
 * val default3: Effect<String, Int> =
 *   foreign.catch { -1 }
 *
 * val resolved3: Effect<String, Int> =
 *   foreign.catch { it.message?.length ?: -1 }
 * ```
 *
 * A big difference with `recover` is that `catch` **cannot** change the error type of `R` because it doesn't resolve it, so it stays unchanged.
 * You can however compose `recover`, and `v` to resolve the error type **and** recover the exception.
 *
 * ```kotlin
 * val default4: Effect<Nothing, Int> =
 *   foreign
 *     .recover<String, Nothing, Int> { -1 }
 *     .catch { -2 }
 * ```
 *
 * `catch` however offers an overload that can _refine the exception_.
 * Let's say you're wrapping some database interactions that might throw `java.sql.SqlException`, or `org.postgresql.util.PSQLException`,
 * then you might only be interested in those exceptions and not `Throwable`. `catch` allows you to install multiple handlers for specific exceptions.
 * If the desired exception is not matched, then it stays in the `suspend` exception channel and will be thrown or recovered at a later point.
 *
 * ```kotlin
 * val default5: Effect<String, Int> =
 *   foreign
 *     .catch { ex: RuntimeException -> -1 }
 *     .catch { ex: java.sql.SQLException -> -2 }
 * ```
 *
 * Finally, since `catch` also supports `suspend` we can safely call other `suspend` code and throw `Throwable` into the `suspend` system.
 * This can be useful if refinement of exceptions is not sufficient, for example in the case of `org.postgresql.util.PSQLException` you might want to
 * check the `SQLState` to check for a `foreign key violation` and rethrow the exception if not matched.
 *
 * ```kotlin
 * suspend fun java.sql.SQLException.isForeignKeyViolation(): Boolean = true
 *
 * val rethrown: Effect<String, Int> =
 *   failed.catch { ex: java.sql.SQLException ->
 *     if(ex.isForeignKeyViolation()) raise("foreign key violation")
 *     else throw ex
 *   }
 * ```
 *
 * <!--- KNIT example-raise-04.kt -->
 *
 * Note:
 *  Handling errors can also be done with `try/catch` but this is **not recommended**, it uses `CancellationException` which is used to cancel `Coroutine`s and is advised not to capture in Kotlin.
 *  The `CancellationException` from `Effect` is `RaiseCancellationException`, this a public type, thus can be distinguished from any other `CancellationException` if necessary.
 *
 * ## Structured Concurrency
 *
 * `Effect<R, A>` relies on `kotlin.cancellation.CancellationException` to `raise` error values of type `R` inside the `Continuation` since it effectively cancels/short-circuits it.
 * For this reason `raise` adheres to the same rules as [`Structured Concurrency`](https://kotlinlang.org/docs/coroutines-basics.html#structured-concurrency)
 *
 * Let's overview below how `raise` behaves with the different concurrency builders from Arrow Fx & KotlinX Coroutines.
 * In the examples below we're going to be using a utility to show how _sibling tasks_ get cancelled.
 * The utility function show below called `awaitExitCase` will `never` finish suspending, and completes a `Deferred` with the `ExitCase`.
 * `ExitCase` is a sealed class that can be a value of `Failure(Throwable)`, `Cancelled(CancellationException)`, or `Completed`.
 * Since `awaitExitCase` suspends forever, it can only result in `Cancelled(CancellationException)`.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.guaranteeCase
 * import arrow.fx.coroutines.parZip
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeTypeOf
 * import kotlinx.coroutines.CompletableDeferred
 * import kotlinx.coroutines.awaitCancellation
 * -->
 * ```kotlin
 * suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
 *   guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }
 *
 * ```
 *
 * ### Arrow Fx Coroutines
 * All operators in Arrow Fx Coroutines run in place, so they have no way of leaking `raise`.
 * It's there always safe to compose `effect` with any Arrow Fx combinator. Let's see some small examples below.
 *
 * #### parZip
 *
 * ```kotlin
 *  suspend fun main() {
 *    val error = "Error"
 *    val exit = CompletableDeferred<ExitCase>()
 *   effect<String, Int> {
 *     parZip({ awaitExitCase<Int>(exit) }, { raise(error) }) { a: Int, b: Int -> a + b }
 *   }.fold({ it shouldBe error }, { fail("Int can never be the result") })
 *   exit.await().shouldBeTypeOf<ExitCase>()
 * }
 * ```
 * <!--- KNIT example-raise-05.kt -->
 *
 * #### parTraverse
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.guaranteeCase
 * import arrow.fx.coroutines.parTraverse
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeTypeOf
 * import kotlinx.coroutines.CompletableDeferred
 * import kotlinx.coroutines.awaitCancellation
 *
 * suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
 *  guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }
 *
 * suspend fun <A> CompletableDeferred<A>.getOrNull(): A? =
 *  if (isCompleted) await() else null
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val error = "Error"
 *   val exits = (0..3).map { CompletableDeferred<ExitCase>() }
 *   effect<String, List<Unit>> {
 *     (0..4).parTraverse { index ->
 *       if (index == 4) raise(error)
 *       else awaitExitCase(exits[index])
 *     }
 *   }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
 *   // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
 *   exits.forEach { exit -> exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>() }
 * }
 * ```
 * <!--- KNIT example-raise-06.kt -->
 *
 * `parTraverse` will launch 5 tasks, for every element in `1..5`.
 * The last task to get scheduled will `raise` with "error", and it will cancel the other launched tasks before returning.
 *
 * #### raceN
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.core.merge
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.guaranteeCase
 * import arrow.fx.coroutines.raceN
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeTypeOf
 * import kotlinx.coroutines.CompletableDeferred
 * import kotlinx.coroutines.awaitCancellation
 *
 * suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
 *   guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }
 *
 * suspend fun <A> CompletableDeferred<A>.getOrNull(): A? =
 *   if (isCompleted) await() else null
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val error = "Error"
 *   val exit = CompletableDeferred<ExitCase>()
 *   effect<String, Int> {
 *     raceN({ awaitExitCase<Int>(exit) }) { raise(error) }
 *       .merge() // Flatten Either<Int, Int> result from race into Int
 *   }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
 *   // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
 *   exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>()
 * }
 * ```
 * <!--- KNIT example-raise-07.kt -->
 *
 * `raceN` races `n` suspend functions in parallel, and cancels all participating functions when a winner is found.
 * We can consider the function that `raise`s the winner of the race, except with a raised value instead of a successful one.
 * So when a function in the race `raise`s, and thus short-circuiting the race, it will cancel all the participating functions.
 *
 * #### bracketCase / Resource
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.bracketCase
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeTypeOf
 * import kotlinx.coroutines.CompletableDeferred
 * import java.io.BufferedReader
 * import java.io.File
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val error = "Error"
 *   val exit = CompletableDeferred<ExitCase>()
 *   effect<String, Int> {
 *     bracketCase(
 *       acquire = { File("build.gradle.kts").bufferedReader() },
 *       use = { reader: BufferedReader -> raise(error) },
 *       release = { reader, exitCase ->
 *         reader.close()
 *         exit.complete(exitCase)
 *       }
 *     )
 *   }.fold({ it shouldBe error }, { fail("Int can never be the result") })
 *   exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
 * }
 * ```
 * <!--- KNIT example-raise-08.kt -->
 *
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.ResourceScope
 * import arrow.fx.coroutines.autoCloseable
 * import arrow.fx.coroutines.resourceScope
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeTypeOf
 * import kotlinx.coroutines.CompletableDeferred
 * import java.io.BufferedReader
 * import java.io.File
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val error = "Error"
 *   val exit = CompletableDeferred<ExitCase>()
 *
 *   suspend fun ResourceScope.bufferedReader(path: String): BufferedReader =
 *     autoCloseable { File(path).bufferedReader() }.also {
 *       onRelease { exitCase -> exit.complete(exitCase) }
 *     }
 *
 *   resourceScope {
 *     effect<String, Int> {
 *       val reader = bufferedReader("build.gradle.kts")
 *       raise(error)
 *       reader.lineSequence().count()
 *     }.fold({ it shouldBe error }, { fail("Int can never be the result") })
 *   }
 *   exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
 * }
 * ```
 * <!--- KNIT example-raise-09.kt -->
 *
 * ### KotlinX
 * #### withContext
 * It's always safe to call `raise` from `withContext` since it runs in place, so it has no way of leaking `raise`.
 * When `raise` is called from within `withContext` it will cancel all `Job`s running inside the `CoroutineScope` of `withContext`.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.Effect
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import arrow.core.raise.ensureNotNull
 * import arrow.core.raise.ensure
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.guaranteeCase
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import io.kotest.matchers.types.shouldBeInstanceOf
 * import kotlinx.coroutines.CompletableDeferred
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.awaitCancellation
 * import kotlinx.coroutines.launch
 * import kotlinx.coroutines.withContext
 * import java.io.File
 * import java.io.FileNotFoundException
 *
 * @JvmInline
 * value class Content(val body: List<String>)
 *
 * sealed interface FileError
 * @JvmInline value class SecurityError(val msg: String?) : FileError
 * @JvmInline value class FileNotFound(val path: String) : FileError
 * object EmptyPath : FileError {
 *   override fun toString() = "EmptyPath"
 * }
 *
 * fun readFile(path: String?): Effect<FileError, Content> = effect {
 *   ensureNotNull(path) { EmptyPath }
 *   ensure(path.isNotEmpty()) { EmptyPath }
 *   try {
 *     val lines = File(path).readLines()
 *     Content(lines)
 *   } catch (e: FileNotFoundException) {
 *     raise(FileNotFound(path))
 *   } catch (e: SecurityException) {
 *     raise(SecurityError(e.message))
 *   }
 * }
 *
 * suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
 *   guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val exit = CompletableDeferred<ExitCase>()
 *   effect<FileError, Int> {
 *     withContext(Dispatchers.IO) {
 *       val job = launch { awaitExitCase(exit) }
 *       val content = readFile("failure").bind()
 *       job.join()
 *       content.body.size
 *     }
 *   }.fold({ e -> e shouldBe FileNotFound("failure") }, { fail("Int can never be the result") })
 *   exit.await().shouldBeInstanceOf<ExitCase>()
 * }
 * ```
 * <!--- KNIT example-raise-10.kt -->
 *
 * #### async
 *
 * When calling `raise` from `async` you should **always** call `await`, otherwise `raise` can leak out of its scope.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.collections.shouldBeIn
 * import kotlinx.coroutines.async
 * import kotlinx.coroutines.coroutineScope
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val errorA = "ErrorA"
 *   val errorB = "ErrorB"
 *   coroutineScope {
 *     effect<String, Int> {
 *       val fa = async<Int> { raise(errorA) }
 *       val fb = async<Int> { raise(errorB) }
 *       fa.await() + fb.await()
 *     }.fold({ error -> error shouldBeIn listOf(errorA, errorB) }, { fail("Int can never be the result") })
 *   }
 * }
 * ```
 * <!--- KNIT example-raise-11.kt -->
 *
 * #### launch
 *
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import kotlinx.coroutines.coroutineScope
 * import kotlinx.coroutines.launch
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val errorA = "ErrorA"
 *   val errorB = "ErrorB"
 *   val int = 45
 *   effect<String, Int> {
 *     coroutineScope<Int> {
 *       launch { raise(errorA) }
 *       launch { raise(errorB) }
 *       int
 *     }
 *   }.fold({ fail("Raise can never finish") }, { it shouldBe int })
 * }
 * ```
 * <!--- KNIT example-raise-12.kt -->
 *
 * #### Strange edge cases
 *
 * **NOTE**
 * Capturing `raise` into a lambda, and leaking it outside of `Effect` to be invoked outside will yield unexpected results.
 * Below we capture `raise` from inside the DSL, and then invoke it outside its context `Raise<String>`.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.effect
 * import arrow.core.raise.fold
 * import kotlinx.coroutines.Deferred
 * import kotlinx.coroutines.async
 * import kotlinx.coroutines.coroutineScope
 *
 * suspend fun main() {
 * -->
 * <!--- SUFFIX
 * }
 * -->
 * ```kotlin
 *   effect<String, suspend () -> Unit> {
 *     suspend { raise("error") }
 *   }.fold({ }, { leakedRaise -> leakedRaise.invoke() })
 * ```
 *
 * The same violation is possible in all DSLs in Kotlin, including Structured Concurrency.
 *
 * ```kotlin
 *   val leakedAsync = coroutineScope<suspend () -> Deferred<Unit>> {
 *     suspend {
 *       async {
 *         println("I am never going to run, until I get called invoked from outside")
 *       }
 *     }
 *   }
 *
 *   leakedAsync.invoke().await()
 * ```
 * <!--- KNIT example-raise-13.kt -->
 */
public typealias Effect<Error, A> = suspend Raise<Error>.() -> A

public inline fun <Error, A> effect(@BuilderInference noinline block: suspend Raise<Error>.() -> A): Effect<Error, A> = block

/** The same behavior and API as [Effect] except without requiring _suspend_. */
public typealias EagerEffect<Error, A> = Raise<Error>.() -> A

public inline fun <Error, A> eagerEffect(@BuilderInference noinline block: Raise<Error>.() -> A): EagerEffect<Error, A> = block

public suspend fun <A> Effect<A, A>.merge(): A = merge { invoke() }
public fun <A> EagerEffect<A, A>.merge(): A = merge { invoke() }

public suspend fun <A> Effect<Nothing, A>.get(): A = merge()

public fun <A> EagerEffect<Nothing, A>.get(): A = merge()
