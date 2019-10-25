package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.OptionOf
import arrow.core.TryOf
import arrow.core.extensions.list.traverse.traverse
import arrow.core.fix
import arrow.core.identity
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext

interface FxSyntax<F> : Concurrent<F, Throwable>, BindSyntax<F> {

  suspend fun <A> effectIdentity(a: A): A = a

  val NonBlocking: CoroutineContext
    get() = dispatchers().default()

  fun <A, B> CoroutineContext.parTraverse(
    effects: Iterable<Kind<F, A>>,
    f: (A) -> B
  ): Kind<F, List<B>> =
    effects.fold(emptyList<Kind<F, Fiber<F, B>>>()) { acc, fa ->
      acc + startFiber(fa.map(f))
    }.traverse(this@FxSyntax) { kind ->
      kind.flatMap { it.join() }
    }.map { it.fix() }

  fun <A> CoroutineContext.parSequence(effects: Iterable<Kind<F, A>>): Kind<F, List<A>> =
    parTraverse(effects, ::identity)

  fun <A> ensure(fa: suspend () -> A, error: () -> Throwable, predicate: (A) -> Boolean): Kind<F, A> =
    run<Monad<F>, Kind<F, A>> { fa.effect().ensure(error, predicate) }

  private fun <A> asyncOp(fb: Async<F, Throwable>.() -> Kind<F, A>): Kind<F, A> =
    run<Async<F, Throwable>, Kind<F, A>> { fb(this) }

  fun <A> CoroutineContext.effect(dummy: Unit = Unit, f: suspend () -> A): Kind<F, A> =
    asyncOp { defer(this@effect) { f.effect() } }

  fun <A> (suspend () -> A).effect(unit: Unit = Unit): Kind<F, A> =
    effect(this)

  fun <A, B> (suspend (A) -> B).effect(): (Kind<F, A>) -> Kind<F, B> = { fa ->
    fa.flatMap { a ->
      effect { this(a) }
    }
  }

  fun <A, B, C> (suspend (A, B) -> C).effect(): (Kind<F, A>, Kind<F, B>) -> Kind<F, C> = { ka, kb ->
    ka.flatMap { a ->
      kb.flatMap { b ->
        effect { this(a, b) }
      }
    }
  }

  fun <A, B, C, D> (suspend (A, B, C) -> D).effect(): (Kind<F, A>, Kind<F, B>, Kind<F, C>) -> Kind<F, D> = { ka, kb, kc ->
    ka.flatMap { a ->
      kb.flatMap { b ->
        kc.flatMap { c ->
          effect { this(a, b, c) }
        }
      }
    }
  }

  fun <A, B> (suspend (A) -> B).flatLiftM(unit: Unit = Unit): (A) -> Kind<F, B> = { a ->
    effect { this(a) }
  }

  fun <A, B, C> (suspend (A, B) -> C).flatLiftM(): (A, B) -> Kind<F, C> = { a, b ->
    effect { this(a, b) }
  }

  fun <A, B, C, D> (suspend (A, B, C) -> D).flatLiftM(): (A, B, C) -> Kind<F, D> = { a, b, c ->
    effect { this(a, b, c) }
  }

  suspend fun <A> handleError(fa: suspend () -> A, recover: suspend (Throwable) -> A): Kind<F, A> =
    run<ApplicativeError<F, Throwable>, Kind<F, A>> { fa.effect().handleErrorWith(recover.flatLiftM()) }

  suspend fun <A> OptionOf<A>.getOrRaiseError(f: () -> Throwable): Kind<F, A> =
    run<ApplicativeError<F, Throwable>, Kind<F, A>> { this@getOrRaiseError.fromOption(f) }

  suspend fun <A, B> Either<B, A>.getOrRaiseError(f: (B) -> Throwable): Kind<F, A> =
    run<ApplicativeError<F, Throwable>, Kind<F, A>> { this@getOrRaiseError.fromEither(f) }

  suspend fun <A> TryOf<A>.getOrRaiseError(f: (Throwable) -> Throwable): Kind<F, A> =
    run<ApplicativeError<F, Throwable>, Kind<F, A>> { this@getOrRaiseError.fromTry(f) }

  suspend fun <A> attempt(fa: suspend () -> A): Kind<F, Either<Throwable, A>> =
    run<ApplicativeError<F, Throwable>, Kind<F, Either<Throwable, A>>> { fa.effect().attempt() }

  private fun <A> bracketing(fb: Bracket<F, Throwable>.() -> Kind<F, A>): Kind<F, A> =
    run<Bracket<F, Throwable>, Kind<F, A>> { fb(this) }

  fun <A, B> bracketCase(
    f: suspend () -> A,
    release: suspend (A, ExitCase<Throwable>) -> Unit,
    use: suspend (A) -> B
  ): Kind<F, B> =
    bracketing { f.effect().bracketCase(release.flatLiftM(), use.flatLiftM()) }

  fun <A, B> bracket(
    f: suspend () -> A,
    release: suspend (A) -> Unit,
    use: suspend (A) -> B
  ): Kind<F, B> =
    bracketing { f.effect().bracket(release.flatLiftM(), use.flatLiftM()) }

  fun <A> uncancelable(f: suspend () -> A): Kind<F, A> =
    bracketing { f.effect().uncancelable() }

  fun <A> guarantee(
    f: suspend () -> A,
    finalizer: suspend () -> Unit
  ): Kind<F, A> =
    bracketing { f.effect().guarantee(finalizer.effect()) }

  fun <A> Kind<F, A>.guaranteeCase(
    unit: Unit = Unit,
    finalizer: suspend (ExitCase<Throwable>) -> Unit
  ): Kind<F, A> =
    bracketing { guaranteeCase(finalizer.flatLiftM()) }

  fun <A, B> Iterable<suspend () -> A>.traverse(f: suspend (A) -> B): Kind<F, List<B>> =
    effect { map { fa: suspend () -> A -> f(fa()) } }

  fun <A> Iterable<suspend () -> A>.sequence(): Kind<F, List<A>> =
    traverse(::effectIdentity)

  fun <A, B> Iterable<suspend () -> A>.flatTraverse(f: suspend (A) -> List<B>): Kind<F, List<B>> =
    effect { flatMap { f(it()) } }

  fun <A> Iterable<Iterable<suspend () -> A>>.flatSequence(): Kind<F, List<A>> =
    flatten().sequence()
}
