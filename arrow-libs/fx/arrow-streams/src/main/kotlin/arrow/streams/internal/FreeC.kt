package arrow.streams.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.core.NonFatal
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.orElse
import arrow.core.some
import arrow.fx.typeclasses.ExitCase
import arrow.streams.CompositeFailure
import arrow.streams.internal.FreeC.Result
import arrow.typeclasses.MonadError

// TODO temporary here until moved instances to separate module
class ForFreeC private constructor() {
  companion object
}
typealias FreeCOf<F, R> = arrow.Kind2<ForFreeC, F, R>
typealias FreeCPartialOf<F> = arrow.Kind<ForFreeC, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <F, R> FreeCOf<F, R>.fix(): FreeC<F, R> =
  this as FreeC<F, R>

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
@Suppress("StringLiteralDuplication", "TooGenericExceptionThrown")
sealed class FreeC<F, out R> : FreeCOf<F, R> {

  @Suppress("UNCHECKED_CAST")
  fun <R2> flatMap(f: (R) -> FreeCOf<F, R2>): FreeC<F, R2> = FreeC.FlatMapped(this) { free: FreeC.Result<R> ->
    when (free) {
      is Pure<*, R> -> {
        try {
          f(free.r)
        } catch (t: Throwable) {
          if (NonFatal(t)) {
            FreeC.Fail<F, R2>(t)
          } else {
            throw t
          }
        }
      }
      is Fail<*, R> -> free
      is Interrupted<*, R, *> -> free
      else -> throw AssertionError("Unreachable")
    } as FreeC<F, R2>
  }

  @Suppress("UNCHECKED_CAST")
  fun <R2> map(f: (R) -> R2): FreeC<F, R2> = FlatMapped(this) { free ->
    when (free) {
      is Pure<*, R> -> {
        try {
          FreeC.Pure<F, R2>(f(free.r))
        } catch (t: Throwable) {
          if (NonFatal(t)) {
            FreeC.Fail<F, R2>(t)
          } else {
            throw t
          }
        }
      }
      is Fail<*, R> -> free
      is Interrupted<*, R, *> -> free
      else -> throw AssertionError("Unreachable")
    } as FreeC<F, R2>
  }

  @Suppress("UNCHECKED_CAST")
  fun asHandler(e: Throwable): FreeC<F, R> = when (val view = ViewL(this.fix())) {
    is FreeC.Pure -> FreeC.Fail(e)
    is FreeC.Fail -> FreeC.Fail(CompositeFailure(view.error, e))
    is FreeC.Interrupted<F, R, *> -> FreeC.Interrupted(
      view.context,
      view.deferredError.map { t -> CompositeFailure(e, t) }.orElse { e.some() }
    )
    is ViewL.Companion.View<F, *, R> -> (view as ViewL.Companion.View<F, Any?, R>).next(FreeC.Fail<F, Any?>(e))
    else -> throw AssertionError("Unreachable BOOM!")
  }

  val viewL: ViewL<F, R>
    get() = ViewL(this)

  @Suppress("UNCHECKED_CAST")
  open fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = FreeC.defer {
    when (val viewL = viewL) {
      is Pure -> viewL.asFreeC()
      is Fail -> viewL.asFreeC()
      is Interrupted<F, R, *> -> viewL.asFreeC<G>()
      is ViewL.Companion.View<F, *, R> -> (viewL as ViewL.Companion.View<F, Any?, R>).let { (step, next) -> FlatMapped(Suspend(step).translate(f)) { e -> next(e).translate(f) } }
      else -> throw AssertionError("Unreachable BOOM!")
    }
  }

  companion object {

    fun <F> unit(): FreeC<F, Unit> = FreeC.just(Unit)

    fun <F, R> just(r: R): FreeC<F, R> = FreeC.Pure(r)

    fun <F, A> liftF(f: Kind<F, A>): FreeC<F, A> = Suspend(f)

    fun <F, R> raiseError(error: Throwable): FreeC<F, R> = FreeC.Fail(error)

    fun <F, A, X> interrupted(interruptContext: X, failure: Option<Throwable>): FreeC<F, A> =
      FreeC.Interrupted(interruptContext, failure)

    fun <F, R> defer(fr: () -> FreeCOf<F, R>): FreeC<F, R> =
      FreeC.Pure<F, Unit>(Unit).flatMap { fr() }

    fun <F, R> pureContinuation(): (Result<R>) -> FreeC<F, R> = { it.asFreeC() }

    fun <F, A, B> tailRecM(a: A, f: (A) -> FreeC<F, Either<A, B>>): FreeC<F, B> =
      f(a).flatMap { it.fold({ l -> tailRecM(l, f) }, { r -> FreeC.just(r) }) }

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
      interrupted = { _, _ -> ExitCase.Canceled }
    )

