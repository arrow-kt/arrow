package arrow.streams.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.core.Option
import arrow.effects.ExitCase
import arrow.higherkind
import arrow.streams.CompositeFailure


/**
 * Free Monad with Catch (and Interruption).
 *
 * [[FreeC]] provides mechanism for ensuring stack safety and capturing any exceptions that may arise during computation.
 *
 * Furthermore, it may capture Interruption of the evaluation, although [[FreeC]] itself does not have any
 * interruptible behaviour per se.
 *
 * Interruption cause may be captured in [[FreeC.Result.Interrupted]] and allows user to pass along any information relevant
 * to interpreter.
 *
 * Typically the [[FreeC]] user provides interpretation of FreeC in form of [[ViewL]] structure, that allows to step
 * FreeC via series of Results ([[Result.Pure]], [[Result.Fail]] and [[Result.Interrupted]]) and FreeC step ([[ViewL.View]])
 */
@higherkind internal sealed class FreeC<F, out R> : FreeCOf<F, R> {

  fun <R2> flatMap(f: (R) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.Bind(this) { r: FreeC.Result<R> ->
    //if (r is FreeC.Pure<F, X>) //Cannot check for instance of erassed type??? Is working in arrow.free
    r.fold(
      pure = {
        catchNonFatal({
          f(it.r)
        }, { t ->
          FreeC.Fail(t)
        })
      },
      fail = { FreeC.Fail(it.error) },
      interrupted = { FreeC.Interrupted(it.context, it.deferredError) }
    )
  }

  fun <R2> map(f: (R) -> R2): FreeC<F, R2> = Bind(this) { r ->
    r.fold<R, FreeC<F, R2>>(
      pure = {
        catchNonFatal({
          FreeC.Pure(f(it.r))
        }, { t ->
          FreeC.Fail(t)
        })
      },
      fail = { FreeC.Fail(it.error) },
      interrupted = { FreeC.Interrupted(it.context, it.deferredError) }
    )
  }

  open fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = TODO("""/*FreeC.suspend {
    viewL match {
      case ViewL.View(fx, k) =>
      Bind(Eval(fx).translate(f), (e: Result[Any]) => k(e).translate(f))
      case r @ Result.Pure(_)           => r.asFreeC[G]
      case r @ Result.Fail(_)           => r.asFreeC[G]
      case r @ Result.Interrupted(_, _) => r.asFreeC[G]
    }
  }*/""")

  companion object {

    fun <F, R> pure(r: R): FreeC<F, R> = FreeC.Pure(r)

    fun <F, A> eval(f: Kind<F, A>): FreeC<F, A> = Eval(f)

    fun <F, R> raiseError(error: Throwable): FreeC<F, R> = FreeC.Fail(error)

    fun <F, A, X> interrupted(interruptContext: X, failure: Option<Throwable>): FreeC<F, A> =
      FreeC.Interrupted(interruptContext, failure)

    fun <F, R> suspend(fr: () -> FreeC<F, R>): FreeC<F, R> =
      FreeC.Pure<F, Unit>(Unit).flatMap { _ -> fr() }

    fun <F, R> pureContinuation(): (Result<R>) -> FreeC<F, R> = { it.asFreeC() }

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
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = f(this as Kind<F, R>) as FreeC<G, R>
  }

  data class Fail<F, R>(val error: Throwable) : FreeC<F, R>(), Result<R>, ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = f(this as Kind<F, R>) as FreeC<G, R>
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
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = f(this as Kind<F, R>) as FreeC<G, R>
    override fun toString(): String = "FreeC.Interrupted($context, ${deferredError.map { it.message }})"
  }

  data class Eval<F, R>(val fr: Kind<F, R>) : FreeC<F, R>() {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = try {
      Eval(f(fr))
    } catch (t: Throwable) {
      FreeC.Fail(t)
    }
  }

  data class Bind<F, X, R>(val fx: FreeC<F, X>, val f: (Result<X>) -> FreeC<F, R>) : FreeC<F, R>()

}

internal fun <F, R, R2> FreeCOf<F, R>.transformWith(f: (FreeC.Result<R>) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.Bind(this.fix()) { r ->
  catchNonFatal({
    f(r)
  }, { t ->
    FreeC.Fail(t)
  })
}

internal fun <F, R> FreeCOf<F, R>.handleErrorWith(h: (Throwable) -> FreeC<F, R>): FreeC<F, R> = FreeC.Bind(this.fix()) { r ->
  r.fold(
    fail = { t ->
      catchNonFatal({
        h(t.error)
      }, { tt ->
        FreeC.Fail(tt) //Shouldn't this be a CompositeFailure??? https://github.com/functional-streams-for-scala/fs2/blob/7146253402a32181ebbece60a9745de0756bb3c1/core/shared/src/main/scala/fs2/internal/FreeC.scala#L57
      })
    },
    pure = { it.asFreeC() },
    interrupted = { it.asFreeC() })
}

internal fun <F, R> FreeCOf<F, R>.recoverWith(f: (Throwable) -> FreeCOf<F, R>): FreeC<F, R> = when (this) {
  is FreeC.Fail -> try {
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

    //  @tailrec
    //  private def mk[F[_], R](free: FreeC[F, R]): ViewL[F, R] =
    //  free match {
    //    case Eval(fx) => View(fx, pureContinuation[F, R])
    //    case b: FreeC.Bind[F, y, R] =>
    //    b.fx match {
    //      case Result(r)  => mk(b.f(r))
    //      case Eval(fr)   => ViewL.View(fr, b.f)
    //      case Bind(w, g) => mk(Bind(w, (e: Result[Any]) => Bind(g(e), b.f)))
    //    }
    //    case r @ Result.Pure(_)           => r
    //    case r @ Result.Fail(_)           => r
    //    case r @ Result.Interrupted(_, _) => r
    //  }
    fun <F, R> mk(free: FreeC<F, R>): ViewL<F, R> {
      if(free is FreeC.Eval) {
        View(free.fr, FreeC.pureContinuation())
      } else if (free is FreeC.Bind<F, Any?, R>) {

      }

      TODO("return above if statement.")
    }
  }
}

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
  is FreeC.Pure<*, *> -> pure(this as FreeC.Pure<Any?, R>)
  is FreeC.Fail<*, *> -> fail(this as FreeC.Fail<Any?, R>)
  is FreeC.Interrupted<*, *, *> -> interrupted(this as FreeC.Interrupted<Any?, R, Any?>)
  else -> throw AssertionError("Unreachable") //Emulated sealed trait
}