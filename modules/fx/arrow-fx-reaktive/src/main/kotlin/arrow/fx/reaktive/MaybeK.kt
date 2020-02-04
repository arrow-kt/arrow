package arrow.fx.reaktive

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.Right
import arrow.core.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.ExitCase
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeEmitter
import com.badoo.reaktive.maybe.blockingGet
import com.badoo.reaktive.maybe.doOnBeforeComplete
import com.badoo.reaktive.maybe.doOnBeforeDispose
import com.badoo.reaktive.maybe.doOnBeforeError
import com.badoo.reaktive.maybe.doOnBeforeSuccess
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.map
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.maybe.maybeDefer
import com.badoo.reaktive.maybe.maybeFromFunction
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeOfError
import com.badoo.reaktive.maybe.observeOn
import com.badoo.reaktive.maybe.onErrorResumeNext
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.single.blockingGet
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

typealias MaybeKProc<A> = ((Either<Throwable, A>) -> Unit) -> Unit
typealias MaybeKProcF<A> = ((Either<Throwable, A>) -> Unit) -> Kind<ForMaybeK, Unit>

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

data class MaybeK<out A>(val maybe: Maybe<A>) : MaybeKOf<A> {

  suspend fun suspended(): A? = suspendCoroutine { cont ->
    value().subscribe(onSuccess = cont::resume, onError = cont::resumeWithException, onComplete = { cont.resume(null) })
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
   * import arrow.fx.reaktive.*
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
   *   println_atomic(safeComputation)
   * }
   *  ```
   */

  fun <B> bracketCase(use: (A) -> MaybeKOf<B>, release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>): MaybeK<B> =
    value()
      .flatMap { value: A ->
        maybeDefer { use(value).value() }
          .doOnBeforeError { e ->
            release(value, ExitCase.Error(e.nonFatalOrThrow())).value().subscribe()
          }
          .doOnBeforeSuccess {
            release(value, ExitCase.Completed).value().subscribe()
          }
          .doOnBeforeComplete {
            release(value, ExitCase.Completed).value().subscribe()
          }
          .doOnBeforeDispose {
            release(value, ExitCase.Canceled).value().subscribe()
          }
      }
      .k()

  fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B = maybe.blockingGet().let {
    if (it == null) ifEmpty() else ifSome(it)
  }

  fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { a -> f(b, a) })

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { fold({ lb }, { a -> f(a, lb) }) }

  fun isEmpty(): Boolean = maybe.isEmpty().blockingGet()

  fun nonEmpty(): Boolean = !isEmpty()

  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { a -> predicate(a) })

  fun forall(p: Predicate<A>): Boolean = fold({ true }, p)

  fun continueOn(ctx: CoroutineContext): MaybeK<A> =
    maybe.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> MaybeKOf<Unit>): MaybeK<Unit> =
    maybe.flatMap { cb(Right(it)).value() }.onErrorResumeNext { cb(Left(it)).value() }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is MaybeK<*> -> this.maybe == other.maybe
      is Maybe<*> -> this.maybe == other
      else -> false
    }

  fun <B> filterMap(f: (A) -> Option<B>): MaybeK<B> =
    maybe.flatMap { a ->
      f(a).fold(::maybeOfEmpty, ::maybeOf)
    }.k()

  override fun hashCode(): Int = maybe.hashCode()

  companion object {
    fun <A> just(a: A): MaybeK<A> =
      maybeOf(a).k()

    fun <A> raiseError(t: Throwable): MaybeK<A> =
      maybeOfError<A>(t).k()

    operator fun <A> invoke(fa: () -> A): MaybeK<A> =
      maybeFromFunction(fa).k()

    fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
      maybeDefer { fa().value() }.k()

    /**
     * Creates a [MaybeK] that'll run [MaybeKProc].
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
      maybe<A> { emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError) {
            if (it == null) {
              emitter.onComplete()
            } else {
              emitter.onSuccess(it)
            }
          }
        }
      }.k()

    fun <A> asyncF(fa: MaybeKProcF<A>): MaybeK<A> =
      maybe { emitter: MaybeEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }.fix().maybe.subscribe(onError = emitter::onError)

        emitter.setDisposable(dispose)
      }.k()

    /**
     * Creates a [MaybeK] that'll run [MaybeKProc].
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
      maybe { emitter: MaybeEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }

        val token =
          try {
            fa(cb)
          } catch (t: Throwable) {
            cb(Left(t.nonFatalOrThrow()))
            just(Unit)
          }

        emitter.setDisposable(Disposable { token.value().subscribe(onError = emitter::onError) })
      }.k()

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> MaybeKOf<CancelToken<ForMaybeK>>): MaybeK<A> =
      maybe { emitter: MaybeEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold(emitter::onError, emitter::onSuccess)
        }

        val fa2 =
          try {
            fa(cb)
          } catch (t: Throwable) {
            cb(Left(t.nonFatalOrThrow()))
            just(just(Unit))
          }

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForMaybeK>>?>(null)
        val disposable =
          fa2
            .value()
            .subscribe(
              onSuccess = { token ->
                val cancel = cancelOrToken.getAndSet(Right(token))
                cancel?.fold({
                  token.value().subscribe(onError = emitter::onError)
                  Unit
                }, {})
              },
              onError = emitter::onError
            )

        emitter.setDisposable(
          Disposable {
            disposable.dispose()
            val token = cancelOrToken.getAndSet(Left(Unit))
            token?.fold({}, { it.value().subscribe(onError = emitter::onError) })
          }
        )
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> {
      val either: Either<A, B>? = f(a).value().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> maybeOf(either.b).k()
        null -> maybeOfEmpty<B>().k()
      }
    }
  }
}

fun <A> MaybeK<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
  value().subscribe(onSuccess = { cb(Right(it)) }, onError = { cb(Left(it)) }).let { }

fun <A> MaybeK<A>.unsafeRunSync(): A? =
  value().blockingGet()

fun <A> MaybeK<A>.handleErrorWith(function: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
