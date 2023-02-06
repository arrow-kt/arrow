package arrow.core.continuations

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonFatal
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * [Effect] represents a function of `suspend () -> A`, that short-circuit with a value of [R] (and [Throwable]),
 * or completes with a value of [A].
 *
 * So [Effect] is defined by `suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B`,
 * to map both values of [R] and [A] to a value of `B`.
 *
 * <!--- TOC -->

      * [Writing a program with Effect<R, A>](#writing-a-program-with-effect<r-a>)
      * [Handling errors](#handling-errors)
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
          * [Leaking `shift`](#leaking-shift)

 * <!--- END -->
 *
 *
 * ## Writing a program with Effect<R, A>
 *
 * Let's write a small program to read a file from disk, and instead of having the program work exception based we want to
 * turn it into a polymorphic type-safe program.
 *
 * We'll start by defining a small function that accepts a [String], and does some simply validation to check that the path
 * is not empty. If the path is empty, we want to program to result in `EmptyPath`. So we're immediately going to see how
 * we can raise an error of any arbitrary type [R] by using the function `shift`. The name `shift` comes shifting (or
 * changing, especially unexpectedly), away from the computation and finishing the `Continuation` with [R].
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.Effect
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.ensureNotNull
 * -->
 * ```kotlin
 * object EmptyPath
 *
 * fun readFile(path: String): Effect<EmptyPath, Unit> = effect {
 *   if (path.isEmpty()) shift(EmptyPath) else Unit
 * }
 * ```
 *
 * Here we see how we can define an `Effect<R, A>` which has `EmptyPath` for the shift type [R], and `Unit` for the success
 * type [A].
 *
 * Patterns like validating a [Boolean] is very common, and the [Effect] DSL offers utility functions like [kotlin.require]
 * and [kotlin.requireNotNull]. They're named [EffectScope.ensure] and [ensureNotNull] to avoid conflicts with the `kotlin` namespace.
 * So let's rewrite the function from above to use the DSL instead.
 *
 * ```kotlin
 * fun readFile2(path: String?): Effect<EmptyPath, Unit> = effect {
 *   ensureNotNull(path) { EmptyPath }
 *   ensure(path.isNotEmpty()) { EmptyPath }
 * }
 * ```
 * <!--- KNIT example-effect-guide-01.kt -->
 *
 * Now that we have the path, we can read from the `File` and return it as a domain model `Content`.
 * We also want to take a look at what exceptions reading from a file might occur `FileNotFoundException` & `SecurityError`,
 * so lets make some domain errors for those too. Grouping them as a sealed interface is useful since that way we can resolve *all* errors in a type safe manner.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.Ior
 * import arrow.core.None
 * import arrow.core.Validated
 * import arrow.core.continuations.Effect
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.ensureNotNull
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
 *     shift(FileNotFound(path))
 *   } catch (e: SecurityException) {
 *     shift(SecurityError(e.message))
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
 *    readFile("knit.properties").toValidated() shouldBe  Validated.Invalid(FileNotFound("knit.properties"))
 *    readFile("gradle.properties").toIor() shouldBe Ior.Left(FileNotFound("gradle.properties"))
 *    readFile("README.MD").toOption { None } shouldBe None
 *
 *    readFile("build.gradle.kts").fold({ _: FileError -> null }, { it })
 *      .shouldBeInstanceOf<Content>()
 *       .body.shouldNotBeEmpty()
 * }
 * ```
 * <!--- KNIT example-effect-guide-02.kt -->
 *
 * The functions above are available out of the box, but it's easy to define your own extension functions in terms
 * of `fold`. Implementing the `toEither()` operator is as simple as:
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.continuations.Effect
 * import arrow.core.identity
 * -->
 * ```kotlin
 * suspend fun <R, A> Effect<R, A>.toEither(): Either<R, A> =
 *   fold({ Either.Left(it) }) { Either.Right(it) }
 *
 * suspend fun <A> Effect<None, A>.toOption(): Option<A> =
 *   fold(::identity) { Some(it) }
 * ```
 * <!--- KNIT example-effect-guide-03.kt -->
 *
 * Adding your own syntax to `EffectScope<R>` is not advised, yet, but will be easy once "Multiple Receivers" become available.
 *
 * ```
 * context(EffectScope<R>)
 * suspend fun <R, A> Either<R, A>.bind(): A =
 *   when (this) {
 *     is Either.Left -> shift(value)
 *     is Either.Right -> value
 *   }
 *
 * context(EffectScope<None>)
 * fun <A> Option<A>.bind(): A =
 *   fold({ shift(it) }, ::identity)
 * ```
 *
 * ## Handling errors
 *
 * Handling errors of type [R] is the same as handling errors for any other data type in Arrow.
 * `Effect<R, A>` offers `handleError`, `handleErrorWith`, `redeem`, `redeemWith` and `attempt`.
 *
 * As you can see in the examples below it is possible to resolve errors of [R] or `Throwable` in `Effect<R, A>` in a generic manner.
 * There is no need to run `Effect<R, A>` into `Either<R, A>` before you can access [R],
 * you can simply call the same functions on `Effect<R, A>` as you would on `Either<R, A>` directly.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.continuations.Effect
 * import arrow.core.continuations.effect
 * import arrow.core.identity
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * val failed: Effect<String, Int> =
 *   effect { shift("failed") }
 *
 * val resolved: Effect<Nothing, Int> =
 *   failed.handleError { it.length }
 *
 * val newError: Effect<List<Char>, Int> =
 *   failed.handleErrorWith { str ->
 *     effect { shift(str.reversed().toList()) }
 *   }
 *
 * val redeemed: Effect<Nothing, Int> =
 *   failed.redeem({ str -> str.length }, ::identity)
 *
 * val captured: Effect<String, Result<Int>> =
 *   effect<String, Int> { 1 }.attempt()
 *
 * suspend fun main() {
 *   failed.toEither() shouldBe Either.Left("failed")
 *   resolved.toEither() shouldBe Either.Right(6)
 *   newError.toEither() shouldBe Either.Left(listOf('d', 'e', 'l', 'i', 'a', 'f'))
 *   redeemed.toEither() shouldBe Either.Right(6)
 *   captured.toEither() shouldBe Either.Right(Result.success(1))
 * }
 * ```
 * <!--- KNIT example-effect-guide-04.kt -->
 *
 * Note:
 *  Handling errors can also be done with `try/catch` but this is **not recommended**, it uses `CancellationException` which is used to cancel `Coroutine`s and is advised not to capture in Kotlin.
 *  The `CancellationException` from `Effect` is `ShiftCancellationException`, this a public type, thus can be distinguished from any other `CancellationException` if necessary.
 *
 * ## Structured Concurrency
 *
 * `Effect<R, A>` relies on `kotlin.cancellation.CancellationException` to `shift` error values of type [R] inside the `Continuation` since it effectively cancels/short-circuits it.
 * For this reason `shift` adheres to the same rules as [`Structured Concurrency`](https://kotlinlang.org/docs/coroutines-basics.html#structured-concurrency)
 *
 * Let's overview below how `shift` behaves with the different concurrency builders from Arrow Fx & KotlinX Coroutines.
 * In the examples below we're going to be using a utility to show how _sibling tasks_ get cancelled.
 * The utility function show below called `awaitExitCase` will `never` finish suspending, and completes a `Deferred` with the `ExitCase`.
 * `ExitCase` is a sealed class that can be a value of `Failure(Throwable)`, `Cancelled(CancellationException)`, or `Completed`.
 * Since `awaitExitCase` suspends forever, it can only result in `Cancelled(CancellationException)`.
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
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
 * All operators in Arrow Fx Coroutines run in place, so they have no way of leaking `shift`.
 * It's there always safe to compose `effect` with any Arrow Fx combinator. Let's see some small examples below.
 *
 * #### parZip
 *
 * ```kotlin
 *  suspend fun main() {
 *    val error = "Error"
 *    val exit = CompletableDeferred<ExitCase>()
 *   effect<String, Int> {
 *     parZip({ awaitExitCase<Int>(exit) }, { shift<Int>(error) }) { a, b -> a + b }
 *   }.fold({ it shouldBe error }, { fail("Int can never be the result") })
 *   exit.await().shouldBeTypeOf<ExitCase>()
 * }
 * ```
 * <!--- KNIT example-effect-guide-05.kt -->
 *
 * #### parTraverse
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
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
 *       if (index == 4) shift(error)
 *       else awaitExitCase(exits[index])
 *     }
 *   }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
 *   // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
 *   exits.forEach { exit -> exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>() }
 * }
 * ```
 * <!--- KNIT example-effect-guide-06.kt -->
 *
 * `parTraverse` will launch 5 tasks, for every element in `1..5`.
 * The last task to get scheduled will `shift` with "error", and it will cancel the other launched tasks before returning.
 *
 * #### raceN
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
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
 *     raceN({ awaitExitCase<Int>(exit) }) { shift<Int>(error) }
 *       .merge() // Flatten Either<Int, Int> result from race into Int
 *   }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
 *   // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
 *   exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>()
 * }
 * ```
 * <!--- KNIT example-effect-guide-07.kt -->
 *
 * `raceN` races `n` suspend functions in parallel, and cancels all participating functions when a winner is found.
 * We can consider the function that `shift`s the winner of the race, except with a shifted value instead of a successful one.
 * So when a function in the race `shift`s, and thus short-circuiting the race, it will cancel all the participating functions.
 *
 * #### bracketCase / Resource
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
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
 *       use = { reader: BufferedReader -> shift(error) },
 *       release = { reader, exitCase ->
 *         reader.close()
 *         exit.complete(exitCase)
 *       }
 *     )
 *   }.fold({ it shouldBe error }, { fail("Int can never be the result") })
 *   exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
 * }
 * ```
 * <!--- KNIT example-effect-guide-08.kt -->
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
 * import arrow.fx.coroutines.ExitCase
 * import arrow.fx.coroutines.Resource
 * import arrow.fx.coroutines.fromAutoCloseable
 * import arrow.fx.coroutines.releaseCase
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
 *   fun bufferedReader(path: String): Resource<BufferedReader> =
 *     Resource.fromAutoCloseable { File(path).bufferedReader() }
 *       .releaseCase { _, exitCase -> exit.complete(exitCase) }
 *
 *   effect<String, Int> {
 *     val lineCount = bufferedReader("build.gradle.kts")
 *       .use { reader -> shift<Int>(error) }
 *     lineCount
 *   }.fold({ it shouldBe error }, { fail("Int can never be the result") })
 *   exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
 * }
 * ```
 * <!--- KNIT example-effect-guide-09.kt -->
 *
 * ### KotlinX
 * #### withContext
 * It's always safe to call `shift` from `withContext` since it runs _in place_, so it has no way of leaking `shift`.
 * When `shift` is called from within `withContext` it will cancel all `Job`s running inside the `CoroutineScope` of `withContext`.
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.Effect
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.ensureNotNull
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
 *     shift(FileNotFound(path))
 *   } catch (e: SecurityException) {
 *     shift(SecurityError(e.message))
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
 * <!--- KNIT example-effect-guide-10.kt -->
 *
 * #### async
 *
 * When calling `shift` from `async` you should **always** call `await`, otherwise `shift` can leak out of its scope.
 * So it's safe to call `shift` from `async` as long as you **always** call `await` on the `Deferred` returned by `async`,
 * but we advise using Arrow Fx `parZip`, `raceN`, `parTraverse`, etc instead.
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
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
 *       val fa = async<Int> { shift(errorA) }
 *       val fb = async<Int> { shift(errorB) }
 *       fa.await() + fb.await()
 *     }.fold(
 *       { error ->
 *         println(error)
 *         error shouldBeIn listOf(errorA, errorB)
 *       },
 *       { fail("Int can never be the result") }
 *     )
 *   }
 * }
 * ```
 * <!--- KNIT example-effect-guide-11.kt -->
 * ```text
 * ErrorA
 * ```
 *
 * The example here will always print `ErrorA`, but never `ErrorB`. This is because `fa` is awaited first, and when it `shifts` it will cancel `fb`.
 * If instead we used `awaitAll`, then it would print `ErrorA` or `ErrorB` due to both `fa` and `fb` being awaited in parallel.
 *
 * #### launch
 *
 * It's **not allowed** to call `shift` from within `launch`. This is because `launch` creates a separate unrelated child Job/Continuation.
 * Any calls to `shift` inside of `launch` will be ignored by `effect` and result in an exception being thrown inside `launch`.
 * Because KotlinX Coroutines ignores `CancellationException`, and thus swallows the `shift` call.
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 * import kotlinx.coroutines.coroutineScope
 * import kotlinx.coroutines.launch
 * -->
 * ```kotlin
 * suspend fun main() {
 *   val errorA = "ErrorA"
 *   val errorB = "ErrorB"
 *   effect<String, Int> {
 *     coroutineScope<Int> {
 *       launch { shift(errorA) }
 *       launch { shift(errorB) }
 *       45
 *     }
 *   }.fold({ fail("Shift can never finish") }, ::println)
 * }
 * ```
 * <!--- KNIT example-effect-guide-12.kt -->
 * ```text
 * 45
 * ```
 *
 * As you can see from the output, the `effect` block is still executed, but the `shift` calls inside `launch` are ignored.
 *
 * #### Leaking `shift`
 *
 * **IMPORTANT:** Capturing `shift` and leaking it outside of `effect { }` and invoking it outside its scope will yield unexpected results.
 *
 * Below an example of the capturing of `shift` inside a `suspend lambda`, and then invoking it outside its `effect { }` scope.
 *
 * <!--- INCLUDE
 * import arrow.core.continuations.effect
 *
 * suspend fun main() {
 * -->
 * <!--- SUFFIX
 * }
 * -->
 * ```kotlin
 *   effect<String, suspend () -> Unit> {
 *     suspend { shift("error") }
 *   }.fold({ }, { leakedShift -> leakedShift.invoke() })
 * ```
 * <!--- KNIT example-effect-guide-13.kt -->
 *
 * When we invoke `leakedShift` outside of `effect { }` a special `ShiftLeakedException` is thrown to improve the debugging experience.
 * The message clearly states that `shift` was leaked outside its scope, and the stacktrace will point to the exact location where `shift` was captured.
 * In this case in line `9` of `example-effect-guide-13.kt`, which is stated in the second line of the stacktrace: `invokeSuspend(example-effect-guide-13.kt:9)`.
 *
 * ```text
 * Exception in thread "main" arrow.core.continuations.ShiftLeakedException:
 * shift or bind was called outside of its DSL scope, and the DSL Scoped operator was leaked
 * This is kind of usage is incorrect, make sure all calls to shift or bind occur within the lifecycle of effect { }, either { } or similar builders.
 *
 * See: Effect KDoc for additional information.
 * 	at arrow.core.continuations.FoldContinuation.shift(Effect.kt:770)
 * 	at arrow.core.examples.exampleEffectGuide13.Example_effect_guide_13Kt$main$2$1.invokeSuspend(example-effect-guide-13.kt:9)
 * 	at arrow.core.examples.exampleEffectGuide13.Example_effect_guide_13Kt$main$2$1.invoke(example-effect-guide-13.kt)
 * ```
 *
 * An example with KotlinX Coroutines launch. Which can _concurrently_ leak `shift` outside of its scope.
 * In this case by _delaying_ the invocation of `shift` by `3.seconds`,
 * we can see that the `ShiftLeakedException` is again thrown when `shift` is invoked.
 *
 * <!--- INCLUDE
 * import kotlinx.coroutines.launch
 * import kotlinx.coroutines.delay
 * import kotlinx.coroutines.coroutineScope
 * import kotlinx.coroutines.runBlocking
 * import arrow.core.continuations.effect
 * import kotlin.time.Duration.Companion.seconds
 *
 * fun main(): Unit = runBlocking {
 * -->
 * <!--- SUFFIX
 * }
 * -->
 * ```kotlin
 *  effect<String, Int> {
 *    launch {
 *      delay(3.seconds)
 *      shift("error")
 *    }
 *    1
 *  }.fold(::println, ::println)
 * ```
 * ```text
 * 1
 * Exception in thread "main" arrow.core.continuations.ShiftLeakedException:
 * shift or bind was called outside of its DSL scope, and the DSL Scoped operator was leaked
 * This is kind of usage is incorrect, make sure all calls to shift or bind occur within the lifecycle of effect { }, either { } or similar builders.
 *
 * See: Effect KDoc for additional information.
 * 	at arrow.core.continuations.FoldContinuation.shift(Effect.kt:780)
 * 	at arrow.core.examples.exampleEffectGuide14.Example_effect_guide_14Kt$main$1$1$1$1.invokeSuspend(example-effect-guide-14.kt:17)
 * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33) <13 internal lines>
 * 	at arrow.core.examples.exampleEffectGuide14.Example_effect_guide_14Kt.main(example-effect-guide-14.kt:11)
 * 	at arrow.core.examples.exampleEffectGuide14.Example_effect_guide_14Kt.main(example-effect-guide-14.kt)
 * ```
 * <!--- KNIT example-effect-guide-14.kt -->
 */
@Deprecated(
  "Use the arrow.core.raise.Effect type instead, which is more general and can be used to  and can be used to raise typed errors or _logical failures_\n" +
    "The Raise<R> type is source compatible, a simple find & replace of arrow.core.continuations.* to arrow.core.raise.* will do the trick."
)
public interface Effect<out R, out A> {
  /**
   * Runs the suspending computation by creating a [Continuation], and running the `fold` function
   * over the computation.
   *
   * When the [Effect] has shifted with [R] it will [recover] the shifted value to [B], and when it
   * ran the computation to completion it will [transform] the value [A] to [B].
   *
   * ```kotlin
   * import arrow.core.continuations.effect
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   val shift = effect<String, Int> {
   *     shift("Hello, World!")
   *   }.fold({ str: String -> str }, { int -> int.toString() })
   *   shift shouldBe "Hello, World!"
   *
   *   val res = effect<String, Int> {
   *     1000
   *   }.fold({ str: String -> str.length }, { int -> int })
   *   res shouldBe 1000
   * }
   * ```
   * <!--- KNIT example-effect-01.kt -->
   */
  public suspend fun <B> fold(
    recover: suspend (shifted: R) -> B,
    transform: suspend (value: A) -> B,
  ): B

  /**
   * Like [fold] but also allows folding over any unexpected [Throwable] that might have occurred.
   * @see fold
   */
  public suspend fun <B> fold(
    error: suspend (error: Throwable) -> B,
    recover: suspend (shifted: R) -> B,
    transform: suspend (value: A) -> B,
  ): B =
    try {
      fold(recover, transform)
    } catch (e: Throwable) {
      error(e.nonFatalOrThrow())
    }

  /**
   * [fold] the [Effect] into an [Either]. Where the shifted value [R] is mapped to [Either.Left], and
   * result value [A] is mapped to [Either.Right].
   */
  public suspend fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  /**
   * [fold] the [Effect] into an [Ior]. Where the shifted value [R] is mapped to [Ior.Left], and
   * result value [A] is mapped to [Ior.Right].
   */
  public suspend fun toIor(): Ior<R, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

  /**
   * [fold] the [Effect] into an [Validated]. Where the shifted value [R] is mapped to
   * [Validated.Invalid], and result value [A] is mapped to [Validated.Valid].
   */
  public suspend fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  /**
   * [fold] the [Effect] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
   * provided function [orElse], and result value [A] is mapped to [Some].
   */
  public suspend fun toOption(orElse: suspend (R) -> Option<@UnsafeVariance A>): Option<A> = fold(orElse, ::Some)

  /**
   * [fold] the [Effect] into an [A?]. Where the shifted value [R] is mapped to
   * [null], and result value [A].
   */
  public suspend fun orNull(): A? = fold({ null }, ::identity)

  /** Runs the [Effect] and captures any [NonFatal] exception into [Result]. */
  public fun attempt(): Effect<R, Result<A>> = effect {
    try {
      Result.success(bind())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

  public fun handleError(recover: suspend (R) -> @UnsafeVariance A): Effect<Nothing, A> = effect {
    fold(recover, ::identity)
  }

  public fun <R2> handleErrorWith(recover: suspend (R) -> Effect<R2, @UnsafeVariance A>): Effect<R2, A> = effect {
    fold({ recover(it).bind() }, ::identity)
  }

  public fun <B> redeem(recover: suspend (R) -> B, transform: suspend (A) -> B): Effect<Nothing, B> =
    effect {
      fold(recover, transform)
    }

  @Deprecated(
    "redeemWith is being removed in the Raise API",
    ReplaceWith(
      "effect { fold(recover, transform).bind() }",
      "arrow.core.raise.effect"
    )
  )
  public fun <R2, B> redeemWith(
    recover: suspend (R) -> Effect<R2, B>,
    transform: suspend (A) -> Effect<R2, B>,
  ): Effect<R2, B> = effect { fold(recover, transform).bind() }
}

/**
 * **AVOID USING THIS TYPE, it's meant for low-level cancellation code** When in need in low-level
 * code, you can use this type to differentiate between a foreign [CancellationException] and the
 * one from [Effect].
 */
public sealed class ShiftCancellationException : CancellationExceptionNoTrace()

public expect open class CancellationExceptionNoTrace() : CancellationException

/**
 * Holds `R` and `suspend (R) -> B`, the exception that wins the race, will get to execute
 * `recover`.
 */
@PublishedApi
internal class Suspend(val token: Token, val shifted: Any?, val recover: suspend (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

/** Class that represents a unique token by hash comparison **/
@PublishedApi
internal open class Token {
  override fun toString(): String = "Token(${hashCode().toString(16)})"
}

/**
 * Continuation that runs the `recover` function, after attempting to calculate [B]. In case we
 * encounter a `shift` after suspension, we will receive [Result.failure] with
 * [ShiftCancellationException]. In that case we still need to run `suspend (R) -> B`, which is what
 * we do inside the body of this `Continuation`, and we complete the [parent] [Continuation] with
 * the result.
 */
@PublishedApi
@Deprecated(
  "This will become private in Arrow 2.0, and is not going to be visible from binary anymore",
  level = DeprecationLevel.WARNING
)
internal class FoldContinuation<R, B>(
  override val context: CoroutineContext,
  private val error: suspend (Throwable) -> B,
  private val parent: Continuation<B>,
) : Continuation<B>, Token(), EffectScope<R> {

  constructor(ignored: Token, context: CoroutineContext, parent: Continuation<B>) : this(context, { throw it }, parent)
  constructor(
    ignored: Token,
    context: CoroutineContext,
    error: suspend (Throwable) -> B,
    parent: Continuation<B>,
  ) : this(context, error, parent)

  lateinit var recover: suspend (R) -> Any?

  private val isActive: AtomicRef<Boolean> = AtomicRef(true)

  internal fun complete(): Boolean = isActive.getAndSet(false)

  // Shift away from this Continuation by intercepting it, and completing it with
  // ShiftCancellationException
  // This is needed because this function will never yield a result,
  // so it needs to be cancelled to properly support coroutine cancellation
  override suspend fun <B> shift(r: R): B =
  // Some interesting consequences of how Continuation Cancellation works in Kotlin.
  // We have to throw CancellationException to signal the Continuation was cancelled, and we
  // shifted away.
  // This however also means that the user can try/catch shift and recover from the
  // CancellationException and thus effectively recovering from the cancellation/shift.
  // This means try/catch is also capable of recovering from monadic errors.
    // See: EffectSpec - try/catch tests
    if (isActive.get()) throw Suspend(this, r, recover as suspend (Any?) -> Any?)
    else throw ShiftLeakedException()

  // In contrast to `createCoroutineUnintercepted this doesn't create a new ContinuationImpl
  private fun (suspend () -> B).startCoroutineUnintercepted() {
    try {
      when (val res = startCoroutineUninterceptedOrReturn(parent)) {
        COROUTINE_SUSPENDED -> Unit
        else -> parent.resume(res as B)
      }
      // We need to wire all immediately throw exceptions to the parent Continuation
    } catch (e: Throwable) {
      parent.resumeWithException(e)
    }
  }

  override fun resumeWith(result: Result<B>) {
    result.fold(parent::resume) { throwable ->
      when {
        throwable is Suspend && this === throwable.token -> {
          complete()
          suspend { throwable.recover(throwable.shifted) as B }.startCoroutineUnintercepted()
        }

        throwable is Suspend -> parent.resumeWith(result)
        else -> {
          complete()
          suspend { error(throwable.nonFatalOrThrow()) }.startCoroutineUnintercepted()
        }
      }
    }
  }
}

