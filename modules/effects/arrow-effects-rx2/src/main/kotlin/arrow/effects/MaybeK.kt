package arrow.effects

import arrow.core.*
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.higherkind
import io.reactivex.Maybe
import kotlin.coroutines.CoroutineContext

fun <A> Maybe<A>.k(): MaybeK<A> = MaybeK(this)

fun <A> MaybeKOf<A>.value(): Maybe<A> = fix().maybe

@higherkind
data class MaybeK<A>(val maybe: Maybe<A>) : MaybeKOf<A>, MaybeKKindedJ<A> {

  fun <B> map(f: (A) -> B): MaybeK<B> =
    maybe.map(f).k()

  fun <B> ap(fa: MaybeKOf<(A) -> B>): MaybeK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    maybe.flatMap { f(it).value() }.k()

  fun <B> bracketCase(use: (A) -> MaybeKOf<B>, release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>): MaybeK<B> =
    flatMap { a ->
      use(a).value()
        .doOnSuccess { release(a, ExitCase.Completed) }
        .doOnError { release(a, ExitCase.Error(it)) }
        .k()
    }

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

  fun handleErrorWith(function: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    maybe.onErrorResumeNext { t: Throwable -> function(t).value() }.k()

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
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.MaybeK
     * import arrow.effects.MaybeKConnection
     * import arrow.effects.value
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = MaybeK.async { conn: MaybeKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(MaybeK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: MaybeKProc<A>): MaybeK<A> =
      Maybe.create<A> { emitter ->
        val conn = MaybeKConnection()
        emitter.setCancellable { conn.cancel().value().subscribe() }

        fa(conn) { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onSuccess(it)
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

typealias MaybeKConnection = KindConnection<ForMaybeK>
typealias MaybeKProc<A> = (MaybeKConnection, (Either<Throwable, A>) -> Unit) -> Unit

/**
 * Connection for [MaybeK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see MaybeK.async
 */
@Suppress("FunctionName")
fun MaybeKConnection(dummy: Unit = Unit): KindConnection<ForMaybeK> = KindConnection(object : MonadDefer<ForMaybeK> {
  override fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
    MaybeK.defer(fa)

  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)

  override fun <A, B> MaybeKOf<A>.flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> =
    MaybeK.tailRecM(a, f)

  override fun <A, B> MaybeKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>, use: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe() }
