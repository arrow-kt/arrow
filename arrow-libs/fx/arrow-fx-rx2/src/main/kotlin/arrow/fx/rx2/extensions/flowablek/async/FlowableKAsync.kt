package arrow.fx.rx2.extensions.flowablek.async

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKAsync
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
internal val async_singleton: FlowableKAsync = object : arrow.fx.rx2.extensions.FlowableKAsync {}

/**
 *  [async] variant that can suspend side effects in the provided registration function.
 *
 *  The passed in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().makeCompleteAndGetPromiseInAsync()
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
@Deprecated(DeprecateRxJava)
fun <A> asyncF(arg0: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForFlowableK, Unit>>):
    FlowableK<A> = arrow.fx.rx2.FlowableK
   .async()
   .asyncF<A>(arg0) as arrow.fx.rx2.FlowableK<A>

/**
 *  Continue the evaluation on provided [CoroutineContext]
 *
 *  @param ctx [CoroutineContext] to run evaluation on
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().runOnDefaultDispatcher()
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
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.continueOn(arg1: CoroutineContext): FlowableK<A> =
    arrow.fx.rx2.FlowableK.async().run {
  this@continueOn.continueOn<A>(arg1) as arrow.fx.rx2.FlowableK<A>
}

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().invokeOnDefaultDispatcher()
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
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: CoroutineContext, arg1: Function0<A>): FlowableK<A> = arrow.fx.rx2.FlowableK
   .async()
   .later<A>(arg0, arg1) as arrow.fx.rx2.FlowableK<A>

/**
 *  Delay a suspended effect on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().invokeOnDefaultDispatcher()
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
@Deprecated(DeprecateRxJava)
fun <A> effect(arg0: suspend () -> A): FlowableK<A> = arrow.fx.rx2.FlowableK
   .async()
   .effect<A>(arg0) as arrow.fx.rx2.FlowableK<A>

/**
 *  Delay a suspended effect on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().invokeOnDefaultDispatcher()
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
@Deprecated(DeprecateRxJava)
fun <A> effect(arg0: CoroutineContext, arg1: suspend () -> A): FlowableK<A> = arrow.fx.rx2.FlowableK
   .async()
   .effect<A>(arg0, arg1) as arrow.fx.rx2.FlowableK<A>

/**
 *  Delay a computation on provided [CoroutineContext].
 *
 *  @param ctx [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().invokeOnDefaultDispatcher().fix().unsafeRunSync()
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
@Deprecated(DeprecateRxJava)
fun <A> defer(arg0: CoroutineContext, arg1: Function0<Kind<ForFlowableK, A>>): FlowableK<A> =
    arrow.fx.rx2.FlowableK
   .async()
   .defer<A>(arg0, arg1) as arrow.fx.rx2.FlowableK<A>

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
@Deprecated(DeprecateRxJava)
fun <A> laterOrRaise(arg0: CoroutineContext, arg1: Function0<Either<Throwable, A>>): FlowableK<A> =
    arrow.fx.rx2.FlowableK
   .async()
   .laterOrRaise<A>(arg0, arg1) as arrow.fx.rx2.FlowableK<A>

/**
 *  Shift evaluation to provided [CoroutineContext].
 *
 *  @receiver [CoroutineContext] to run evaluation on.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
 * import arrow.core.*
 *
 *
 *  import kotlinx.coroutines.Dispatchers
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   FlowableK.async().run {
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
@Deprecated(DeprecateRxJava)
fun CoroutineContext.shift(): FlowableK<Unit> = arrow.fx.rx2.FlowableK.async().run {
  this@shift.shift() as arrow.fx.rx2.FlowableK<kotlin.Unit>
}

/**
 *  Task that never finishes evaluating.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
 * import arrow.core.*
 *
 *
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val i = FlowableK.async().never<Int>()
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
@Deprecated(DeprecateRxJava)
fun <A> never(): FlowableK<A> = arrow.fx.rx2.FlowableK
   .async()
   .never<A>() as arrow.fx.rx2.FlowableK<A>

/**
 *  Helper function that provides an easy way to construct a suspend effect
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.rx2.*
 * import arrow.fx.rx2.extensions.flowablek.async.*
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
 *   val result = FlowableK.async().effect(Dispatchers.Default) { Thread.currentThread().name }.effectMap { s: String -> logAndIncrease(s) }
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
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.effectMap(arg1: suspend (A) -> B): FlowableK<B> =
    arrow.fx.rx2.FlowableK.async().run {
  this@effectMap.effectMap<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

/**
 *  [Async] models how a data type runs an asynchronous computation that may fail.
 *  Defined by the [Proc] signature, which is the consumption of a callback.
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.async(): FlowableKAsync = async_singleton
