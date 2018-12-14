package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.CoroutineContextReactorScheduler.asScheduler
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.higherkind
import arrow.typeclasses.Applicative
import reactor.core.publisher.Flux
import kotlin.coroutines.CoroutineContext

fun <A> Flux<A>.k(): FluxK<A> = FluxK(this)

fun <A> FluxKOf<A>.value(): Flux<A> =
  this.fix().flux

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@higherkind
data class FluxK<A>(val flux: Flux<A>) : FluxKOf<A>, FluxKKindedJ<A> {
  fun <B> map(f: (A) -> B): FluxK<B> =
    flux.map(f).k()

  fun <B> ap(fa: FluxKOf<(A) -> B>): FluxK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    flux.flatMap { f(it).fix().flux }.k()

  fun <B> bracketCase(use: (A) -> FluxKOf<B>, release: (A, ExitCase<Throwable>) -> FluxKOf<Unit>): FluxK<B> =
    flatMap { a ->
      use(a).value()
        .doOnError { release(a, ExitCase.Error(it)) }
        .doOnCancel { release(a, ExitCase.Cancelled) }
        .doOnComplete { release(a, ExitCase.Completed) }
        .k()
    }

  fun <B> concatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    flux.concatMap { f(it).fix().flux }.k()

  fun <B> switchMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    flux.switchMap { f(it).fix().flux }.k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = flux.reduce(b, f).block()

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: FluxK<A>): Eval<B> = when {
      fa_p.flux.hasElements().map { !it }.block() -> lb
      else -> f(fa_p.flux.blockFirst(), Eval.defer { loop(fa_p.flux.skip(1).k()) })
    }

    return Eval.defer { loop(this) }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FluxK<B>> =
    foldRight(Eval.always { GA.just(Flux.empty<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { Flux.concat(Flux.just<B>(it.a), it.b.flux).k() } }
    }.value()

  fun handleErrorWith(function: (Throwable) -> FluxK<A>): FluxK<A> =
    this.fix().flux.onErrorResume { t: Throwable -> function(t).flux }.k()

  fun continueOn(ctx: CoroutineContext): FluxK<A> =
    flux.publishOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
    flux.flatMap { cb(Right(it)).value() }.onErrorResume { cb(Left(it)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Disposable> =
    Flux.defer {
      val disposable: reactor.core.Disposable = runAsync(cb).value().subscribe()
      val dispose: Disposable = { disposable.dispose() }
      Flux.just(dispose)
    }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is FluxK<*> -> this.flux == other.flux
      is Flux<*> -> this.flux == other
      else -> false
    }

  override fun hashCode(): Int = flux.hashCode()

  companion object {
    fun <A> just(a: A): FluxK<A> =
      Flux.just(a).k()

    fun <A> raiseError(t: Throwable): FluxK<A> =
      Flux.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): FluxK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
      Flux.defer { fa().value() }.k()

    /**
     * Creates a [FluxK] that'll run [FluxKProc].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.FluxK
     * import arrow.effects.FluxKConnection
     * import arrow.effects.value
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = FluxK.async { conn: FluxKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(FluxK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: FluxKProc<A>): FluxK<A> =
      Flux.create<A> { sink ->
        val conn = FluxKConnection()
        sink.onCancel { conn.cancel().value().subscribe() }

        fa(conn) { callback: Either<Throwable, A> ->
          callback.fold({
            sink.error(it)
          }, {
            sink.next(it)
            sink.complete()
          })
        }
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> FluxKOf<Either<A, B>>): FluxK<B> {
      val either = f(a).value().blockFirst()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Flux.just(either.b).k()
      }
    }

  }
}

fun <A, G> FluxKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, FluxK<A>> =
  fix().traverse(GA, ::identity)

typealias FluxKConnection = KindConnection<ForFluxK>
typealias FluxKProc<A> = (FluxKConnection, (Either<Throwable, A>) -> Unit) -> Unit

/**
 * Connection for [FluxK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see FluxK.async
 */
@Suppress("FunctionName")
fun FluxKConnection(dummy: Unit = Unit): KindConnection<ForFluxK> = KindConnection(object : MonadDefer<ForFluxK> {
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
    FluxK.defer(fa)

  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)

  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> FluxKOf<Either<A, B>>): FluxK<B> =
    FluxK.tailRecM(a, f)

  override fun <A, B> FluxKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FluxKOf<Unit>, use: (A) -> FluxKOf<B>): FluxK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe({}, {}) }