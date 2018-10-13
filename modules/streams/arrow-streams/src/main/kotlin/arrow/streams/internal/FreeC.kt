package arrow.streams.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.core.Option
import arrow.higherkind
import arrow.streams.CompositeFailure

internal typealias Result<F, R> = Either2<FreeC.Pure<F, R>, FreeC.Fail<F, R>, FreeC.Interrupted<F, R, Any?>>

internal fun <F, R> pureContinuation(): (Result<F, R>) -> FreeC<F, R> = {
  when (it) {
    is Either2.Left -> it.l
    is Either2.Middle -> it.m
    is Either2.Right -> it.r
  }
}

internal sealed class Either2<L, M, R> {  //This is used to emulate the nested `sealed trait Result<X>` in FS2
  data class Left<L>(val l: L) : Either2<L, Nothing, Nothing>()
  data class Middle<M>(val m: M) : Either2<Nothing, M, Nothing>()
  data class Right<R>(val r: R) : Either2<Nothing, Nothing, R>()
}

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
 *
 *
 */
@higherkind internal sealed class FreeC<F, out R> : FreeCOf<F, R> {

  fun <R2> flatMap(f: (R) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.Bind(
    this
  ) { e ->
    when (e) {
      is Either2.Left -> try {
        f(e.l.r)
      } catch (t: Throwable) {
        FreeC.Fail<F, R2>(t)
      }
      is Either2.Middle -> FreeC.Fail(e.m.error)
      is Either2.Right -> FreeC.Interrupted(e.r.context, e.r.deferredError)
    }
  }

  fun <R2> map(f: (R) -> R2): FreeC<F, R2> = Bind(this) { r ->
    when (r) {
      is Either2.Left -> try {
        FreeC.Pure<F, R2>(f(r.l.r)) as FreeC<F, R2>
      } catch (t: Throwable) {
        FreeC.Fail<F, R2>(t) as FreeC<F, R2>
      }
      is Either2.Middle -> FreeC.Fail<F, R2>(r.m.error) as FreeC<F, R2>
      is Either2.Right -> FreeC.Interrupted<F, R2, Any?>(r.r.context, r.r.deferredError) as FreeC<F, R2>
    }
  }

  open fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = TODO()/*FreeC.suspend {
    viewL match {
      case ViewL.View(fx, k) =>
      Bind(Eval(fx).translate(f), (e: Result[Any]) => k(e).translate(f))
      case r @ Result.Pure(_)           => r.asFreeC[G]
      case r @ Result.Fail(_)           => r.asFreeC[G]
      case r @ Result.Interrupted(_, _) => r.asFreeC[G]
    }
  }*/

  companion object {
    fun <F> unit(): FreeC<F, Unit> = pure(Unit)
    fun <F, A> eval(f: Kind<F, A>): FreeC<F, A> = Eval(f)
    fun <F, R> pure(r: R): FreeC<F, R> = FreeC.Pure(r)
    fun <F, R> raiseError(error: Throwable): FreeC<F, R> = FreeC.Fail(error)

    fun <F, A, X> interrupted(interruptContext: X, failure: Option<Throwable>): FreeC<F, A> =
      FreeC.Interrupted(interruptContext, failure)

    fun <F, A> interrupted(scopeId: Token, failure: Option<Throwable>): FreeC<F, A> =
      FreeC.Interrupted(scopeId, failure)

    fun <F, R> fromEither(either: Either<Throwable, R>): FreeC<F, R> =
      either.fold({ FreeC.Fail(it) }, { FreeC.Pure(it) })

    fun <F, R> suspend(fr: () -> FreeC<F, R>): FreeC<F, R> =
      FreeC.Pure<F, Unit>(Unit).flatMap { _ -> fr() }
  }

  data class Pure<F, R>(val r: R) : FreeC<F, R>(), ViewL<F, R> {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = f(this as Kind<F, R>) as FreeC<G, R>
  }

  data class Fail<F, R>(val error: Throwable) : FreeC<F, R>(), ViewL<F, R> {
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
  data class Interrupted<F, R, X>(val context: X, val deferredError: Option<Throwable>) : FreeC<F, R>(), ViewL<F, R> {
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

  data class Bind<F, X, R>(val fx: FreeC<F, X>, val f: (Result<F, X>) -> FreeC<F, R>) : FreeC<F, R>()

}

internal fun <F, R, R2> FreeC<F, R>.transformWith(f: (Result<F, R>) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.Bind(this) { r ->
  try {
    f(r)
  } catch (t: Throwable) {
    FreeC.Fail<F, R2>(t)
  }
}

internal fun <F, R> FreeCOf<F, R>.recoverWith(f: (Throwable) -> FreeCOf<F, R>): FreeC<F, R> = when (this) {
  is FreeC.Fail -> try {
    f(error).fix()
  } catch (t: Throwable) {
    FreeC.raiseError<F, R>(CompositeFailure(error, t))
  }
  else -> this.fix()
}

internal interface ViewL<F, out R>
/** unrolled view of FreeC `bind` structure **/
internal data class View<F, X, R>(val step: Kind<F, X>, val next: (Result<F, X>) -> FreeC<F, R>) : ViewL<F, R> {

  private fun <F, R> mk(free: FreeC<F, R>): ViewL<F, R> = when (free) {
    is FreeC.Eval -> View<F, Any?, R>(free.fr) { pureContinuation<F, R>()(it as Result<F, R>) }
    is FreeC.Bind<F, *, R> -> when(free.fx) {
      is FreeC.Eval -> View(free.fx.fr, free.f)
      is FreeC.Bind -> TODO("free.f is typed to (Nothing) -> Free<F,R> instead of (Any?) -> Free<F, R>")
      is FreeC.Pure -> TODO("free.f is typed to (Nothing) -> Free<F,R> instead of (Any?) -> Free<F, R>")
      is FreeC.Fail -> TODO("free.f is typed to (Nothing) -> Free<F,R> instead of (Any?) -> Free<F, R>")
      is FreeC.Interrupted -> TODO("free.f is typed to (Nothing) -> Free<F,R> instead of (Any?) -> Free<F, R>")
    }
    is FreeC.Pure -> TODO()
    is FreeC.Fail -> TODO()
    is FreeC.Interrupted -> TODO()
  }

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

}


internal typealias FreeCOf<F, R> = arrow.Kind2<ForFreeC, F, R>
internal typealias FreeCPartialOf<F> = arrow.Kind<ForFreeC, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
internal inline fun <F, R> FreeCOf<F, R>.fix(): FreeC<F, R> = this as FreeC<F, R>

internal open class ForFreeC internal constructor() {
  companion object
}
