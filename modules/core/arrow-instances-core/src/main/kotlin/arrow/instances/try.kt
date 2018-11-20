package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.core.Try.Failure
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*
import arrow.instances.traverse as tryTraverse

fun <A> Try<A>.combine(SG: Semigroup<A>, b: Try<A>): Try<A> =
  when (this) {
    is Success<A> -> when (b) {
      is Success<A> -> Success(SG.run { value.combine(b.value) })
      is Failure -> this
    }
    is Failure -> b
  }

@extension
interface TrySemigroupInstance<A> : Semigroup<Try<A>> {

  fun SG(): Semigroup<A>

  override fun Try<A>.combine(b: Try<A>): Try<A> = fix().combine(SG(), b)
}

@extension
interface TryMonoidInstance<A> : Monoid<Try<A>>, TrySemigroupInstance<A> {
  fun MO(): Monoid<A>

  override fun SG(): Semigroup<A> = MO()

  override fun empty(): Try<A> = Success(MO().empty())
}

@extension
interface TryApplicativeErrorInstance : ApplicativeError<ForTry, Throwable>, TryApplicativeInstance {

  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
    fix().recoverWith { f(it).fix() }

}

@extension
interface TryMonadErrorInstance : MonadError<ForTry, Throwable>, TryMonadInstance {
  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
    fix().recoverWith { f(it).fix() }
}

@extension
interface TryMonadThrowInstance : MonadThrow<ForTry>, TryMonadErrorInstance

@extension
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

@extension
interface TryShowInstance<A> : Show<Try<A>> {
  override fun Try<A>.show(): String =
    toString()
}

@extension
interface TryFunctorInstance : Functor<ForTry> {
  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

@extension
interface TryApplicativeInstance : Applicative<ForTry> {
  override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)
}

@extension
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

@extension
interface TryFoldableInstance : Foldable<ForTry> {
  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForTry, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForTry, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <A, B, G> TryOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> = GA.run {
  fix().fold({ just(Try.raise(it)) }, { f(it).map { Try.just(it) } })
}

fun <A, G> TryOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Try<A>> =
  tryTraverse(GA, ::identity)

@extension
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

@extension
interface TryHashInstance<A> : Hash<Try<A>>, TryEqInstance<A> {

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

object TryContext : TryMonadErrorInstance, TryTraverseInstance {
  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForTry.Companion.extensions(f: TryContext.() -> A): A =
  f(TryContext)
