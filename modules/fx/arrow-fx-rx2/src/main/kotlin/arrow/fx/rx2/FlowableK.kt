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
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import kotlin.coroutines.CoroutineContext

class ForFlowableK private constructor() {
  companion object
}
typealias FlowableKOf<A> = arrow.Kind<ForFlowableK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> FlowableKOf<A>.fix(): FlowableK<A> =
  this as FlowableK<A>

fun <A> Flowable<A>.k(): FlowableK<A> = FlowableK(this)

@Suppress("UNCHECKED_CAST")
fun <A> FlowableKOf<A>.value(): Flowable<A> = fix().flowable as Flowable<A>

data class FlowableK<out A>(val flowable: Flowable<out A>) : FlowableKOf<A> {

  fun <B> map(f: (A) -> B): FlowableK<B> =
    flowable.map(f).k()

  fun <B> ap(fa: FlowableKOf<(A) -> B>): FlowableK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.flatMap { f(it).value() }.k()

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [FlowableK] with the result.
   * Once the resulting [FlowableK] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [FlowableK] of [use] is terminates.
   *
   * ```kotlin:ank:playground
   * import io.reactivex.Flowable
   * import arrow.fx.rx2.*
   * import arrow.fx.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): FlowableK<String> =
   *     Flowable.just("This", "file", "contains", "some", "interesting", "content!").k()
   * }
   *
   * fun openFile(uri: String): FlowableK<File> = FlowableK { File(uri).open() }
   * fun closeFile(file: File): FlowableK<Unit> = FlowableK { file.close() }
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
  fun <B> bracketCase(use: (A) -> FlowableKOf<B>, release: (A, ExitCase<Throwable>) -> FlowableKOf<Unit>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<B> =
    Flowable.create<B>({ emitter ->
      val dispose =
        handleErrorWith { e -> Flowable.fromCallable { emitter.onError(e) }.flatMap { Flowable.error<A>(e) }.k() }
          .value()
          .concatMap { a ->
            if (emitter.isCancelled) {
              release(a, ExitCase.Canceled).value().subscribe({}, emitter::onError)
              Flowable.never<B>()
            } else {
              Flowable.defer { use(a).value() }
                .doOnError { t: Throwable ->
                  Flowable.defer { release(a, ExitCase.Error(t.nonFatalOrThrow())).value() }.subscribe({ emitter.onError(t) }, { e -> emitter.onError(Platform.composeErrors(t, e)) })
                }.doOnComplete {
                  Flowable.defer { release(a, ExitCase.Completed).value() }.subscribe({ emitter.onComplete() }, emitter::onError)
                }.doOnCancel {
                  Flowable.defer { release(a, ExitCase.Canceled).value() }.subscribe({}, {})
                }
            }
          }.subscribe(emitter::onNext, {}, {})
      emitter.setCancellable { dispose.dispose() }
    }, mode).k()

  fun <B> concatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.concatMap { f(it).value() }.k()

  fun <B> switchMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.switchMap { f(it).value() }.k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = flowable.reduce(b, f).blockingGet()

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: FlowableK<A>): Eval<B> = when {
      fa_p.flowable.isEmpty.blockingGet() -> lb
      else -> f(fa_p.flowable.blockingFirst(), Eval.defer { loop(fa_p.flowable.skip(1).k()) })
    }

    return Eval.defer { loop(this) }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FlowableK<B>> =
    foldRight(Eval.always { GA.just(Flowable.empty<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { Flowable.concat(Flowable.just<B>(it.a), it.b.flowable).k() } }
    }.value()

  fun continueOn(ctx: CoroutineContext): FlowableK<A> =
    flowable.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
    flowable.flatMap { cb(Right(it)).value() }.onErrorResumeNext { t: Throwable -> cb(Left(t)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Disposable> =
    Flowable.fromCallable {
      val disposable: io.reactivex.disposables.Disposable = runAsync(cb).value().subscribe()
      val dispose: () -> Unit = { disposable.dispose() }
      dispose
    }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is FlowableK<*> -> this.flowable == other.flowable
      is Flowable<*> -> this.flowable == other
      else -> false
    }

  fun <B> filterMap(f: (A) -> Option<B>): FlowableK<B> =
    flowable.flatMap { a ->
      f(a).fold({ Flowable.empty<B>() }, { b -> Flowable.just(b) })
    }.k()

  override fun hashCode(): Int = flowable.hashCode()

  companion object {
    fun <A> just(a: A): FlowableK<A> =
      Flowable.just(a).k()

    fun <A> raiseError(t: Throwable): FlowableK<A> =
      Flowable.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): FlowableK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
      Flowable.defer { fa().value() }.k()

    /**
     * Creates a [FlowableK] that'll run [FlowableKProc].
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
     *   val result = FlowableK.async(fa= { cb: (Either<Throwable, String>) -> Unit ->
     *     val nw = NetworkApi()
     *     nw.async { value -> cb(Right(value)) }
     *   })
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: FlowableKProc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create<A>({ emitter ->
        fa { either: Either<Throwable, A> ->
          either.fold({ e ->
            emitter.tryOnError(e)
          }, { a ->
            emitter.onNext(a)
            emitter.onComplete()
          })
        }
      }, mode).k()

    fun <A> asyncF(fa: FlowableKProcF<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create({ emitter: FlowableEmitter<A> ->
        val dispose = fa { either: Either<Throwable, A> ->
          either.fold({ e ->
            emitter.tryOnError(e)
          }, { a ->
            emitter.onNext(a)
            emitter.onComplete()
          })
        }.fix().flowable.subscribe({}, emitter::onError)

        emitter.setCancellable { dispose.dispose() }
      }, mode).k()

    fun <A> cancelable(fa: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForFlowableK>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create<A>({ emitter ->
        val token = fa { either: Either<Throwable, A> ->
          either.fold({ e ->
            emitter.tryOnError(e)
          }, { a ->
            emitter.onNext(a)
            emitter.onComplete()
          })
        }
        emitter.setCancellable { token.value().subscribe({}, { e -> emitter.tryOnError(e) }) }
      }, mode).k()

    fun <A> cancelableF(fa: ((Either<Throwable, A>) -> Unit) -> FlowableKOf<CancelToken<ForFlowableK>>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create({ emitter: FlowableEmitter<A> ->
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

        val cancelOrToken = AtomicRefW<Either<Unit, CancelToken<ForFlowableK>>?>(null)
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
      }, mode).k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> FlowableKOf<Either<A, B>>): FlowableK<B> {
      val either = f(a).value().blockingFirst()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Flowable.just(either.b).k()
      }
    }
  }
}

fun <A, G> FlowableKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, FlowableK<A>> =
  fix().traverse(GA, ::identity)

fun <A> FlowableK<A>.handleErrorWith(function: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
  value().onErrorResumeNext { t: Throwable -> function(t).value() }.k()
