@file:Suppress("UnusedImports")
package arrow.core.extensions

import arrow.Kind
import arrow.core.*
import arrow.core.Try.Failure
import arrow.core.extensions.`try`.monad.monad
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.`try`.monadThrow.monadThrow
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.option.monadError.monadError
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.suspended.monad.Fx
import arrow.core.extensions.traverse as tryTraverse

fun <A> Try<A>.combine(SG: Semigroup<A>, b: Try<A>): Try<A> =
  when (this) {
    is Success<A> -> when (b) {
      is Success<A> -> Success(SG.run { value.combine(b.value) })
      is Failure -> b
    }
    is Failure -> this
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

  override fun empty(): Try<A> = Success(MO().empty())
}

@extension
interface TryApplicativeError: ApplicativeError<ForTry, Throwable>, TryApplicative {

  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> TryOf<A>.handleErrorWith(f: (Throwable) -> TryOf<A>): Try<A> =
    fix().recoverWith { f(it).fix() }

}

@extension
interface TryMonadError: MonadError<ForTry, Throwable>, TryMonad {
  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> TryOf<A>.handleErrorWith(f: (Throwable) -> TryOf<A>): Try<A> =
    fix().recoverWith { f(it).fix() }
}

@extension
interface TryMonadThrow: MonadThrow<ForTry>, TryMonadError

@extension
interface TryEq<A> : Eq<Try<A>> {

  fun EQA(): Eq<A>

  fun EQT(): Eq<Throwable>

  override fun Try<A>.eqv(b: Try<A>): Boolean = when (this) {
    is Success -> when (b) {
      is Failure -> false
      is Success -> EQA().run { value.eqv(b.value) }
    }
    is Failure -> when (b) {
      is Failure -> EQT().run { exception.eqv(b.exception) }
      is Success -> false
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
  fix().fold({ just(Try.raise(it)) }, { f(it).map { Try.just(it) } })
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

@extension
interface TryFx : arrow.typeclasses.suspended.monaderror.Fx<ForTry> {
  override fun monadError(): MonadThrow<ForTry> = Try.monadThrow()
}