    companion object {

      val unit: Result<Unit> = just(Unit)

      fun <A> just(a: A): Result<A> = FreeC.Pure<Any?, A>(a)

      fun <A> raiseError(error: Throwable): Result<A> = FreeC.Fail<Any?, A>(error)

      fun <A> interrupted(scopeId: Any?, failure: Option<Throwable>): Result<A> =
        FreeC.Interrupted<Any?, A, Any?>(scopeId, failure)

      internal fun <A> interrupted(scopeId: Token, failure: Option<Throwable>): Result<A> =
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
  internal data class Suspend<F, R>(val fr: Kind<F, R>) : FreeC<F, R>() {
    override fun <G> translate(f: FunctionK<F, G>): FreeC<G, R> = try {
      Suspend(f(fr))
    } catch (t: Throwable) {
      if (NonFatal(t)) {
        FreeC.Fail(t)
      } else {
        throw t
      }
    }
  }

  @PublishedApi
  internal data class FlatMapped<F, X, out R>(val fx: FreeC<F, X>, val f: (Result<X>) -> FreeCOf<F, R>) : FreeC<F, R>()

  override fun toString(): String = "FreeC(...) : toString is not stack-safe"
}

fun <F, A> A.freeC(): FreeC<F, A> = FreeC.just(this)

/**
 * Transform both the context [F] and value [A].
 */
fun <F, G, A, B> FreeCOf<F, A>.transform(f: (A) -> B, fs: FunctionK<F, G>): FreeC<G, B> =
  this.fix().map(f).translate(fs)

/**
 * Given a function [ff] in the context of [FreeC], applies the function.
 */
fun <F, A, B> FreeCOf<F, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
  fix().flatMap { a -> ff.fix().map { f -> f(a) } }

/**
 * Transform [FreeC] while being able to inspect the [Result] type.
 */
fun <F, R, R2> FreeCOf<F, R>.transformWith(f: (FreeC.Result<R>) -> FreeC<F, R2>): FreeC<F, R2> = FreeC.FlatMapped(this.fix()) { r ->
  try {
    f(r)
  } catch (t: Throwable) {
    if (NonFatal(t)) {
      FreeC.Fail<F, R2>(t)
    } else {
      throw t
    }
  }
}

/**
 * Handle any error, potentially recovering from it, by mapping it to a [FreeCOf] value by [h].
 */
@Suppress("UNCHECKED_CAST")
fun <F, R> FreeCOf<F, R>.handleErrorWith(h: (Throwable) -> FreeCOf<F, R>): FreeC<F, R> =
  FreeC.FlatMapped(this.fix()) { free ->
    when (free) {
      is FreeC.Pure<*, R> -> free
      is FreeC.Fail<*, R> ->
        try {
          h(free.error)
        } catch (t: Throwable) {
          if (NonFatal(t)) {
            FreeC.Fail<F, R>(t)
          } else {
            throw t
          }
        }
      is FreeC.Interrupted<*, R, *> -> free
      else -> throw AssertionError("Unreachable BOOM!")
    } as FreeC<F, R>
  }

/**
 * Runs a [FreeC] structure with [MonadError] in context of [F].
 *
 * @return [None] indicates that the [FreeC] was [FreeC.Interrupted].
 */
@Suppress("UNCHECKED_CAST", "TooGenericExceptionThrown")
fun <F, R> FreeCOf<F, R>.run(ME: MonadError<F, Throwable>): Kind<F, Option<R>> = when (val viewL = fix().viewL) {
  is FreeC.Pure -> ME.just(Some(viewL.r))
  is FreeC.Fail -> ME.raiseError(viewL.error)
  is FreeC.Interrupted<F, R, *> -> viewL.deferredError.fold({ ME.just(None) }, { e -> ME.raiseError(e) })
  is ViewL.Companion.View<F, *, R> -> (viewL as ViewL.Companion.View<F, Any?, R>).let { (step, next) ->
    ME.run {
      step.attempt().flatMap { e ->
        next(Result.fromEither(e)).run(ME)
      }
    }
  }
  else -> throw RuntimeException("Unreachable BOOM!")
}

/**
 * Catamorphism for `FreeC`.
 *
 * Run to completion, mapping the suspension with the given
 * transformation at each step and accumulating into the monad `M`.
 *
 * This method uses `tailRecM` to provide stack-safety.
 */
@Suppress("UNCHECKED_CAST")
fun <M, S, A> FreeCOf<S, A>.foldMap(f: FunctionK<S, M>, MM: MonadError<M, Throwable>): Kind<M, Option<A>> = MM.tailRecM(this@foldMap) {
  val x = it.fix().step()
  when (x) {
    is FreeC.Pure<S, A> -> MM.just(Either.Right(Some(x.r)))
    is FreeC.Fail -> MM.raiseError(x.error)
    is FreeC.Interrupted<S, A, *> -> x.deferredError.fold({ MM.just(Either.Right(None)) }, { error -> MM.raiseError(error) })
    is FreeC.Suspend<S, A> -> MM.run { f(x.fr).map { a -> Either.Right(Some(a)) } }
    is FreeC.FlatMapped<S, *, A> -> {
      val g = (x.f as (Result<A>) -> FreeC<S, A>)
      val c = x.fx as FreeC<S, A>
      val folded = c.foldMap(f, MM)
      MM.run {
        folded.map { cc ->
          cc.fold({ Right(None) }, // this means that the `FreeC` instance was interrupted.
            { a -> Either.Left(g(Result.just(a))) })
        }
      }
    }
  }
}

/** Takes one evaluation step in the Free monad, re-associating left-nested binds in the process. */
@Suppress("UNCHECKED_CAST")
tailrec fun <S, A> FreeC<S, A>.step(): FreeC<S, A> =
  if (this is FreeC.FlatMapped<S, *, A> && this.fx is FreeC.FlatMapped<S, *, *>) {
    val g = this.f as (Result<A>) -> FreeC<S, A>
    val c = this.fx.fx as FreeC<S, A>
    val f = this.fx.f as (Result<A>) -> FreeC<S, A>

    // We use `FlatMapped` here instead of `flatMap` because it needs to execute for `Result<A>` and not just `A`.
    FreeC.FlatMapped(c) { cc ->
      FreeC.FlatMapped(f(cc)) { rr ->
        g(rr)
      }
    }.step()
  } else if (this is FreeC.FlatMapped<S, *, A> && this.fx is FreeC.Result<*>) {
    val r = this.fx as FreeC.Result<A>
    val f = this.f as (Result<A>) -> FreeC<S, A>
    f(r).step()
  } else {
    this
  }

/**
 * Applies the given function [f] if this is a [FreeC.Fail], otherwise returns itself.
 * This is like [flatMap] for the error-side.
 */
fun <R> FreeC.Result<R>.recoverWith(f: (Throwable) -> Result<R>): Result<R> = when (this) {
  is FreeC.Fail<*, R> ->
    try {
      f(error)
    } catch (t: Throwable) {
      if (NonFatal(t)) {
        Result.raiseError<R>(CompositeFailure(error, t))
      } else {
        throw t
      }
    }
  else -> this
}

fun <F, A, B> FreeCOf<F, A>.bracketCase(use: (A) -> FreeCOf<F, B>, release: (A, ExitCase<Throwable>) -> FreeCOf<F, Unit>): FreeC<F, B> =
  fix().flatMap { a ->
    val used: FreeC<F, B> = try {
      use(a).fix()
    } catch (t: Throwable) {
      if (NonFatal(t)) {
        FreeC.Fail(t)
      } else {
        throw t
      }
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
      is FreeC.Suspend -> View(free.fr, FreeC.pureContinuation())
      is FreeC.Pure -> free
      is FreeC.Fail -> free
      is FreeC.Interrupted<F, R, *> -> free as FreeC.Interrupted<F, R, Any?>
      is FreeC.FlatMapped<F, *, R> -> {
        val fx: FreeC<F, Any?> = free.fx
        val f = free.f as (Result<R>) -> FreeC<F, R>
        when (fx) {
          is FreeC.Pure -> mk(f(fx as Result<R>))
          is FreeC.Fail -> mk(f(fx as Result<R>))
          is FreeC.Interrupted<F, *, *> -> mk(f(fx as Result<R>))
          is FreeC.Suspend -> View(fx.fr as Kind<F, R>, f)
          is FreeC.FlatMapped<F, *, *> -> {
            val w = fx.fx
            val g = fx.f as (Result<Any?>) -> FreeC<F, R>
            mk(FreeC.FlatMapped(w) { e: FreeC.Result<Any?> -> FreeC.FlatMapped(g(e), f) })
          }
        }
      }
    }
  }
}

