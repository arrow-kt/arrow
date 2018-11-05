package arrow.streams.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.ExitCase
import arrow.higherkind
import arrow.streams.CompositeFailure
import arrow.streams.internal.FreeC.Result
import arrow.typeclasses.MonadError

/**
 * Free Monad with Catch (and Interruption).
 *
 * [FreeC] provides mechanism for ensuring stack safety and capturing any exceptions that may arise during computation.
 *
 * Furthermore, it may capture Interruption of the evaluation, although [FreeC] itself does not have any
 * interruptible behaviour per se.
 *
 * Interruption cause may be captured in [FreeC.Interrupted] and allows user to pass along any information relevant
 * to interpreter.
 *
 * Typically the [FreeC] user provides interpretation of FreeC in form of [ViewL] structure through the [ViewL.fold] function,
 * that allows to step FreeC via series of Results ([FreeC.Pure], [FreeC.Fail] and [FreeC.Interrupted]) and FreeC step ([ViewL.View]).
 */
@higherkind
sealed class FreeC<F, out R> : FreeCOf<F, R> {

  fun <R2> flatMap(f: (R) -> FreeCOf<F, R2>): FreeC<F, R2> = FreeC.Bind(this) { r: FreeC.Result<R> ->
    r.fold(
      pure = { rr ->
        try {
          f(rr)
        } catch (t: Throwable) {
          FreeC.Fail<F, R2>(t)
        }
      },
      fail = { error -> FreeC.Fail(error) },
      interrupted = { context, deferredError -> FreeC.Interrupted(context, deferredError) }
    )
  }

  fun <R2> map(f: (R) -> R2): FreeC<F, R2> = Bind(this) { r ->
    r.fold<R, FreeC<F, R2>>(
      pure = { r ->
        try {
          FreeC.Pure(f(r))
        } catch (t: Throwable) {
          FreeC.Fail(t)
        }
      },
      fail = { error -> FreeC.Fail(error) },
      interrupted = { context, deferredError -> FreeC.Interrupted(context, deferredError) }
    )
  }

  fun asHandler(e: Throwable): FreeC<F, R> = ViewL(this.fix()).fold(
    pure = { _ -> FreeC.Fail(e) },
    fail = { error -> FreeC.Fail(CompositeFailure(error, e)) },
    interrupted = { context, deferredError ->
      FreeC.Interrupted(
        context,
        deferredError.map { t -> CompositeFailure(e, t) }.orElse { e.some() }
      )
    },
    view = { _, next -> next(FreeC.Fail<F, Any?>(e)) }
  )

  val viewL: ViewL<F, R>
    get() = ViewL(this)

  open fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = FreeC.defer {
    viewL.fold(
      view = { step, next -> Bind(Eval(step).translate(f)) { e -> next(e).translate(f) } },
      pure = { r -> FreeC.pure<G, R>(r) },
      fail = { error -> FreeC.raiseError(error) },
      interrupted = { context, deferredError -> FreeC.interrupted(context, deferredError) }
    )
  }

  companion object {

    fun <F> unit(): FreeC<F, Unit> = FreeC.pure(Unit)

    fun <F, R> pure(r: R): FreeC<F, R> = FreeC.Pure(r)

    fun <F, A> liftF(f: Kind<F, A>): FreeC<F, A> = Eval(f)

    fun <F, R> raiseError(error: Throwable): FreeC<F, R> = FreeC.Fail(error)

    fun <F, A, X> interrupted(interruptContext: X, failure: Option<Throwable>): FreeC<F, A> =
      FreeC.Interrupted(interruptContext, failure)

    fun <F, R> defer(fr: () -> FreeCOf<F, R>): FreeC<F, R> =
      FreeC.Pure<F, Unit>(Unit).flatMap { _ -> fr() }

    fun <F, R> pureContinuation(): (Result<R>) -> FreeC<F, R> = { it.asFreeC() }

    fun <F, A, B> tailRecM(a: A, f: (A) -> FreeC<F, Either<A, B>>): FreeC<F, B> =
      f(a).flatMap { it.fold({ l -> tailRecM(l, f) }, { r -> FreeC.pure(r) }) }

    fun <F> functionKF(): FunctionK<F, FreeCPartialOf<F>> =
      object : FunctionK<F, FreeCPartialOf<F>> {
        override fun <A> invoke(fa: Kind<F, A>): FreeC<F, A> =
          liftF(fa)
      }

  }

  /**
   * Emulated sealed trait. **Never extend this interface!**
   * **Working with [FreeC.Result] must be done using [Result.fold] **
   */
  interface Result<out R> {

    @Suppress("UNCHECKED_CAST")
    fun <F> asFreeC(): FreeC<F, R> = this as FreeC<F, R>

    fun asExitCase(): ExitCase<Throwable> = this.fold(
      pure = { ExitCase.Completed },
      fail = { t -> ExitCase.Error(t) },
      interrupted = { _, _ -> ExitCase.Cancelled }
    )

