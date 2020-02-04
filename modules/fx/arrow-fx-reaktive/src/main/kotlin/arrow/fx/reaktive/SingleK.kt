package arrow.fx.reaktive

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.blockingGet
import com.badoo.reaktive.single.doOnBeforeDispose
import com.badoo.reaktive.single.doOnBeforeError
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.onErrorResumeNext
import com.badoo.reaktive.single.single
import com.badoo.reaktive.single.singleDefer
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.single.singleOfError
import com.badoo.reaktive.single.subscribe
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.badoo.reaktive.disposable.Disposable as RxDisposable

typealias SingleKProc<A> = ((Either<Throwable, A>) -> Unit) -> Unit
typealias SingleKProcF<A> = ((Either<Throwable, A>) -> Unit) -> SingleKOf<Unit>

class ForSingleK private constructor() {
  companion object
}
typealias SingleKOf<A> = arrow.Kind<ForSingleK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> SingleKOf<A>.fix(): SingleK<A> =
  this as SingleK<A>

fun <A> Single<A>.k(): SingleK<A> = SingleK(this)

@Suppress("UNCHECKED_CAST")
fun <A> SingleKOf<A>.value(): Single<A> = fix().single as Single<A>

data class SingleK<out A>(val single: Single<out A>) : SingleKOf<A> {

  suspend fun suspended(): A = suspendCoroutine { cont ->
    value().subscribe(onSuccess = cont::resume, onError = cont::resumeWithException)
  }

  fun <B> map(f: (A) -> B): SingleK<B> =
    single.map(f).k()

  fun <B> ap(fa: SingleKOf<(A) -> B>): SingleK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    single.flatMap { f(it).value() }.k()

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [SingleK] with the result.
   * Once the resulting [SingleK] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [SingleK] of [use] is terminates.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.reaktive.*
   * import arrow.fx.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): SingleK<String> =
   *     SingleK.just("This file contains some interesting content!")
   * }
   *
   * fun openFile(uri: String): SingleK<File> = SingleK { File(uri).open() }
   * fun closeFile(file: File): SingleK<Unit> = SingleK { file.close() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracketCase(
   *     release = { file, exitCase ->
   *       when (exitCase) {
   *         is ExitCase.Completed -> { /* do something */ }
   *         is ExitCase.Canceled -> { /* do something */ }
   *         is ExitCase.Error -> { /* do something */ }
   *       }
   *       closeFile(file)
   *     },
   *     use = { file -> file.content() }
   *   )
   *   //sampleEnd
   *   println_atomic(safeComputation)
   * }
   *  ```
   */
  fun <B> bracketCase(use: (A) -> SingleKOf<B>, release: (A, ExitCase<Throwable>) -> SingleKOf<Unit>): SingleK<B> =
    value()
      .flatMap { value: A ->
        singleDefer { use(value).value() }
          .doOnBeforeError { e ->
            release(value, ExitCase.Error(e.nonFatalOrThrow())).value().subscribe()
          }
          .doOnBeforeSuccess {
            release(value, ExitCase.Completed).value().subscribe()
          }
          .doOnBeforeDispose {
            release(value, ExitCase.Canceled).value().subscribe()
          }
      }
      .k()

  fun continueOn(ctx: CoroutineContext): SingleK<A> =
    single.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Unit> =
    single.flatMap { cb(Right(it)).value() }.onErrorResumeNext { cb(Left(it)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Disposable> =
    singleFromFunction {
      val disposable: RxDisposable = runAsync(cb).value().subscribe()
      disposable::dispose
    }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is SingleK<*> -> this.single == other.single
      is Single<*> -> this.single == other
      else -> false
    }

  override fun hashCode(): Int = single.hashCode()

  companion object {
    fun <A> just(a: A): SingleK<A> =
      singleOf(a).k()

    fun <A> raiseError(t: Throwable): SingleK<A> =
      singleOfError<A>(t).k()

    operator fun <A> invoke(fa: () -> A): SingleK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
      singleDefer { fa().value() }.k()

    /**
     * Creates a [SingleK] that'll run [SingleKProc].
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.fx.reaktive.*
     *
     * class NetworkApi {
     *   fun async(f: (String) -> Unit): Unit = f("Some value of a resource")
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = SingleK.async { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     nw.async { result -> cb(Right(result)) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println, ::println)
     * }
     * ```
     */
    fun <A> async(fa: SingleKProc<A>): SingleK<A> =
      SingleK(single { emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }
      })

    fun <A> asyncF(fa: SingleKProcF<A>): SingleK<A> =
      single { emitter: SingleEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }.fix().single.subscribe(onError = emitter::onError)

        emitter.setDisposable(dispose)
      }.k()

    /**
     * Creates a [SingleK] that'll run a cancelable operation.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.fx.reaktive.*
     *
     * typealias Disposable = () -> Unit
     * class NetworkApi {
     *   fun async(f: (String) -> Unit): Disposable {
     *     f("Some value of a resource")
     *     return { Unit }
     *   }
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = SingleK.cancelable { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     val disposable = nw.async { result -> cb(Right(result)) }
     *     SingleK { disposable.invoke() }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println, ::println)
     * }
     * ```
     */
    fun <A> cancelable(fa: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForSingleK>): SingleK<A> =
      single { emitter: SingleEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }

        val token = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(Unit)
        }

        emitter.setDisposable(com.badoo.reaktive.disposable.Disposable { token.value().subscribe(onError = emitter::onError) })
      }.k()

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> SingleKOf<CancelToken<ForSingleK>>): SingleK<A> =
      single { emitter: SingleEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }

        val fa2 = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(just(Unit))
        }

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForSingleK>>?>(null)
        val disposable = fa2.value().subscribe(onSuccess = { token ->
          val cancel = cancelOrToken.getAndSet(Right(token))
          cancel?.fold({
            token.value().subscribe(onError = emitter::onError)
            Unit
          }, {})
        }, onError = emitter::onError)

        emitter.setDisposable(
          com.badoo.reaktive.disposable.Disposable {
            disposable.dispose()
            val token = cancelOrToken.getAndSet(Left(Unit))
            token?.fold({}, {
              it.value().subscribe(onError = emitter::onError)
            })
          }
        )
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> SingleKOf<Either<A, B>>): SingleK<B> {
      val either: Either<A, B> = f(a).value().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> singleOf(either.b).k()
      }
    }
  }
}

/**
 * Runs the [SingleK] asynchronously and then runs the cb.
 * Catches all errors that may be thrown in await. Errors from cb will still throw as expected.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.fx.reaktive.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   SingleK.just(1).unsafeRunAsync { either: Either<Throwable, Int> ->
 *     either.fold({ t: Throwable ->
 *       println_atomic(t)
 *     }, { i: Int ->
 *       println_atomic("Finished with $i")
 *     })
 *   }
 *   //sampleEnd
 * }
 * ```
 */
fun <A> SingleKOf<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
  value().subscribe(onSuccess = { cb(Right(it)) }, onError = { cb(Left(it)) }).let { }

/**
 * Runs this [SingleK] with [Single.blockingGet]. Does not handle errors at all, rethrowing them if they happen.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.reaktive.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: SingleK<String> = SingleK.raiseError<String>(Exception("BOOM"))
 *   //sampleEnd
 *   println_atomic(result.unsafeRunSync())
 * }
 * ```
 */
fun <A> SingleKOf<A>.unsafeRunSync(): A =
  value().blockingGet()

fun <A> SingleK<A>.handleErrorWith(function: (Throwable) -> SingleKOf<A>): SingleK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
