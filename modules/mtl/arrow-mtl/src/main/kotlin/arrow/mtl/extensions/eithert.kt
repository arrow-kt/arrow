package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Tuple2
import arrow.core.extensions.either.foldable.foldable
import arrow.core.extensions.either.traverse.traverse
import arrow.core.fix
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.mtl.EitherT
import arrow.mtl.EitherTOf
import arrow.mtl.EitherTPartialOf
import arrow.mtl.fix
import arrow.mtl.extensions.eithert.monadThrow.monadThrow
import arrow.mtl.value
import arrow.extension
import arrow.mtl.typeclasses.ComposedTraverse
import arrow.mtl.typeclasses.Nested
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonadThrowSyntax
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Traverse
import arrow.mtl.typeclasses.compose
import arrow.mtl.typeclasses.unnest
import arrow.undocumented

@extension
@undocumented
interface EitherTFunctor<L, F> : Functor<EitherTPartialOf<L, F>> {

  fun FF(): Functor<F>

  override fun <A, B> EitherTOf<L, F, A>.map(f: (A) -> B): EitherT<L, F, B> =
    fix().map(FF(), f)
}

@extension
@undocumented
interface EitherTApply<L, F> : Apply<EitherTPartialOf<L, F>>, EitherTFunctor<L, F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A, B> EitherTOf<L, F, A>.map(f: (A) -> B): EitherT<L, F, B> =
    fix().map(AF(), f)

  override fun <A, B> EitherTOf<L, F, A>.ap(ff: EitherTOf<L, F, (A) -> B>): EitherT<L, F, B> =
    fix().ap(AF(), ff)
}

@extension
@undocumented
interface EitherTApplicative<L, F> : Applicative<EitherTPartialOf<L, F>>, EitherTFunctor<L, F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A> just(a: A): EitherT<L, F, A> =
    EitherT.just(AF(), a)

  override fun <A, B> EitherTOf<L, F, A>.map(f: (A) -> B): EitherT<L, F, B> =
    fix().map(AF(), f)

  override fun <A, B> EitherTOf<L, F, A>.ap(ff: EitherTOf<L, F, (A) -> B>): EitherT<L, F, B> =
    fix().ap(AF(), ff)
}

@extension
@undocumented
interface EitherTMonad<L, F> : Monad<EitherTPartialOf<L, F>>, EitherTApplicative<L, F> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun <A, B> EitherTOf<L, F, A>.map(f: (A) -> B): EitherT<L, F, B> =
    fix().map(MF(), f)

  override fun <A, B> EitherTOf<L, F, A>.ap(ff: EitherTOf<L, F, (A) -> B>): EitherT<L, F, B> =
    fix().ap(MF(), ff)

  override fun <A, B> EitherTOf<L, F, A>.flatMap(f: (A) -> EitherTOf<L, F, B>): EitherT<L, F, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> EitherTOf<L, F, Either<A, B>>): EitherT<L, F, B> =
    EitherT.tailRecM(MF(), a, f)
}

@extension
@undocumented
interface EitherTApplicativeError<L, F> : ApplicativeError<EitherTPartialOf<L, F>, L>, EitherTApplicative<L, F> {

  fun AE(): ApplicativeError<F, L>

  override fun AF(): Applicative<F> = AE()

  override fun <A> EitherTOf<L, F, A>.handleErrorWith(f: (L) -> EitherTOf<L, F, A>): EitherT<L, F, A> = AE().run {
    EitherT(value().handleErrorWith { l -> f(l).value() })
  }

  override fun <A> raiseError(e: L): EitherT<L, F, A> = AE().run {
    EitherT.liftF(this, raiseError(e))
  }
}

@extension
@undocumented
interface EitherTMonadError<L, F> : MonadError<EitherTPartialOf<L, F>, L>, EitherTApplicativeError<L, F>, EitherTMonad<L, F> {
  override fun MF(): Monad<F>
  override fun AE(): ApplicativeError<F, L>
  override fun AF(): Applicative<F> = MF()
}

fun <L, F> EitherT.Companion.monadError(ME: MonadError<F, L>): MonadError<EitherTPartialOf<L, F>, L> =
  object : EitherTMonadError<L, F> {
    override fun MF(): Monad<F> = ME
    override fun AE(): ApplicativeError<F, L> = ME
  }

@extension
@undocumented
interface EitherTMonadThrow<F> : MonadThrow<EitherTPartialOf<Throwable, F>>, EitherTMonadError<Throwable, F> {
  override fun MF(): Monad<F>
  override fun AE(): ApplicativeError<F, Throwable>
}

@extension
@undocumented
interface EitherTFoldable<L, F> : Foldable<EitherTPartialOf<L, F>> {

  fun FFF(): Foldable<F>

  override fun <B, C> EitherTOf<L, F, B>.foldLeft(b: C, f: (C, B) -> C): C =
    fix().foldLeft(FFF(), b, f)

  override fun <B, C> EitherTOf<L, F, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(FFF(), lb, f)
}

@extension
@undocumented
interface EitherTTraverse<L, F> : Traverse<EitherTPartialOf<L, F>>, EitherTFunctor<L, F>, EitherTFoldable<L, F> {

  fun TF(): Traverse<F>

  override fun FF(): Functor<F> = TF()

  override fun FFF(): Foldable<F> = TF()

  override fun <A, B> EitherTOf<L, F, A>.map(f: (A) -> B): EitherT<L, F, B> =
    fix().map(TF(), f)

  override fun <G, B, C> EitherTOf<L, F, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<L, F, C>> =
    fix().traverse(TF(), AP, f)
}