    companion object {

      val unit: Result<Unit> = pure(Unit)

      fun <A> pure(a: A): Result<A> = FreeC.Pure<Any?, A>(a)

      fun <A> raiseError(error: Throwable): Result<A> = FreeC.Fail<Any?, A>(error)

      fun <A> interrupted(scopeId: Token, failure: Option<Throwable>): Result<A> =
        FreeC.Interrupted<Any?, A, Token>(scopeId, failure)

      fun <A> fromEither(either: Either<Throwable, A>): Result<A> =
        either.fold({ FreeC.Fail<Any?, A>(it) }, { FreeC.Pure<Any?, A>(it) })

    }

  }

  @PublishedApi internal data class Pure<F, R>(val r: R) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = this.asFreeC()
  }

  @PublishedApi internal data class Fail<F, R>(val error: Throwable) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = this.asFreeC()
  }

  /**
   * Signals that FreeC evaluation was interrupted.
   *
   * @param context Any user specific context that needs to be captured during interruption
   *                for eventual resume of the operation.
   *
   * @param deferredError Any errors, accumulated during resume of the interruption.
   *                      Instead throwing errors immediately during interruption,
   *                      signalling of the errors may be deferred until the Interruption resumes.
   */
  @PublishedApi
  internal data class Interrupted<F, R, X>(val context: X, val deferredError: Option<Throwable>) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = this.asFreeC()
    override fun toString(): String = "FreeC.Interrupted($context, ${deferredError.map { it.message }})"
  }

  @PublishedApi
  internal data class Eval<F, R>(val fr: Kind<F, R>) : FreeC<F, R>() {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = try {
      Eval(f(fr))
    } catch (t: Throwable) {
      FreeC.Fail(t)
    }
  }

  @PublishedApi
  internal data class Bind<F, X, R>(val fx: FreeC<F, X>, val f: (Result<X>) -> FreeCOf<F, R>) : FreeC<F, R>()

  override fun toString(): String = "FreeC(...) : toString is not stack-safe"
}

fun <F, A> A.freeC(): FreeC<F, A> = FreeC.pure(this)

/**
 * Transform both the context [F] and value [A].
 */
fun <F, G, A, B> FreeCOf<F, A>.transform(f: (A) -> B, fs: FunctionK<F, G>): FreeC<G, B> =
  this.fix().map(f).translate(fs)

/**
 * Given a function [ff] in the context of [FreeC], applies the function.
 */
fun <F, A, B> FreeCOf<F, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
  ff.fix().flatMap { f ->
    this@ap.fix().map(f)
  }

/**
 * Transform [FreeC] while being able to inspect the [Result] type.
 */