// Wacky emulated sealed trait... :/
@Suppress("UNCHECKED_CAST")
inline fun <F, R, A> FreeC<F, R>.fold(
  pure: (R) -> A,
  fail: (Throwable) -> A,
  interrupted: (Any?, Option<Throwable>) -> A,
  eval: (Kind<F, R>) -> A,
  bind: (FreeC<F, Any?>, (Result<Any?>) -> FreeCOf<F, R>) -> A
): A = when (this) {
  is FreeC.Suspend -> eval(this.fr)
  is FreeC.Pure -> pure(this.r)
  is FreeC.Fail -> fail(this.error)
  is FreeC.Interrupted<F, R, *> -> interrupted(this.context, this.deferredError)
  is FreeC.FlatMapped<F, *, R> -> bind(this.fx, this.f as (Result<Any?>) -> FreeC<F, R>)
}

@Suppress("UNCHECKED_CAST")
inline fun <R, A> FreeC.Result<R>.fold(
  pure: (R) -> A,
  fail: (Throwable) -> A,
  interrupted: (Any?, Option<Throwable>) -> A
): A = when (this) {
  is FreeC.Pure<*, R> -> pure((this as FreeC.Pure<Any?, R>).r)
  is FreeC.Fail<*, R> -> fail((this as FreeC.Fail<Any?, R>).error)
  is FreeC.Interrupted<*, R, *> -> (this as FreeC.Interrupted<Any?, R, Any?>).let { interrupted(it.context, it.deferredError) }
  else -> throw AssertionError("Unreachable")
}

@Suppress("UNCHECKED_CAST", "TooGenericExceptionThrown")
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
