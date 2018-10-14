package arrow.streams.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.ExitCase
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
internal sealed class FreeC<F, out R> : FreeCOf<F, R> {

  fun <R2> flatMap(f: (R) -> FreeCOf<F, R2>): FreeC<F, R2> = FreeC.Bind(this) { r: FreeC.Result<R> ->
    r.fold(
      pure = {
        try {
          f(it.r)
        } catch (t: Throwable) {
          FreeC.Fail<F, R2>(t)
        }
      },
      fail = { FreeC.Fail(it.error) },
      interrupted = { FreeC.Interrupted(it.context, it.deferredError) }
    )
  }

  fun <R2> map(f: (R) -> R2): FreeC<F, R2> = Bind(this) { r ->
    r.fold<R, FreeC<F, R2>>(
      pure = {
        try {
          FreeC.Pure(f(it.r))
        } catch (t: Throwable) {
          FreeC.Fail(t)
        }
      },
      fail = { FreeC.Fail(it.error) },
      interrupted = { FreeC.Interrupted(it.context, it.deferredError) }
    )
  }

  fun asHandler(e: Throwable): FreeC<F, R> = ViewL(this.fix()).fold(
    pure = { FreeC.Fail(e) },
    fail = { FreeC.Fail(CompositeFailure(it.error, e)) },
    interrupted = {
      FreeC.Interrupted(
        it.context,
        it.deferredError.map { t -> CompositeFailure(e, t) }.orElse { e.some() }
      )
    },
    view = { it.next(FreeC.Fail<F, Any?>(e)) }
  )

  val viewL: ViewL<F, R>
    get() = ViewL(this)

