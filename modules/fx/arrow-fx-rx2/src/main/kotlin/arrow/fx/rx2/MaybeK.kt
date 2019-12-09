package arrow.fx.rx2

import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.Right
import arrow.core.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.fx.CancelToken
import arrow.fx.internal.Platform
import arrow.fx.rx2.CoroutineContextRx2Scheduler.asScheduler
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.ExitCase.Canceled
import arrow.fx.typeclasses.ExitCase.Completed
import arrow.fx.typeclasses.ExitCase.Error
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ForMaybeK private constructor() {
  companion object
}
typealias MaybeKOf<A> = arrow.Kind<ForMaybeK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> MaybeKOf<A>.fix(): MaybeK<A> =
  this as MaybeK<A>

fun <A> Maybe<A>.k(): MaybeK<A> = MaybeK(this)

@Suppress("UNCHECKED_CAST")
fun <A> MaybeKOf<A>.value(): Maybe<A> = fix().maybe as Maybe<A>

data class MaybeK<out A>(val maybe: Maybe<out A>) : MaybeKOf<A> {

  suspend fun suspended(): A? = suspendCoroutine { cont ->
    value().subscribe(cont::resume, cont::resumeWithException) { cont.resume(null) }
  }

  fun <B> map(f: (A) -> B): MaybeK<B> =
    maybe.map(f).k()

  fun <B> ap(fa: MaybeKOf<(A) -> B>): MaybeK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    maybe.flatMap { f(it).value() }.k()

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [MaybeK] with the result.
   * Once the resulting [MaybeK] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [MaybeK] of [use] is terminates.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.rx2.*
   * import arrow.fx.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): MaybeK<String> =
   *     MaybeK.just("This file contains some interesting content!")
   * }
   *
   * fun openFile(uri: String): MaybeK<File> = MaybeK { File(uri).open() }
   * fun closeFile(file: File): MaybeK<Unit> = MaybeK { file.close() }
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
  fun <B> bracketCase(use: (A) -> MaybeKOf<B>, release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>): MaybeK<B> =
    Maybe.create<B> { emitter ->
      val dispose =
        handleErrorWith { t -> Maybe.fromCallable { emitter.onError(t) }.flatMap { Maybe.error<A>(t) }.k() }
          .flatMap { a ->
            if (emitter.isDisposed) {
              release(a, Canceled).fix().maybe.subscribe({}, emitter::onError)
              Maybe.never<B>().k()
            } else {
              MaybeK.defer { use(a) }
                .value()
                .doOnError { t: Throwable ->
                  MaybeK.defer { release(a, Error(t.nonFatalOrThrow())) }.value().subscribe({ emitter.onError(t) }, { e -> emitter.onError(Platform.composeErrors(t, e)) })
                }.doAfterSuccess {
                  MaybeK.defer { release(a, Completed) }.fix().value().subscribe({ emitter.onComplete() }, emitter::onError)
                }
                .doOnDispose {
                  MaybeK.defer { release(a, Canceled) }.value().subscribe({}, {})
                }
                .k()
            }
          }
          .value().subscribe(emitter::onSuccess, {}, {})
      emitter.setCancellable { dispose.dispose() }
    }.k()

  fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B = maybe.blockingGet().let {
    if (it == null) ifEmpty() else ifSome(it)
  }

  fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { a -> f(b, a) })

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { fold({ lb }, { a -> f(a, lb) }) }

  fun isEmpty(): Boolean = maybe.isEmpty.blockingGet()

  fun nonEmpty(): Boolean = !isEmpty()

  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { a -> predicate(a) })

  fun forall(p: Predicate<A>): Boolean = fold({ true }, p)

  fun continueOn(ctx: CoroutineContext): MaybeK<A> =
    maybe.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> MaybeKOf<Unit>): MaybeK<Unit> =
    maybe.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is MaybeK<*> -> this.maybe == other.maybe
      is Maybe<*> -> this.maybe == other
      else -> false
    }

  fun <B> filterMap(f: (A) -> Option<B>): MaybeK<B> =
    maybe.flatMap { a ->
      f(a).fold({ Maybe.empty<B>() }, { b -> Maybe.just(b) })
    }.k()

  override fun hashCode(): Int = maybe.hashCode()

  companion object {
    fun <A> just(a: A): MaybeK<A> =
      Maybe.just(a).k()

    fun <A> raiseError(t: Throwable): MaybeK<A> =
      Maybe.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): MaybeK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
      Maybe.defer { fa().value() }.k()

    /**
     * Creates a [MaybeK] that'll run [MaybeKProc].
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
     *   val result = MaybeK.async { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     nw.async { result -> cb(Right(result)) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: MaybeKProc<A>): MaybeK<A> =
      Maybe.create<A> { emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it)
          }, {
            emitter.onSuccess(it)
            emitter.onComplete()
          })
        }
      }.k()

    fun <A> asyncF(fa: MaybeKProcF<A>): MaybeK<A> =
      Maybe.create { emitter: MaybeEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it)
          }, {
            emitter.onSuccess(it)
            emitter.onComplete()
          })
        }.fix().maybe.subscribe({}, { e -> emitter.tryOnError(e) })

        emitter.setCancellable { dispose.dispose() }
      }.k()

    /**
     * Creates a [MaybeK] that'll run [MaybeKProc].
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
     *   val result = MaybeK.cancelable { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     val disposable = nw.async { result -> cb(Right(result)) }
     *     MaybeK { disposable.invoke() }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> cancelable(fa: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForMaybeK>): MaybeK<A> =
      Maybe.create { emitter: MaybeEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it).let { Unit }
          }, {
            emitter.onSuccess(it)
            emitter.onComplete()
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

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> MaybeKOf<CancelToken<ForMaybeK>>): MaybeK<A> =
      Maybe.create { emitter: MaybeEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it).let { Unit }
          }, {
            emitter.onSuccess(it)
            emitter.onComplete()
          })
        }

        val fa2 = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(just(Unit))
        }

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForMaybeK>>?>(null)
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

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> {
      val either = f(a).value().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Maybe.just(either.b).k()
      }
    }
  }
}

fun <A> MaybeK<A>.handleErrorWith(function: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