fun <F, R, R2> FreeCOf<F, R>.transformWith(f: (FreeC.Result<R>) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.Bind(this.fix()) { r ->
  try {
    f(r)
  } catch (t: Throwable) {
    FreeC.Fail<F, R2>(t)
  }
}

/**
 * Handle any error, potentially recovering from it, by mapping it to a [FreeCOf] value by [h].
 */
fun <F, R> FreeCOf<F, R>.handleErrorWith(h: (Throwable) -> FreeCOf<F, R>): FreeC<F, R> = FreeC.Bind(this.fix()) { r ->
  r.fold(
    fail = { t ->
      try {
        h(t)
      }
      //Should this be a CompositeFailure?
      // https://github.com/functional-streams-for-scala/fs2/blob/7146253402a32181ebbece60a9745de0756bb3c1/core/shared/src/main/scala/fs2/internal/FreeC.scala#L57
      catch (tt: Throwable) {
        FreeC.Fail<F, R>(tt)
      }
    },
    pure = { FreeC.pure(it) },
    interrupted = { context, error -> FreeC.interrupted(context, error) })
}


/**
 * Runs a [FreeC] structure with [MonadError] in context of [F].
 *
 * @return [None] indicates that the [FreeC] was [FreeC.Interrupted].
 */
fun <F, R> FreeCOf<F, R>.run(ME: MonadError<F, Throwable>): Kind<F, Option<R>> = fix().viewL.fold(
  pure = { ME.just(Some(it)) },
  fail = { ME.raiseError(it) },
  interrupted = { _, deferredError -> deferredError.fold({ ME.just(None) }, { e -> ME.raiseError(e) }) },
  view = { step, next ->
    ME.run {
      step.attempt().flatMap { e ->
        next(Result.fromEither(e)).run(ME)
      }
    }
  }
)

/**
 * Applies the given function [f] if this is a [FreeC.Fail], otherwise returns itself.
 * This is like [flatMap] for the error-side.
 */
fun <R> FreeC.Result<R>.recoverWith(f: (Throwable) -> Result<R>): Result<R> = when (this) {
  is FreeC.Fail<*, R> ->
    try {
      f(error)
    } catch (t: Throwable) {
      Result.raiseError<R>(CompositeFailure(error, t))
    }
  else -> this
}

fun <F, A, B> FreeCOf<F, A>.bracketCase(use: (A) -> FreeC<F, B>, release: (A, ExitCase<Throwable>) -> FreeC<F, Unit>): FreeC<F, B> =
  fix().flatMap { a ->
    val used: FreeC<F, B> = try {
      use(a)
    } catch (t: Throwable) {
      FreeC.Fail(t)
    }
    used.transformWith { result ->
      release(a, result.asExitCase()).transformWith<F, Unit, B> { r2 ->
        when (r2) {
          is FreeC.Fail<*, *> -> result.recoverWith { t ->
            FreeC.Fail<F, B>(CompositeFailure(t, r2.error))
          }.asFreeC()
          else -> result.asFreeC()
        }
      }
    }
  }

/**
 * Emulated sealed trait. **Never extend this interface!**
 * Working with `ViewL` must be done using [ViewL.fold]
 */
interface ViewL<F, out R> {
  companion object {

    /**
     * Unrolled view of FreeC `bind` structure
     */
    @PublishedApi
    internal data class View<F, X, R>(val step: Kind<F, X>, val next: (FreeC.Result<X>) -> FreeC<F, R>) : ViewL<F, R>

    operator fun <F, R> invoke(free: FreeC<F, R>): ViewL<F, R> = mk(free)

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <F, R> mk(free: FreeC<F, R>): ViewL<F, R> = when (free) {
      is FreeC.Eval -> View(free.fr, FreeC.pureContinuation())
      is FreeC.Pure -> free
      is FreeC.Fail -> free
      is FreeC.Interrupted<F, R, *> -> free as FreeC.Interrupted<F, R, Any?>
      is FreeC.Bind<F, *, R> -> {
        val fx: FreeC<F, Any?> = free.fx
        val f = free.f as (Result<R>) -> FreeC<F, R>
        when (fx) {
          is FreeC.Pure -> mk(f(fx as Result<R>))
          is FreeC.Fail -> mk(f(fx as Result<R>))
          is FreeC.Interrupted<F, *, *> -> mk(f(fx as Result<R>))
          is FreeC.Eval -> View(fx.fr as Kind<F, R>, f)
          is FreeC.Bind<F, *, *> -> {
            val w = fx.fx
            val g = fx.f as (Result<Any?>) -> FreeC<F, R>
            mk(FreeC.Bind(w) { e: FreeC.Result<Any?> -> FreeC.Bind(g(e), f) })
          }
        }
      }
    }

  }

}

//Wacky emulated sealed trait... :/
@Suppress("UNCHECKED_CAST")
inline fun <F, R, A> FreeC<F, R>.fold(
  pure: (R) -> A,
  fail: (Throwable) -> A,
  interrupted: (Any?, Option<Throwable>) -> A,
  eval: (Kind<F, R>) -> A,
  bind: (FreeC<F, Any?>, (Result<Any?>) -> FreeCOf<F, R>) -> A
): A = when (this) {
  is FreeC.Eval -> eval(this.fr)
  is FreeC.Pure -> pure(this.r)
  is FreeC.Fail -> fail(this.error)
  is FreeC.Interrupted<F, R, *> -> interrupted(this.context, this.deferredError)
  is FreeC.Bind<F, *, R> -> bind(this.fx, this.f as (Result<Any?>) -> FreeC<F, R>)
}

@Suppress("UNCHECKED_CAST", "ThrowRuntimeException")
inline fun <R, A> FreeC.Result<R>.fold(
  pure: (R) -> A,
  fail: (Throwable) -> A,
  interrupted: (Any?, Option<Throwable>) -> A
): A = when (this) {
  is FreeC.Pure<*, R> -> pure((this as FreeC.Pure<Any?, R>).r)
  is FreeC.Fail<*, R> -> fail((this as FreeC.Fail<Any?, R>).error)
  is FreeC.Interrupted<*, *, *> -> (this as FreeC.Interrupted<Any?, R, Any?>).let { interrupted(it.context, it.deferredError) }
  else -> throw AssertionError("Unreachable")
}

@Suppress("UNCHECKED_CAST", "ThrowRuntimeException")
inline fun <F, R, A> ViewL<F, R>.fold(
  pure: (R) -> A,
  fail: (Throwable) -> A,
  interrupted: (Any?, Option<Throwable>) -> A,
  view: (Kind<F, Any?>, (FreeC.Result<Any?>) -> FreeC<F, R>) -> A
): A = when (this) {
  is FreeC.Pure -> pure(this.r)
  is FreeC.Fail -> fail(this.error)
  is FreeC.Interrupted<F, R, *> -> (this as FreeC.Interrupted<F, R, Any?>).let { (context, deferredError) -> interrupted(context, deferredError) }
  is ViewL.Companion.View<F, *, R> -> (this as ViewL.Companion.View<F, Any?, R>).let { (step, next) -> view(step, next) }
  else -> throw RuntimeException("Unreachable BOOM!")
}