  open fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = FreeC.defer {
    viewL.fold(
      view = { Bind(Eval(it.step).translate(f)) { e -> it.next(e).translate(f) } },
      pure = { it.asFreeC<G>() },
      fail = { it.asFreeC() },
      interrupted = { it.asFreeC() }
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

  internal interface Result<out R> {

    fun <F> asFreeC(): FreeC<F, R> = this as FreeC<F, R>

    fun asExitCase(): ExitCase<Throwable> = this.fold(
      pure = { ExitCase.Completed },
      fail = { t -> ExitCase.Error(t.error) },
      interrupted = { ExitCase.Cancelled }
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

  data class Pure<F, R>(val r: R) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = this.asFreeC()
  }

  data class Fail<F, R>(val error: Throwable) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
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
  data class Interrupted<F, R, X>(val context: X, val deferredError: Option<Throwable>) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = this.asFreeC()
    override fun toString(): String = "FreeC.Interrupted($context, ${deferredError.map { it.message }})"
  }

  data class Eval<F, R>(val fr: Kind<F, R>) : FreeC<F, R>() {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = try {
      Eval(f(fr))
    } catch (t: Throwable) {
      FreeC.Fail(t)
    }
  }

  data class Bind<F, X, R>(val fx: FreeC<F, X>, val f: (Result<X>) -> FreeCOf<F, R>) : FreeC<F, R>()

  override fun toString(): String = "FreeC(...) : toString is not stack-safe"
}

internal fun <F, G, A, B> FreeCOf<F, A>.transform(f: (A) -> B, fs: FunctionK<F, G>): FreeC<G, B> =
  this.fix().map(f).translate(fs)

internal fun <F, A, B> FreeCOf<F, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
  ff.fix().flatMap { f ->
    this@ap.fix().map(f)
  }

internal fun <F, R, R2> FreeCOf<F, R>.transformWith(f: (FreeC.Result<R>) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.Bind(this.fix()) { r ->
  try {
    f(r)
  } catch (t: Throwable) {
    FreeC.Fail<F, R2>(t)
  }
}

internal fun <F, R> FreeCOf<F, R>.handleErrorWith(h: (Throwable) -> FreeCOf<F, R>): FreeC<F, R> = FreeC.Bind(this.fix()) { r ->
  r.fold(
    fail = { t ->
      try {
        h(t.error)
      }
      //Should this be a CompositeFailure?
      // https://github.com/functional-streams-for-scala/fs2/blob/7146253402a32181ebbece60a9745de0756bb3c1/core/shared/src/main/scala/fs2/internal/FreeC.scala#L57
      catch (tt: Throwable) {
        FreeC.Fail<F, R>(tt)
      }
    },
    pure = { it.asFreeC() },
    interrupted = { it.asFreeC() })
}

internal fun <F, R> FreeCOf<F, R>.recoverWith(f: (Throwable) -> FreeCOf<F, R>): FreeC<F, R> = when (this) {
  is FreeC.Fail ->
    try {
      f(error).fix()
    } catch (t: Throwable) {
      FreeC.raiseError<F, R>(CompositeFailure(error, t))
    }
  else -> this.fix()
}

internal interface ViewL<F, out R> {
  companion object {

    /** unrolled view of FreeC `bind` structure **/
    internal data class View<F, X, R>(val step: Kind<F, X>, val next: (FreeC.Result<X>) -> FreeC<F, R>) : ViewL<F, R>

    operator fun <F, R> invoke(free: FreeC<F, R>): ViewL<F, R> = mk(free)

    private tailrec fun <F, R> mk(free: FreeC<F, R>): ViewL<F, R> = when (free) {
      is FreeC.Eval -> View(free.fr, FreeC.pureContinuation())
      is FreeC.Pure -> free as ViewL<F, R>
      is FreeC.Fail -> free as ViewL<F, R>
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

internal fun <F, A> A.freeC(): FreeC<F, A> = FreeC.pure(this)

/* InvariantOps */
// None indicates the FreeC was interrupted
internal fun <F, R> FreeCOf<F, R>.run(ME: MonadError<F, Throwable>): Kind<F, Option<R>> = fix().viewL.fold(
  pure = { ME.just(Some(it.r)) },
  fail = { ME.raiseError(it.error) },
  interrupted = { it.deferredError.fold({ ME.just(None) }, { e -> ME.raiseError(e) }) },
  view = { view ->
    ME.run {
      view.step.attempt().flatMap { e ->
        val next = view.next
        val result: Result<Any?> = Result.fromEither(e)
        next(result).run(ME)
      }
    }
  }
)

/**
 * Utils
 */
internal typealias FreeCOf<F, R> = arrow.Kind2<ForFreeC, F, R>

internal typealias FreeCPartialOf<F> = arrow.Kind<ForFreeC, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
internal inline fun <F, R> FreeCOf<F, R>.fix(): FreeC<F, R> = this as FreeC<F, R>

internal open class ForFreeC internal constructor() {
  companion object
}

//Wacky emulated sealed trait... :/
internal inline fun <R, A> FreeC.Result<R>.fold(
  pure: (FreeC.Pure<Any?, R>) -> A,
  fail: (FreeC.Fail<Any?, R>) -> A,
  interrupted: (FreeC.Interrupted<Any?, R, Any?>) -> A
): A = when (this) {
  is FreeC.Pure<*, R> -> pure(this as FreeC.Pure<Any?, R>)
  is FreeC.Fail<*, R> -> fail(this as FreeC.Fail<Any?, R>)
  is FreeC.Interrupted<*, *, *> -> interrupted(this as FreeC.Interrupted<Any?, R, Any?>)
  else -> throw AssertionError("Unreachable")
}

internal inline fun <F, R, A> ViewL<F, R>.fold(
  pure: (FreeC.Pure<F, R>) -> A,
  fail: (FreeC.Fail<F, R>) -> A,
  interrupted: (FreeC.Interrupted<F, R, Any?>) -> A,
  view: (ViewL.Companion.View<F, Any?, R>) -> A
): A = when (this) {
  is FreeC.Pure -> pure(this)
  is FreeC.Fail -> fail(this)
  is FreeC.Interrupted<F, R, *> -> interrupted(this as FreeC.Interrupted<F, R, Any?>)
  is ViewL.Companion.View<F, *, R> -> view(this as ViewL.Companion.View<F, Any?, R>)
  else -> throw RuntimeException("Unreachable BOOM!")
}
