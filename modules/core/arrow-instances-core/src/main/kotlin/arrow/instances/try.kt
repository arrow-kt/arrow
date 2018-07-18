package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Failure
import arrow.core.ForTry
import arrow.core.Success
import arrow.core.Try
import arrow.core.TryOf
import arrow.core.fix
import arrow.core.identity
import arrow.core.recoverWith
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.instances.traverse as tryTraverse

@instance(Try::class)
interface TrySemigroupInstance<A> : Semigroup<Try<A>> {

  fun SG(): Semigroup<A>

  override fun Try<A>.combine(b: Try<A>): Try<A> =
    when (this) {
      is Success<A> -> when (b) {
        is Success<A> -> Success(SG().run { value.combine(b.value) })
        is Failure -> this
      }
      is Failure -> b
    }
}

@instance(Try::class)
interface TryMonoidInstance<A> : TrySemigroupInstance<A>, Monoid<Try<A>> {
  fun MO(): Monoid<A>

  override fun SG(): Semigroup<A> = MO()

  override fun empty(): Try<A> = Success(MO().empty())
}

@instance(Try::class)
interface TryApplicativeErrorInstance : TryApplicativeInstance, ApplicativeError<ForTry, Throwable> {

  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
    fix().recoverWith { f(it).fix() }

}

@instance(Try::class)
interface TryMonadErrorInstance : TryMonadInstance, MonadError<ForTry, Throwable> {
  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
    fix().recoverWith { f(it).fix() }
}

@instance(Try::class)
interface TryEqInstance<A> : Eq<Try<A>> {

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

@instance(Try::class)
interface TryShowInstance<A> : Show<Try<A>> {
  override fun Try<A>.show(): String =
    toString()
}

@instance(Try::class)
interface TryFunctorInstance : Functor<ForTry> {
  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

@instance(Try::class)
interface TryApplicativeInstance : Applicative<ForTry> {
  override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)
}

@instance(Try::class)
interface TryMonadInstance : Monad<ForTry> {
  override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForTry, A>.flatMap(f: (A) -> Kind<ForTry, B>): Try<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, TryOf<Either<A, B>>>): Try<B> =
    Try.tailRecM(a, f)

  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)
}

@instance(Try::class)
interface TryFoldableInstance : Foldable<ForTry> {
  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForTry, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForTry, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <A, B, G> TryOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> = GA.run {
  fix().fold({ just(Try.raise(it)) }, { f(it).map({ Try.just(it) }) })
}

fun <A, G> TryOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Try<A>> =
  tryTraverse(GA, ::identity)

@instance(Try::class)
interface TryTraverseInstance : Traverse<ForTry> {
  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <G, A, B> TryOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> =
    tryTraverse(AP, f)

  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): kotlin.Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForTry, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForTry, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

object TryContext : TryMonadErrorInstance, TryTraverseInstance {
  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

infix fun <A> ForTry.Companion.extensions(f: TryContext.() -> A): A =
  f(TryContext)
