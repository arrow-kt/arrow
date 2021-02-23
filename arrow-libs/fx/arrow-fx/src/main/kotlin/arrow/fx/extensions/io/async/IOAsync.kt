package arrow.fx.extensions.io.async

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOAsync
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val async_singleton: IOAsync = object : arrow.fx.extensions.IOAsync {}

/**
 *  [async] variant that can suspend side effects in the provided registration function.
 *
 *  The passed in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *  import arrow.fx.*
 *  import arrow.fx.typeclasses.Async
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   fun <F> Async<F>.makeCompleteAndGetPromiseInAsync() =
 *     asyncF<String> { cb: (Either<Throwable, String>) -> Unit ->
 *       Promise.uncancellable<F, String>(this).flatMap { promise ->
 *         promise.complete("Hello World!").flatMap {
 *           promise.get().map { str -> cb(Right(str)) }
 *         }
 *       }
 *     }
 *
 *   val result = IO.async().makeCompleteAndGetPromiseInAsync()
 *  //sampleEnd
 *  println(result)
 *  }
 *  ```
 *
 *  @see async for a simpler, non suspending version.
 */
@JvmName("asyncF")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> asyncF(k: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForIO, Unit>>): IO<A> =
  arrow.fx.IO
    .async()
    .asyncF<A>(k) as arrow.fx.IO<A>

/**
 *  Continue the evaluation on provided [CoroutineContext]
 *
 *  @param ctx [CoroutineContext] to run evaluation on
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   fun <F> Async<F>.runOnDefaultDispatcher(): Kind<F, String> =
 *     just(Unit).continueOn(Dispatchers.Default).flatMap {
 *       later({ Thread.currentThread().name })
 *     }
 *
 *   val result = IO.async().runOnDefaultDispatcher()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("continueOn")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.continueOn(ctx: CoroutineContext): IO<A> = arrow.fx.IO.async().run {
  this@continueOn.continueOn<A>(ctx) as arrow.fx.IO<A>
}

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
 *     later(Dispatchers.Default, { Thread.currentThread().name })
 *
 *   val result = IO.async().invokeOnDefaultDispatcher()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> later(ctx: CoroutineContext, f: Function0<A>): IO<A> = arrow.fx.IO
  .async()
  .later<A>(ctx, f) as arrow.fx.IO<A>

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
 *     defer(Dispatchers.Default, { effect { Thread.currentThread().name } })
 *
 *   val result = IO.async().invokeOnDefaultDispatcher().fix().unsafeRunSync()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> defer(ctx: CoroutineContext, f: Function0<Kind<ForIO, A>>): IO<A> = arrow.fx.IO
  .async()
  .defer<A>(ctx, f) as arrow.fx.IO<A>

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 */
@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> laterOrRaise(ctx: CoroutineContext, f: Function0<Either<Throwable, A>>): IO<A> = arrow.fx.IO
  .async()
  .laterOrRaise<A>(ctx, f) as arrow.fx.IO<A>

/**
 *  Shift evaluation to provided [CoroutineContext].
 *
 *  @receiver [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   IO.async().run {
 *     val result = Dispatchers.Default.shift().map {
 *       Thread.currentThread().name
 *     }
 *
 *     println(result)
 *   }
 *   //sampleEnd
 *  }
 *  ```
 */
@JvmName("shift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun CoroutineContext.shift(): IO<Unit> = arrow.fx.IO.async().run {
  this@shift.shift() as arrow.fx.IO<kotlin.Unit>
}

/**
 *  Task that never finishes evaluating.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val i = IO.async().never<Int>()
 *
 *   println(i)
 *   //sampleEnd
 *  }
 *  ```
 */
@JvmName("never")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> never(): IO<A> = arrow.fx.IO
  .async()
  .never<A>() as arrow.fx.IO<A>

/**
 *  Helper function that provides an easy way to construct a suspend effect
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.*
 * import arrow.fx.extensions.io.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   suspend fun logAndIncrease(s: String): Int {
 *      println(s)
 *      return s.toInt() + 1
 *   }
 *
 *   val result = IO.async().effect(Dispatchers.Default) { Thread.currentThread().name }.effectMap { s: String -> logAndIncrease(s) }
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("effectMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.effectMap(f: suspend (A) -> B): IO<B> = arrow.fx.IO.async().run {
  this@effectMap.effectMap<A, B>(f) as arrow.fx.IO<B>
}

/**
 *  [Async] models how a data type runs an asynchronous computation that may fail.
 *  Defined by the [Proc] signature, which is the consumption of a callback.
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.async(): IOAsync = async_singleton
