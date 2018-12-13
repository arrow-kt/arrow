package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import arrow.typeclasses.Applicative
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import kotlin.coroutines.CoroutineContext

fun <A> Flowable<A>.k(): FlowableK<A> = FlowableK(this)

fun <A> FlowableKOf<A>.value(): Flowable<A> = this.fix().flowable

@higherkind
data class FlowableK<A>(val flowable: Flowable<A>) : FlowableKOf<A>, FlowableKKindedJ<A> {

  fun <B> map(f: (A) -> B): FlowableK<B> =
    flowable.map(f).k()

  fun <B> ap(fa: FlowableKOf<(A) -> B>): FlowableK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.flatMap { f(it).fix().flowable }.k()

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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import io.reactivex.Flowable
   * import arrow.effects.*
   * import arrow.effects.typeclasses.ExitCase
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
   *         is ExitCase.Cancelled -> { /* do something */ }
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
  fun <B> bracketCase(use: (A) -> FlowableKOf<B>, release: (A, ExitCase<Throwable>) -> FlowableKOf<Unit>): FlowableK<B> =
    flatMap { a ->
      Flowable.unsafeCreate<B> { subscriber ->
        use(a).fix()
          .flatMap { b ->
            release(a, ExitCase.Completed)
              .fix().map { b }
          }.handleErrorWith { e ->
            release(a, ExitCase.Error(e))
              .fix().flatMap { FlowableK.raiseError<B>(e) }
          }.flowable.subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete) { d ->
          subscriber.onSubscribe(d.onCancel { release(a, ExitCase.Cancelled).fix().flowable.subscribe({}, subscriber::onError) })
        }
      }.k()
    }

  fun <B> concatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.concatMap { f(it).fix().flowable }.k()

  fun <B> switchMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.switchMap { f(it).fix().flowable }.k()

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

  fun handleErrorWith(function: (Throwable) -> FlowableK<A>): FlowableK<A> =
    flowable.onErrorResumeNext { t: Throwable -> function(t).flowable }.k()

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

    fun <A> async(fa: Proc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create({ emitter: FlowableEmitter<A> ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onNext(it)
            emitter.onComplete()
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
