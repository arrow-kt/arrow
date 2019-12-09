package arrow.fx.rx2

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.core.internal.AtomicRefW
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.fx.CancelToken
import arrow.fx.internal.Platform
import arrow.fx.rx2.CoroutineContextRx2Scheduler.asScheduler
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import arrow.typeclasses.Applicative
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlin.coroutines.CoroutineContext

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
  fix().observable as Observable<A>

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
   * import arrow.fx.rx2.*
   * import arrow.fx.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): ObservableK<String> =
   *     Observable.just("This", "file", "contains", "some", "interesting", "content!").k()
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
   *   println(safeComputation)
   * }
   *  ```
   */
  fun <B> bracketCase(use: (A) -> ObservableKOf<B>, release: (A, ExitCase<Throwable>) -> ObservableKOf<Unit>): ObservableK<B> =
    Observable.create<B> { emitter ->
      val dispose =
        handleErrorWith { t -> Observable.fromCallable { emitter.tryOnError(t) }.flatMap { Observable.error<A>(t) }.k() }
          .concatMap { a ->
            if (emitter.isDisposed) {
              release(a, ExitCase.Canceled).fix().observable.subscribe({}, { e -> emitter.tryOnError(e) })
              Observable.never<B>().k()
            } else {
              defer { use(a) }
                .value()
                .doOnError { t: Throwable ->
                  defer { release(a, ExitCase.Error(t.nonFatalOrThrow())) }.value().subscribe({ emitter.tryOnError(t) }, { e -> emitter.tryOnError(Platform.composeErrors(t, e)) })
                }.doOnComplete {
                  defer { release(a, ExitCase.Completed) }.fix().value().subscribe({ emitter.onComplete() }, { e ->
                    emitter.tryOnError(e)
                  })
                }
                .doOnDispose {
                  defer { release(a, ExitCase.Canceled) }.value().subscribe({}, {})
                }
                .k()
            }
          }
          .value().subscribe(emitter::onNext, {}, {})
      emitter.setCancellable { dispose.dispose() }
    }.k()

  fun <B> concatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.concatMap { f(it).value() }.k()

  fun <B> switchMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.switchMap { f(it).value() }.k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = observable.reduce(b, f).blockingGet()

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: ObservableK<A>): Eval<B> = when {
      fa_p.observable.isEmpty.blockingGet() -> lb
      else -> f(fa_p.observable.blockingFirst(), Eval.defer { loop(fa_p.observable.skip(1).k()) })
    }

    return Eval.defer { loop(this) }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ObservableK<B>> =
    foldRight(Eval.always { GA.just(Observable.empty<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { Observable.concat(Observable.just<B>(it.a), it.b.observable).k() } }
    }.value()

  fun continueOn(ctx: CoroutineContext): ObservableK<A> =
    observable.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
    observable.flatMap { cb(Right(it)).value() }.onErrorResumeNext { t: Throwable -> cb(Left(t)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Disposable> =
    Observable.fromCallable {
      val disposable: io.reactivex.disposables.Disposable = runAsync(cb).value().subscribe()
      val dispose: () -> Unit = { disposable.dispose() }
      dispose
    }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is ObservableK<*> -> this.observable == other.observable
      is Observable<*> -> this.observable == other
      else -> false
    }

  fun <B> filterMap(f: (A) -> Option<B>): ObservableK<B> =
    observable.flatMap { a ->
      f(a).fold({ Observable.empty<B>() }, { b -> Observable.just(b) })
    }.k()

  override fun hashCode(): Int = observable.hashCode()

  companion object {
    fun <A> just(a: A): ObservableK<A> =
      Observable.just(a).k()

    fun <A> raiseError(t: Throwable): ObservableK<A> =
      Observable.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): ObservableK<A> =
      Observable.fromCallable(fa).k()

    fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
      Observable.defer { fa().value() }.k()

    /**
     * Creates a [ObservableK] that'll run [ObservableKProc].
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
      Observable.create<A> { emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold({ e ->
            emitter.tryOnError(e)
          }, { a ->
            emitter.onNext(a)
            emitter.onComplete()
          })
        }
      }.k()

    fun <A> asyncF(fa: ObservableKProcF<A>): ObservableK<A> =
      Observable.create { emitter: ObservableEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it)
          }, {
            emitter.onNext(it)
            emitter.onComplete()
          })
        }.fix().observable.subscribe({}, { e -> emitter.tryOnError(e) })

        emitter.setCancellable { dispose.dispose() }
      }.k()

    /**
     * Creates a [ObservableK] that'll run a cancelable operation.
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
      Observable.create<A> { emitter ->
        val token = fa { either: Either<Throwable, A> ->
          either.fold({ e ->
            emitter.tryOnError(e)
          }, { a ->
            emitter.onNext(a)
            emitter.onComplete()
          })
        }
        emitter.setCancellable { token.value().subscribe({}, { e -> emitter.tryOnError(e) }) }
      }.k()

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> ObservableKOf<CancelToken<ForObservableK>>): ObservableK<A> =
      Observable.create { emitter: ObservableEmitter<A> ->
        val cb = { either: Either<Throwable, A> ->
          either.fold({
            emitter.tryOnError(it).let { Unit }
          }, { a ->
            emitter.onNext(a)
            emitter.onComplete()
          })
        }

        val fa2 = try {
          fa(cb)
        } catch (t: Throwable) {
          cb(Left(t.nonFatalOrThrow()))
          just(just(Unit))
        }

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForObservableK>>?>(null)
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

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> ObservableKOf<Either<A, B>>): ObservableK<B> {
      val either = f(a).value().blockingFirst()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Observable.just(either.b).k()
      }
    }
  }
}

fun <A, G> ObservableKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, ObservableK<A>> =
  fix().traverse(GA, ::identity)

fun <A> ObservableKOf<A>.handleErrorWith(function: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
