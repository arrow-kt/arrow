@file:Suppress("UnusedImports")

package arrow.streams.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.core.Option
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.Boolean
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.run
import arrow.streams.internal.ap as apply
import arrow.streams.internal.bracketCase as bracketC
import arrow.streams.internal.handleErrorWith as handleErrorW

@extension
@undocumented
interface FreeCFunctor<F> : Functor<FreeCPartialOf<F>> {
  override fun <A, B> FreeCOf<F, A>.map(f: (A) -> B): FreeCOf<F, B> =
    this.fix().map(f)
}

@extension
@undocumented
interface FreeCApplicative<F> : Applicative<FreeCPartialOf<F>> {
  override fun <A> just(a: A): FreeCOf<F, A> =
    FreeC.just(a)

  override fun <A, B> FreeCOf<F, A>.ap(ff: FreeCOf<F, (A) -> B>): FreeCOf<F, B> =
    apply(ff)
}

@extension
@undocumented
interface FreeCMonad<F> : Monad<FreeCPartialOf<F>> {
  override fun <A, B> FreeCOf<F, A>.flatMap(f: (A) -> FreeCOf<F, B>): FreeCOf<F, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> FreeCOf<F, Either<A, B>>): FreeCOf<F, B> =
    FreeC.tailRecM(a) { f(it).fix() }

  override fun <A> just(a: A): FreeCOf<F, A> =
    FreeC.just(a)
}

@extension
@undocumented
interface FreeCApplicativeError<F> : ApplicativeError<FreeCPartialOf<F>, Throwable> {
  override fun <A> raiseError(e: Throwable): FreeCOf<F, A> =
    FreeC.raiseError(e)

  override fun <A> FreeCOf<F, A>.handleErrorWith(f: (Throwable) -> FreeCOf<F, A>): FreeCOf<F, A> =
    handleErrorW(f)

  override fun <A> just(a: A): FreeCOf<F, A> =
    FreeC.just(a)

  override fun <A, B> FreeCOf<F, A>.ap(ff: FreeCOf<F, (A) -> B>): FreeCOf<F, B> =
    apply(ff)
}

@extension
@undocumented
interface FreeCMonadError<F> : MonadError<FreeCPartialOf<F>, Throwable> {
  override fun <A> raiseError(e: Throwable): FreeCOf<F, A> =
    FreeC.raiseError(e)

  override fun <A> FreeCOf<F, A>.handleErrorWith(f: (Throwable) -> FreeCOf<F, A>): FreeCOf<F, A> =
    handleErrorW(f)

  override fun <A> just(a: A): FreeCOf<F, A> =
    FreeC.just(a)

  override fun <A, B> FreeCOf<F, A>.flatMap(f: (A) -> FreeCOf<F, B>): FreeCOf<F, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> FreeCOf<F, Either<A, B>>): FreeCOf<F, B> =
    FreeC.tailRecM(a) { f(it).fix() }
}

@extension
@undocumented
interface FreeCBracket<F> : Bracket<FreeCPartialOf<F>, Throwable>, FreeCMonadError<F> {
  override fun <A, B> FreeCOf<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> FreeCOf<F, Unit>, use: (A) -> FreeCOf<F, B>): FreeCOf<F, B> =
    bracketC(use, release)
}

@extension
@undocumented
interface FreeCMonadDefer<F> : MonadDefer<FreeCPartialOf<F>>, FreeCBracket<F> {
  override fun <A> defer(fa: () -> FreeCOf<F, A>): FreeCOf<F, A> =
    FreeC.defer(fa)
}

@extension
@undocumented
interface FreeCEq<F, G, A> : Eq<FreeCOf<F, A>> {

  fun ME(): MonadError<G, Throwable>

  fun FK(): FunctionK<F, G>

  fun EQFA(): Eq<Kind<G, Option<A>>>

  override fun FreeCOf<F, A>.eqv(b: FreeCOf<F, A>): Boolean = EQFA().run {
    fix().foldMap(FK(), ME()).eqv(b.fix().foldMap(FK(), ME()))
  }
}
