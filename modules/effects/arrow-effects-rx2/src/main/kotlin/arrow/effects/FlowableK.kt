package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.higherkind
import arrow.typeclasses.Applicative
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlin.coroutines.CoroutineContext

fun <A> Flowable<A>.k(): FlowableK<A> = FlowableK(this)

fun <A> FlowableKOf<A>.value(): Flowable<A> = fix().flowable

@higherkind
data class FlowableK<A>(val flowable: Flowable<A>) : FlowableKOf<A>, FlowableKKindedJ<A> {

  fun <B> map(f: (A) -> B): FlowableK<B> =
    flowable.map(f).k()

  fun <B> ap(fa: FlowableKOf<(A) -> B>): FlowableK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.flatMap { f(it).value() }.k()

  fun <B> bracketCase(use: (A) -> FlowableKOf<B>, release: (A, ExitCase<Throwable>) -> FlowableKOf<Unit>): FlowableK<B> =
    flatMap { a ->
      use(a).value()
        .doOnError { release(a, ExitCase.Error(it)) }
        .doOnCancel { release(a, ExitCase.Cancelled) }
        .doOnComplete { release(a, ExitCase.Completed) }
        .k()
    }

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

  fun handleErrorWith(function: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    flowable.onErrorResumeNext { t: Throwable -> function(t).value() }.k()

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

    /**
     * Creates a [FlowableK] that'll run [FlowableKProc].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.FlowableK
     * import arrow.effects.FlowableKConnection
     * import arrow.effects.value
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = FlowableK.async( fa= { conn: FlowableKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(FlowableK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   })
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: FlowableKProc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create<A>({ emitter ->
        val conn = FlowableKConnection()
        emitter.setCancellable { conn.cancel().value().subscribe() }

        fa(conn) { either: Either<Throwable, A> ->
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