@extension
@undocumented
interface EitherTSemigroupK<L, F> : SemigroupK<EitherTPartialOf<L, F>> {
  fun MF(): Monad<F>

  override fun <A> EitherTOf<L, F, A>.combineK(y: EitherTOf<L, F, A>): EitherT<L, F, A> =
    fix().combineK(MF(), y)
}

@extension
@undocumented
interface EitherTContravariant<L, F> : Contravariant<EitherTPartialOf<L, F>> {
  fun CF(): Contravariant<F>

  override fun <A, B> Kind<EitherTPartialOf<L, F>, A>.contramap(f: (B) -> A): Kind<EitherTPartialOf<L, F>, B> =
    EitherT(
      CF().run { value().contramap<Either<L, A>, Either<L, B>> { it.map(f) } }
    )
}

@extension
@undocumented
interface EitherTDivide<L, F> : Divide<EitherTPartialOf<L, F>>, EitherTContravariant<L, F> {
  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  override fun <A, B, Z> divide(fa: Kind<EitherTPartialOf<L, F>, A>, fb: Kind<EitherTPartialOf<L, F>, B>, f: (Z) -> Tuple2<A, B>): Kind<EitherTPartialOf<L, F>, Z> =
    EitherT(
      DF().divide(fa.value(), fb.value()) { either ->
        either.fold({ it.left() toT it.left() }, {
          val (a, b) = f(it)
          a.right() toT b.right()
        })
      }
    )
}

@extension
@undocumented
interface EitherTDivisibleInstance<L, F> : Divisible<EitherTPartialOf<L, F>>, EitherTDivide<L, F> {

  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()

  override fun <A> conquer(): Kind<EitherTPartialOf<L, F>, A> =
    EitherT(
      DFF().conquer()
    )
}

@extension
@undocumented
interface EitherTDecidableInstance<L, F> : Decidable<EitherTPartialOf<L, F>>, EitherTDivisibleInstance<L, F> {

  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()

  override fun <A, B, Z> choose(fa: Kind<EitherTPartialOf<L, F>, A>, fb: Kind<EitherTPartialOf<L, F>, B>, f: (Z) -> Either<A, B>): Kind<EitherTPartialOf<L, F>, Z> =
    EitherT(
      DFFF().choose(fa.value(), fb.value()) { either ->
        either.map(f).fold({ left ->
          left.left().left()
        }, { e ->
          e.fold({ a ->
            a.right().left()
          }, { b ->
            b.right().right()
          })
        })
      }
    )
}

fun <A, F, B, C> EitherTOf<A, F, B>.foldLeft(FF: Foldable<F>, b: C, f: (C, B) -> C): C =
  FF.compose(Either.foldable<A>()).foldLC(value(), b, f)

fun <A, F, B, C> EitherTOf<A, F, B>.foldRight(FF: Foldable<F>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = FF.compose(Either.foldable<A>()).run {
  value().foldRC(lb, f)
}

fun <A, F, B, G, C> EitherTOf<A, F, B>.traverse(FF: Traverse<F>, GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<A, F, C>> {
  val fa: Kind<G, Kind<Nested<F, EitherPartialOf<A>>, C>> = ComposedTraverse(FF, Either.traverse<A>()).run { value().traverseC(f, GA) }
  val mapper: (Kind<Nested<F, EitherPartialOf<A>>, C>) -> EitherT<A, F, C> = { nested -> EitherT(FF.run { nested.unnest().map { it.fix() } }) }
  return GA.run { fa.map(mapper) }
}

fun <A, F, G, B> EitherTOf<A, F, Kind<G, B>>.sequence(FF: Traverse<F>, GA: Applicative<G>): Kind<G, EitherT<A, F, B>> =
  traverse(FF, GA, ::identity)

fun <L, F> EitherT.Companion.applicativeError(MF: Monad<F>): ApplicativeError<EitherTPartialOf<L, F>, L> =
  object : ApplicativeError<EitherTPartialOf<L, F>, L>, EitherTApplicative<L, F> {

    override fun AF(): Applicative<F> = MF

    override fun <A> raiseError(e: L): EitherTOf<L, F, A> =
      EitherT(MF.just(Left(e)))

    override fun <A> EitherTOf<L, F, A>.handleErrorWith(f: (L) -> EitherTOf<L, F, A>): EitherT<L, F, A> =
      handleErrorWith(this, f, MF)
  }

fun <L, F> EitherT.Companion.monadError(MF: Monad<F>): MonadError<EitherTPartialOf<L, F>, L> =
  object : MonadError<EitherTPartialOf<L, F>, L>, EitherTMonad<L, F> {
    override fun MF(): Monad<F> = MF

    override fun <A> raiseError(e: L): EitherTOf<L, F, A> =
      EitherT(MF.just(Left(e)))

    override fun <A> EitherTOf<L, F, A>.handleErrorWith(f: (L) -> EitherTOf<L, F, A>): EitherT<L, F, A> =
      handleErrorWith(this, f, MF())
  }

private fun <L, F, A> handleErrorWith(fa: EitherTOf<L, F, A>, f: (L) -> EitherTOf<L, F, A>, MF: Monad<F>): EitherT<L, F, A> =
  MF.run {
    EitherT(fa.value().flatMap {
      when (it) {
        is Either.Left -> f(it.a).value()
        is Either.Right -> just(it)
      }
    })
  }

fun <R, F> EitherT.Companion.fx(M: MonadThrow<F>, c: suspend MonadThrowSyntax<EitherTPartialOf<Throwable, F>>.() -> R): EitherT<Throwable, F, R> =
  EitherT.monadThrow(M, M).fx.monadThrow(c).fix()
