package arrow.instances

import arrow.Kind
import arrow.Kind2
import arrow.core.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*
import arrow.core.ap as eitherAp
import arrow.core.combineK as eitherCombineK
import arrow.core.flatMap as eitherFlatMap
import arrow.instances.traverse as eitherTraverse

fun <L, R> Either<L, R>.combine(SGL: Semigroup<L>, SGR: Semigroup<R>, b: Either<L, R>): Either<L, R> {
  val a = this

  return when (a) {
    is Either.Left -> when (b) {
      is Either.Left -> Either.Left(SGL.run { a.a.combine(b.a) })
      is Either.Right -> a
    }
    is Either.Right -> when (b) {
      is Either.Left -> b
      is Either.Right -> Either.right(SGR.run { a.b.combine(b.b) })
    }
  }
}

@extension
interface EitherSemigroupInstance<L, R> : Semigroup<Either<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Either<L, R>.combine(b: Either<L, R>): Either<L, R> = fix().combine(SGL(), SGR(), b)
}

@extension
interface EitherMonoidInstance<L, R> : Monoid<Either<L, R>>, EitherSemigroupInstance<L, R> {
  fun MOL(): Monoid<L>
  fun MOR(): Monoid<R>

  override fun SGL(): Semigroup<L> = MOL()
  override fun SGR(): Semigroup<R> = MOR()

  override fun empty(): Either<L, R> = Right(MOR().empty())
}

@extension
interface EitherFunctorInstance<L> : Functor<EitherPartialOf<L>> {
  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)
}

@extension
interface EitherBifunctorInstance : Bifunctor<ForEither> {
  override fun <A, B, C, D> Kind2<ForEither, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<ForEither, C, D> =
    fix().bimap(fl, fr)
}

@extension
interface EitherApplicativeInstance<L> : Applicative<EitherPartialOf<L>>, EitherFunctorInstance<L> {

  override fun <A> just(a: A): Either<L, A> = Right(a)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.ap(ff: Kind<EitherPartialOf<L>, (A) -> B>): Either<L, B> =
    fix().eitherAp(ff)
}

@extension
interface EitherMonadInstance<L> : Monad<EitherPartialOf<L>>, EitherApplicativeInstance<L> {

  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.ap(ff: Kind<EitherPartialOf<L>, (A) -> B>): Either<L, B> =
    fix().eitherAp(ff)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.flatMap(f: (A) -> Kind<EitherPartialOf<L>, B>): Either<L, B> =
    fix().eitherFlatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> =
    Either.tailRecM(a, f)
}

@extension
interface EitherApplicativeErrorInstance<L> : ApplicativeError<EitherPartialOf<L>, L>, EitherApplicativeInstance<L> {

  override fun <A> raiseError(e: L): Either<L, A> = Left(e)

  override fun <A> Kind<EitherPartialOf<L>, A>.handleErrorWith(f: (L) -> Kind<EitherPartialOf<L>, A>): Either<L, A> {
    val fea = fix()
    return when (fea) {
      is Either.Left -> f(fea.a).fix()
      is Either.Right -> fea
    }
  }
}

@extension
interface EitherMonadErrorInstance<L> : MonadError<EitherPartialOf<L>, L>, EitherApplicativeErrorInstance<L>, EitherMonadInstance<L>

@extension
interface EitherFoldableInstance<L> : Foldable<EitherPartialOf<L>> {

  override fun <A, B> Kind<EitherPartialOf<L>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <G, A, B, C> EitherOf<A, B>.traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Either<A, C>> =
  fix().fold({ GA.just(Either.Left(it)) }, { GA.run { f(it).map { Either.Right(it) } } })

@extension
interface EitherTraverseInstance<L> : Traverse<EitherPartialOf<L>>, EitherFoldableInstance<L> {

  override fun <G, A, B> Kind<EitherPartialOf<L>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<EitherPartialOf<L>, B>> =
    fix().eitherTraverse(AP, f)
}

@extension
interface EitherSemigroupKInstance<L> : SemigroupK<EitherPartialOf<L>> {

  override fun <A> Kind<EitherPartialOf<L>, A>.combineK(y: Kind<EitherPartialOf<L>, A>): Either<L, A> =
    fix().eitherCombineK(y)
}

@extension
interface EitherEqInstance<in L, in R> : Eq<Either<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Either<L, R>.eqv(b: Either<L, R>): Boolean = when (this) {
    is Either.Left -> when (b) {
      is Either.Left -> EQL().run { a.eqv(b.a) }
      is Either.Right -> false
    }
    is Either.Right -> when (b) {
      is Either.Left -> false
      is Either.Right -> EQR().run { this@eqv.b.eqv(b.b) }
    }
  }

}

@extension
interface EitherShowInstance<L, R> : Show<Either<L, R>> {
  override fun Either<L, R>.show(): String =
    toString()
}

@extension
interface EitherHashInstance<L, R> : Hash<Either<L, R>>, EitherEqInstance<L, R> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun EQL(): Eq<L> = HL()

  override fun EQR(): Eq<R> = HR()

  override fun Either<L, R>.hash(): Int = fold({
    HL().run { it.hash() }
  }, {
    HR().run { it.hash() }
  })
}

class EitherContext<L> : EitherMonadErrorInstance<L>, EitherTraverseInstance<L>, EitherSemigroupKInstance<L> {
  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> =
    fix().map(f)
}

class EitherContextPartiallyApplied<L> {
  infix fun <A> extensions(f: EitherContext<L>.() -> A): A =
    f(EitherContext())
}

@Deprecated(ExtensionsDSLDeprecated)
fun <L> ForEither(): EitherContextPartiallyApplied<L> =
  EitherContextPartiallyApplied()
