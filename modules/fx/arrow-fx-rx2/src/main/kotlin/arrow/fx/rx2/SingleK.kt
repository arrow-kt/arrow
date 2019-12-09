package arrow.fx.rx2

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.fx.CancelToken
import arrow.fx.internal.Platform
import arrow.fx.rx2.CoroutineContextRx2Scheduler.asScheduler
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.ExitCase.Canceled
import arrow.fx.typeclasses.ExitCase.Completed
import arrow.fx.typeclasses.ExitCase.Error
import io.reactivex.Single
import io.reactivex.SingleEmitter
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
    value().subscribe(cont::resume, cont::resumeWithException)
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
   * import arrow.fx.rx2.*
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
   *   println(safeComputation)
   * }
   *  ```
   */
  fun <B> bracketCase(use: (A) -> SingleKOf<B>, release: (A, ExitCase<Throwable>) -> SingleKOf<Unit>): SingleK<B> =
    Single.create<B> { emitter ->
      val dispose =
        handleErrorWith { t -> Single.fromCallable { emitter.onError(t) }.flatMap { Single.error<A>(t) }.k() }
          .flatMap { a ->
            if (emitter.isDisposed) {
              release(a, Canceled).fix().single.subscribe({}, emitter::onError)
              Single.never<B>().k()
            } else {
              SingleK.defer { use(a) }
                .value()
                .doOnError { t: Throwable ->
                  SingleK.defer { release(a, Error(t.nonFatalOrThrow())) }.value().subscribe({ emitter.onError(t) }, { e -> emitter.onError(Platform.composeErrors(t, e)) })
                }.doAfterSuccess {
                  SingleK.defer { release(a, Completed) }.fix().value().subscribe({ }, emitter::onError)
                }.doOnDispose {
                  SingleK.defer { release(a, Canceled) }.value().subscribe({}, {})
                }
                .k()
            }
          }
          .value().subscribe(emitter::onSuccess) {}
      emitter.setCancellable { dispose.dispose() }
    }.k()

  fun continueOn(ctx: CoroutineContext): SingleK<A> =
    single.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Unit> =
    single.flatMap { cb(Right(it)).value() }.onErrorResumeNext { cb(Left(it)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Disposable> =
    Single.fromCallable {
      val disposable: io.reactivex.disposables.Disposable = runAsync(cb).value().subscribe()
      val dispose: () -> Unit = { disposable.dispose() }
      dispose
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
      Single.just(a).k()

    fun <A> raiseError(t: Throwable): SingleK<A> =
      Single.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): SingleK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
      Single.defer { fa().value() }.k()

    /**
     * Creates a [SingleK] that'll run [SingleKProc].
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.fx.rx2.*
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
      SingleK(Single.create<A> { emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onSuccess(it)
          })
        }
      })

    fun <A> asyncF(fa: SingleKProcF<A>): SingleK<A> =
      Single.create { emitter: SingleEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onSuccess(it)
          })
        }.fix().single.subscribe({}, emitter::onError)

        emitter.setCancellable { dispose.dispose() }
      }.k()

    /**
     * Creates a [SingleK] that'll run a cancelable operation.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.fx.rx2.*
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
      Single.create { emitter: SingleEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it).let { Unit }
          }, {
            emitter.onSuccess(it)
          })
        }

        val token = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(Unit)
        }

        emitter.setCancellable { token.value().subscribe({}, { e -> emitter.tryOnError(e) }) }
      }.k()

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> SingleKOf<CancelToken<ForSingleK>>): SingleK<A> =
      Single.create { emitter: SingleEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it).let { Unit }
          }, {
            emitter.onSuccess(it)
          })
        }

        val fa2 = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(just(Unit))
        }

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForSingleK>>?>(null)
        val disp = fa2.value().subscribe({ token ->
          val cancel = cancelOrToken.getAndSet(Right(token))
          cancel?.fold({
            token.value().subscribe({}, { e -> emitter.tryOnError(e) }).let { Unit }
          }, {})
        }, { e -> emitter.tryOnError(e) })

        emitter.setCancellable {
          disp.dispose()
          val token = cancelOrToken.getAndSet(Left(Unit))
          token?.fold({}, {
            it.value().subscribe({}, { e -> emitter.tryOnError(e) })
          })
        }
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> SingleKOf<Either<A, B>>): SingleK<B> {
      val either = f(a).value().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Single.just(either.b).k()
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
 * import arrow.fx.rx2.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   SingleK.just(1).unsafeRunAsync { either: Either<Throwable, Int> ->
 *     either.fold({ t: Throwable ->
 *       println(t)
 *     }, { i: Int ->
 *       println("Finished with $i")
 *     })
 *   }
 *   //sampleEnd
 * }
 * ```
 */
fun <A> SingleKOf<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
  value().subscribe({ cb(Right(it)) }, { cb(Left(it)) }).let { }

/**
 * Runs this [SingleK] with [Single.blockingGet]. Does not handle errors at all, rethrowing them if they happen.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.rx2.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: SingleK<String> = SingleK.raiseError<String>(Exception("BOOM"))
 *   //sampleEnd
 *   println(result.unsafeRunSync())
 * }
 * ```
 */
fun <A> SingleKOf<A>.unsafeRunSync(): A =
  value().blockingGet()

fun <A> SingleK<A>.handleErrorWith(function: (Throwable) -> SingleKOf<A>): SingleK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
