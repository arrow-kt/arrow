@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForTry
import arrow.core.Try
import arrow.core.Try.Failure
import arrow.core.TryOf
import arrow.core.extensions.`try`.eq.eq
import arrow.core.extensions.`try`.monadThrow.monadThrow
import arrow.core.fix
import arrow.core.identity
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFx
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonadThrowFx
import arrow.typeclasses.MonadThrowSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.core.extensions.traverse as tryTraverse
import arrow.core.handleErrorWith as tryHandleErrorWith

fun <A> Try<A>.combine(SG: Semigroup<A>, b: Try<A>): Try<A> =
  flatMap { a ->
    b.map { b -> SG.run { a + b } }
  }

@extension
interface TrySemigroup<A> : Semigroup<Try<A>> {

  fun SG(): Semigroup<A>

  override fun Try<A>.combine(b: Try<A>): Try<A> = fix().combine(SG(), b)
}

@extension
interface TryMonoid<A> : Monoid<Try<A>>, TrySemigroup<A> {
  fun MO(): Monoid<A>

  override fun SG(): Semigroup<A> = MO()

  override fun empty(): Try<A> = Try.Success(MO().empty())
}

@extension
interface TryApplicativeError : ApplicativeError<ForTry, Throwable>, TryApplicative {

  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> TryOf<A>.handleErrorWith(f: (Throwable) -> TryOf<A>): Try<A> =
    fix().tryHandleErrorWith { f(it).fix() }
}

@extension
interface TryMonadError : MonadError<ForTry, Throwable>, TryMonad {
  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> TryOf<A>.handleErrorWith(f: (Throwable) -> TryOf<A>): Try<A> =
    fix().tryHandleErrorWith { f(it).fix() }
}

@extension
interface TryMonadThrow : MonadThrow<ForTry>, TryMonadError {
  override val fx: MonadThrowFx<ForTry>
    get() = TryFxMonadThrow
}

@extension
interface TryEq<A> : Eq<Try<A>> {

  fun EQA(): Eq<A>

  fun EQT(): Eq<Throwable>

  override fun Try<A>.eqv(b: Try<A>): Boolean = when (this) {
    is Try.Success -> when (b) {
      is Failure -> false
      is Try.Success -> EQA().run { value.eqv(b.value) }
    }
    is Failure -> when (b) {
      is Failure -> EQT().run { exception.eqv(b.exception) }
      is Try.Success -> false
    }
  }
}

@extension
interface TryShow<A> : Show<Try<A>> {
  override fun Try<A>.show(): String =
    toString()
}

@extension
interface TryFunctor : Functor<ForTry> {
  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

@extension
interface TryApply : Apply<ForTry> {
  override fun <A, B> TryOf<A>.ap(ff: TryOf<(A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForTry, A>.lazyAp(ff: () -> Kind<ForTry, (A) -> B>): Kind<ForTry, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }

  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

@extension
interface TryApplicative : Applicative<ForTry> {
  override fun <A, B> TryOf<A>.ap(ff: TryOf<(A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)
}

@extension
interface TryMonad : Monad<ForTry> {
  override fun <A, B> TryOf<A>.ap(ff: TryOf<(A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> TryOf<A>.flatMap(f: (A) -> TryOf<B>): Try<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, TryOf<Either<A, B>>>): Try<B> =
    Try.tailRecM(a, f)

  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)

  override val fx: MonadFx<ForTry>
    get() = TryFxMonadThrow
}

@extension
interface TryFoldable : Foldable<ForTry> {
  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> TryOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> TryOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <A, B, G> TryOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> = GA.run {
  fix().fold({ just(Try.raiseError(it)) }, { f(it).map { Try.just(it) } })
}

fun <A, G> TryOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Try<A>> =
  tryTraverse(GA, ::identity)

@extension
interface TryTraverse : Traverse<ForTry> {
  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <G, A, B> TryOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> =
    tryTraverse(AP, f)

  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): kotlin.Boolean =
    fix().exists(p)

  override fun <A, B> TryOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> TryOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface TryHash<A> : Hash<Try<A>>, TryEq<A> {

  fun HA(): Hash<A>
  fun HT(): Hash<Throwable>

  override fun EQA(): Eq<A> = HA()

  override fun EQT(): Eq<Throwable> = HT()

  override fun Try<A>.hash(): Int = fold({
    HT().run { it.hash() }
  }, {
    HA().run { it.hash() }
  })
}

internal object TryFxMonadThrow : MonadThrowFx<ForTry> {
  override val ME: MonadThrow<ForTry> = Try.monadThrow()
}

fun <A> Try.Companion.fx(c: suspend MonadThrowSyntax<ForTry>.() -> A): Try<A> =
  Try.monadThrow().fx.monadThrow(c).fix()

@extension
interface TryEqK : EqK<ForTry> {
  override fun <A> Kind<ForTry, A>.eqK(other: Kind<ForTry, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      Try.eq(EQ, Eq.any()).run {
        it.first.eqv(it.second)
      }
    }
}
