package arrow.effects.rx2

import arrow.Kind
import arrow.core.*
import arrow.effects.OnCancel
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.higherkind
import arrow.typeclasses.Applicative
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlin.coroutines.CoroutineContext
import arrow.effects.rx2.CoroutineContextRx2Scheduler.asScheduler

fun <A> Observable<A>.k(): ObservableK<A> = ObservableK(this)

fun <A> ObservableKOf<A>.value(): Observable<A> =
  fix().observable

@higherkind
data class ObservableK<A>(val observable: Observable<A>) : ObservableKOf<A>, ObservableKKindedJ<A> {

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
   * import arrow.effects.rx2.*
   * import arrow.effects.typeclasses.ExitCase
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
    ObservableK(Observable.create<B> { emitter ->
      observable.subscribe({ a ->
        if (emitter.isDisposed) release(a, ExitCase.Canceled).fix().observable.subscribe({}, emitter::onError)
        else try {
          emitter.setDisposable(use(a).fix()
            .flatMap { b -> release(a, ExitCase.Completed).fix().map { b } }
            .handleErrorWith { e -> release(a, ExitCase.Error(e)).fix().flatMap { ObservableK.raiseError<B>(e) } }
            .observable
            .doOnDispose { release(a, ExitCase.Canceled).fix().observable.subscribe({}, emitter::onError) }
            .subscribe(emitter::onNext, emitter::onError))
        } catch (e: Throwable) {
          release(a, ExitCase.Error(e)).fix().observable.subscribe({
            emitter.onError(e)
          }, { e2 ->
            emitter.onError(Platform.composeErrors(e, e2))
          })
        }
      }, emitter::onError, emitter::onComplete)
    })

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

  fun handleErrorWith(function: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()

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

  override fun hashCode(): Int = observable.hashCode()

  companion object {
    fun <A> just(a: A): ObservableK<A> =
      Observable.just(a).k()

    fun <A> raiseError(t: Throwable): ObservableK<A> =
      Observable.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): ObservableK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
      Observable.defer { fa().value() }.k()

    /**
     * Creates a [ObservableK] that'll run [ObservableKProc].
     *
     * ```kotlin:ank:playground
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.rx2.ObservableK
     * import arrow.effects.rx2.ObservableKConnection
     * import arrow.effects.rx2.value
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = ObservableK.async { conn: ObservableKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(ObservableK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: ObservableKProc<A>): ObservableK<A> =
      Observable.create<A> { emitter ->
        val connection = ObservableKConnection()
        //On disposing of the upstream stream this will be called by `setCancellable` so check if upstream is already disposed or not because
        //on disposing the stream will already be in a terminated state at this point so calling onError, in a terminated state, will blow everything up.
        connection.push(ObservableK { if (!emitter.isDisposed) emitter.onError(OnCancel.CancellationException) })
        emitter.setCancellable { connection.cancel().value().subscribe({}, {}) }

        fa(connection) { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onNext(it)
            emitter.onComplete()
          })
        }
      }.k()

    fun <A> asyncF(fa: ObservableKProcF<A>): ObservableK<A> =
      Observable.create { emitter: ObservableEmitter<A> ->
        val connection = ObservableKConnection()
        //On disposing of the upstream stream this will be called by `setCancellable` so check if upstream is already disposed or not because
        //on disposing the stream will already be in a terminated state at this point so calling onError, in a terminated state, will blow everything up.
        connection.push(ObservableK { if (!emitter.isDisposed) emitter.onError(OnCancel.CancellationException) })
        emitter.setCancellable { connection.cancel().value().subscribe({}, {}) }

        fa(connection) { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onNext(it)
            emitter.onComplete()
          })
        }.fix().observable.subscribe({}, emitter::onError)
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