/**
 * DSL for constructing Effect<R, A> values
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Validated
 * import arrow.core.continuations.effect
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 *
 * suspend fun main() {
 *   effect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z = Option(3).bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })
 *
 *   effect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z: Int = None.bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
 * }
 * ```
 * <!--- KNIT example-effect-02.kt -->
 */
@Deprecated(
  "Use the arrow.core.raise.effect DSL instead, which is more general and can be used to  and can be used to raise typed errors or _logical failures_\n" +
    "The Raise<R> type is source compatible, a simple find & replace of arrow.core.continuations.* to arrow.core.raise.* will do the trick.",
  ReplaceWith(
    "effect<R, A>(f)",
    "arrow.core.raise.effect"
  )
)
public fun <R, A> effect(f: suspend EffectScope<R>.() -> A): Effect<R, A> = DefaultEffect(f)

private class DefaultEffect<R, A>(val f: suspend EffectScope<R>.() -> A) : Effect<R, A> {

  override suspend fun <B> fold(
    recover: suspend (shifted: R) -> B,
    transform: suspend (value: A) -> B,
  ): B = fold({ throw it }, recover, transform)

  // We create a `Token` for fold Continuation, so we can properly differentiate between nested folds
  override suspend fun <B> fold(
    error: suspend (error: Throwable) -> B,
    recover: suspend (shifted: R) -> B,
    transform: suspend (value: A) -> B,
  ): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val shift = FoldContinuation<R, B>(cont.context, error, cont)
      shift.recover = recover
      try {
        val fold: suspend EffectScope<R>.() -> B = {
          val res = f(this).also { shift.complete() }
          transform(res)
        }
        fold.startCoroutineUninterceptedOrReturn(shift, shift)
      } catch (e: Suspend) {
        if (shift === e.token) {
          shift.complete()
          val f: suspend () -> B = { e.recover(e.shifted) as B }
          f.startCoroutineUninterceptedOrReturn(cont)
        } else throw e
      } catch (e: Throwable) {
        shift.complete()
        val f: suspend () -> B = { error(e.nonFatalOrThrow()) }
        f.startCoroutineUninterceptedOrReturn(cont)
      }
    }
}

public suspend fun <A> Effect<A, A>.merge(): A = fold(::identity, ::identity)

public class ShiftLeakedException : IllegalStateException(
  """
  
  shift or bind was called outside of its DSL scope, and the DSL Scoped operator was leaked
  This is kind of usage is incorrect, make sure all calls to shift or bind occur within the lifecycle of effect { }, either { } or similar builders.
 
  See: Effect KDoc for additional information.
  """.trimIndent()
)
