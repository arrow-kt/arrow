package arrow.fx.reaktive

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.core.identity
import arrow.core.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import arrow.typeclasses.Applicative
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.maybe.blockingGet
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableEmitter
import com.badoo.reaktive.observable.collect
import com.badoo.reaktive.observable.concat
import com.badoo.reaktive.observable.concatMap
import com.badoo.reaktive.observable.doOnBeforeComplete
import com.badoo.reaktive.observable.doOnBeforeDispose
import com.badoo.reaktive.observable.doOnBeforeError
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.observableDefer
import com.badoo.reaktive.observable.observableFromFunction
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.observableOfEmpty
import com.badoo.reaktive.observable.observableOfError
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.onErrorResumeNext
import com.badoo.reaktive.observable.skip
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.switchMap
import com.badoo.reaktive.single.blockingGet
import kotlin.coroutines.CoroutineContext
import com.badoo.reaktive.disposable.Disposable as RxDisposable

typealias ObservableKProc<A> = ((Either<Throwable, A>) -> Unit) -> Unit
typealias ObservableKProcF<A> = ((Either<Throwable, A>) -> Unit) -> ObservableKOf<Unit>

class ForObservableK private constructor() {
  companion object
}
typealias ObservableKOf<A> = arrow.Kind<ForObservableK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> ObservableKOf<A>.fix(): ObservableK<A> =
  this as ObservableK<A>

fun <A> Observable<A>.k(): ObservableK<A> = ObservableK(this)

@Suppress("UNCHECKED_CAST")
fun <A> ObservableKOf<A>.value(): Observable<A> =
  fix().observable

data class ObservableK<out A>(val observable: Observable<out A>) : ObservableKOf<A> {

  fun <B> map(f: (A) -> B): ObservableK<B> =
    observable.map(f).k()

  fun <B> ap(fa: ObservableKOf<(A) -> B>): ObservableK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.flatMap { f(it).value() }.k()

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [ObservableK] with the result.
   * Once the resulting [ObservableK] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [ObservableK] of [use] is terminates.
   *
   * ```kotlin:ank:playground
   * import io.reactivex.Observable
   * import arrow.fx.reaktive.*
   * import arrow.fx.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): ObservableK<String> =
   *     observableOf("This", "file", "contains", "some", "interesting", "content!").k()
   * }
   *
   * fun openFile(uri: String): ObservableK<File> = ObservableK { File(uri).open() }
   * fun closeFile(file: File): ObservableK<Unit> = ObservableK { file.close() }
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
  fun <B> bracketCase(use: (A) -> ObservableKOf<B>, release: (A, ExitCase<Throwable>) -> ObservableKOf<Unit>): ObservableK<B> =
    value()
      .flatMap { value: A ->
        observableDefer { use(value).value() }
          .doOnBeforeError { e ->
            release(value, ExitCase.Error(e.nonFatalOrThrow())).value().subscribe()
          }
          .doOnBeforeComplete {
            release(value, ExitCase.Completed).value().subscribe()
          }
          .doOnBeforeDispose {
            release(value, ExitCase.Canceled).value().subscribe()
          }
      }
      .k()

  fun <B> concatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.concatMap { f(it).value() }.k()

  fun <B> switchMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.switchMap { f(it).value() }.k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = observable.collect(b, f).blockingGet()

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: ObservableK<A>): Eval<B> = when {
      fa_p.observable.isEmpty().blockingGet() -> lb
      else -> f(fa_p.observable.firstOrError().blockingGet(), Eval.defer { loop(fa_p.observable.skip(1).k()) })
    }

    return Eval.defer { loop(this) }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ObservableK<B>> =
    foldRight(Eval.always { GA.just(observableOfEmpty<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { concat(observableOf(it.a), it.b.observable).k() } }
    }.value()

  fun continueOn(ctx: CoroutineContext): ObservableK<A> =
    observable.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
    observable.flatMap { cb(Right(it)).value() }.onErrorResumeNext { t: Throwable -> cb(Left(t)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Disposable> =
    observableFromFunction {
      val disposable: RxDisposable = runAsync(cb).value().subscribe()
      disposable::dispose
    }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is ObservableK<*> -> this.observable == other.observable
      is Observable<*> -> this.observable == other
      else -> false
    }

  fun <B> filterMap(f: (A) -> Option<B>): ObservableK<B> =
    observable.flatMap { a ->
      f(a).fold(::observableOfEmpty, ::observableOf)
    }.k()

  override fun hashCode(): Int = observable.hashCode()

  companion object {
    fun <A> just(a: A): ObservableK<A> =
      observableOf(a).k()

    fun <A> raiseError(t: Throwable): ObservableK<A> =
      observableOfError<A>(t).k()

    operator fun <A> invoke(fa: () -> A): ObservableK<A> =
      observableFromFunction(fa).k()

    fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
      observableDefer { fa().value() }.k()

    /**
     * Creates a [ObservableK] that'll run [ObservableKProc].
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
     *   val result = ObservableK.async { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     nw.async { result -> cb(Right(result)) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: ObservableKProc<A>): ObservableK<A> =
      observable<A> { emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError) { a ->
            emitter.onNext(a)
            emitter.onComplete()
          }
        }
      }.k()

    fun <A> asyncF(fa: ObservableKProcF<A>): ObservableK<A> =
      observable { emitter: ObservableEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError) {
            emitter.onNext(it)
            emitter.onComplete()
          }
        }.fix().observable.subscribe(onError = emitter::onError)

        emitter.setDisposable(dispose)
      }.k()

    /**
     * Creates a [ObservableK] that'll run a cancelable operation.
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
     *   val result = ObservableK.cancelable { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     val disposable = nw.async { result -> cb(Right(result)) }
     *     ObservableK { disposable.invoke() }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> cancelable(fa: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForObservableK>): ObservableK<A> =
      observable<A> { emitter ->
        val token = fa { either: Either<Throwable, A> ->
          either.fold(emitter::onError) { a ->
            emitter.onNext(a)
            emitter.onComplete()
          }
        }
        emitter.setDisposable(com.badoo.reaktive.disposable.Disposable { token.value().subscribe(onError = { e -> emitter.onError(e) }) })
      }.k()

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> ObservableKOf<CancelToken<ForObservableK>>): ObservableK<A> =
      observable { emitter: ObservableEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold(emitter::onError) { a ->
            emitter.onNext(a)
            emitter.onComplete()
          }
        }

        val fa2 = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(just(Unit))
        }

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForObservableK>>?>(null)
        val disposable =
          fa2
            .value()
            .subscribe(
              onNext = { token ->
                val cancel = cancelOrToken.getAndSet(Right(token))
                cancel?.fold({
                  token.value().subscribe(onError = emitter::onError)
                  Unit
                }, {})
              },
              onError = emitter::onError
            )

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

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> ObservableKOf<Either<A, B>>): ObservableK<B> {
      val either: Either<A, B>? = f(a).value().firstOrComplete().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> observableOf(either.b).k()
        null -> observableOfEmpty<B>().k()
      }
    }
  }
}

fun <A, G> ObservableKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, ObservableK<A>> =
  fix().traverse(GA, ::identity)

fun <A> ObservableKOf<A>.handleErrorWith(function: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
