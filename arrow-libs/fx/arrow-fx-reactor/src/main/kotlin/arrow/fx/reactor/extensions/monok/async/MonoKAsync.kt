package arrow.fx.reactor.extensions.monok.async

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKAsync
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
internal val async_singleton: MonoKAsync = object : arrow.fx.reactor.extensions.MonoKAsync {}

/**
 *  [async] variant that can suspend side effects in the provided registration function.
 *
 *  The passed in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
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
 *   val result = MonoK.async().makeCompleteAndGetPromiseInAsync()
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
@Deprecated(DeprecateReactor)
fun <A> asyncF(arg0: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForMonoK, Unit>>):
  MonoK<A> = arrow.fx.reactor.MonoK
    .async()
    .asyncF<A>(arg0) as arrow.fx.reactor.MonoK<A>

/**
 *  Continue the evaluation on provided [CoroutineContext]
 *
 *  @param ctx [CoroutineContext] to run evaluation on
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
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
 *   val result = MonoK.async().runOnDefaultDispatcher()
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
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.continueOn(arg1: CoroutineContext): MonoK<A> =
  arrow.fx.reactor.MonoK.async().run {
    this@continueOn.continueOn<A>(arg1) as arrow.fx.reactor.MonoK<A>
  }

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
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
 *   val result = MonoK.async().invokeOnDefaultDispatcher()
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
@Deprecated(DeprecateReactor)
fun <A> later(arg0: CoroutineContext, arg1: Function0<A>): MonoK<A> = arrow.fx.reactor.MonoK
  .async()
  .later<A>(arg0, arg1) as arrow.fx.reactor.MonoK<A>

/**
 *  Delay a suspended effect on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   suspend fun getThreadSuspended(): String = Thread.currentThread().name
 *
 *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
 *     effect(Dispatchers.Default, { getThreadSuspended() })
 *
 *   val result = MonoK.async().invokeOnDefaultDispatcher()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("effect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> effect(arg0: suspend () -> A): MonoK<A> = arrow.fx.reactor.MonoK
  .async()
  .effect<A>(arg0) as arrow.fx.reactor.MonoK<A>

/**
 *  Delay a suspended effect on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   suspend fun getThreadSuspended(): String = Thread.currentThread().name
 *
 *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
 *     effect(Dispatchers.Default, { getThreadSuspended() })
 *
 *   val result = MonoK.async().invokeOnDefaultDispatcher()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("effect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> effect(arg0: CoroutineContext, arg1: suspend () -> A): MonoK<A> = arrow.fx.reactor.MonoK
  .async()
  .effect<A>(arg0, arg1) as arrow.fx.reactor.MonoK<A>

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
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
 *   val result = MonoK.async().invokeOnDefaultDispatcher().fix().unsafeRunSync()
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
@Deprecated(DeprecateReactor)
fun <A> defer(arg0: CoroutineContext, arg1: Function0<Kind<ForMonoK, A>>): MonoK<A> =
  arrow.fx.reactor.MonoK
    .async()
    .defer<A>(arg0, arg1) as arrow.fx.reactor.MonoK<A>

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
@Deprecated(DeprecateReactor)
fun <A> laterOrRaise(arg0: CoroutineContext, arg1: Function0<Either<Throwable, A>>): MonoK<A> =
  arrow.fx.reactor.MonoK
    .async()
    .laterOrRaise<A>(arg0, arg1) as arrow.fx.reactor.MonoK<A>

/**
 *  Shift evaluation to provided [CoroutineContext].
 *
 *  @receiver [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   MonoK.async().run {
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
@Deprecated(DeprecateReactor)
fun CoroutineContext.shift(): MonoK<Unit> = arrow.fx.reactor.MonoK.async().run {
  this@shift.shift() as arrow.fx.reactor.MonoK<kotlin.Unit>
}

/**
 *  Task that never finishes evaluating.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
 * import arrow.core.*
 *
 *
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val i = MonoK.async().never<Int>()
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
@Deprecated(DeprecateReactor)
fun <A> never(): MonoK<A> = arrow.fx.reactor.MonoK
  .async()
  .never<A>() as arrow.fx.reactor.MonoK<A>

/**
 *  Helper function that provides an easy way to construct a suspend effect
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.async.*
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
 *   val result = MonoK.async().effect(Dispatchers.Default) { Thread.currentThread().name }.effectMap { s: String -> logAndIncrease(s) }
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
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.effectMap(arg1: suspend (A) -> B): MonoK<B> =
  arrow.fx.reactor.MonoK.async().run {
    this@effectMap.effectMap<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
  }

/**
 *  [Async] models how a data type runs an asynchronous computation that may fail.
 *  Defined by the [Proc] signature, which is the consumption of a callback.
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.async(): MonoKAsync = async_singleton
