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
import arrow.typeclasses.Alternative
import arrow.typeclasses.Monoid
import arrow.undocumented

@extension
@undocumented
interface EitherTFunctor<F, L> : Functor<EitherTPartialOf<F, L>> {

  fun FF(): Functor<F>

  override fun <A, B> EitherTOf<F, L, A>.map(f: (A) -> B): EitherT<F, L, B> =
    fix().map(FF(), f)
}

@extension
@undocumented
interface EitherTApply<F, L> : Apply<EitherTPartialOf<F, L>>, EitherTFunctor<F, L> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A, B> EitherTOf<F, L, A>.map(f: (A) -> B): EitherT<F, L, B> =
    fix().map(AF(), f)

  override fun <A, B> EitherTOf<F, L, A>.ap(ff: EitherTOf<F, L, (A) -> B>): EitherT<F, L, B> =
    fix().ap(AF(), ff)
}

@extension
@undocumented
interface EitherTApplicative<F, L> : Applicative<EitherTPartialOf<F, L>>, EitherTFunctor<F, L> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A> just(a: A): EitherT<F, L, A> =
    EitherT.just(AF(), a)

  override fun <A, B> EitherTOf<F, L, A>.map(f: (A) -> B): EitherT<F, L, B> =
    fix().map(AF(), f)

  override fun <A, B> EitherTOf<F, L, A>.ap(ff: EitherTOf<F, L, (A) -> B>): EitherT<F, L, B> =
    fix().ap(AF(), ff)
}

@extension
@undocumented
interface EitherTMonad<F, L> : Monad<EitherTPartialOf<F, L>>, EitherTApplicative<F, L> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun <A, B> EitherTOf<F, L, A>.map(f: (A) -> B): EitherT<F, L, B> =
    fix().map(MF(), f)

  override fun <A, B> EitherTOf<F, L, A>.ap(ff: EitherTOf<F, L, (A) -> B>): EitherT<F, L, B> =
    fix().ap(MF(), ff)

  override fun <A, B> EitherTOf<F, L, A>.flatMap(f: (A) -> EitherTOf<F, L, B>): EitherT<F, L, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> EitherTOf<F, L, Either<A, B>>): EitherT<F, L, B> =
    EitherT.tailRecM(MF(), a, f)
}

@extension
@undocumented
interface EitherTApplicativeError<F, L, E> : ApplicativeError<EitherTPartialOf<F, L>, E>, EitherTApplicative<F, L> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> raiseError(e: E): EitherT<F, L, A> =
    EitherT.liftF(AE(), AE().raiseError(e))

  override fun <A> EitherTOf<F, L, A>.handleErrorWith(f: (E) -> EitherTOf<F, L, A>): EitherT<F, L, A> = AE().run {
    EitherT(value().handleErrorWith { l -> f(l).value() })
  }
}

@extension
@undocumented
interface EitherTMonadError<F, L, E> : MonadError<EitherTPartialOf<F, L>, E>, EitherTApplicativeError<F, L, E>, EitherTMonad<F, L> {
  override fun MF(): Monad<F>
  override fun AE(): ApplicativeError<F, E>
  override fun AF(): Applicative<F> = MF()
}

fun <F, L, E> EitherT.Companion.monadError(ME: MonadError<F, E>): MonadError<EitherTPartialOf<F, L>, E> =
  object : EitherTMonadError<F, L, E> {
    override fun MF(): Monad<F> = ME
    override fun AE(): ApplicativeError<F, E> = ME
  }

@extension
@undocumented
interface EitherTMonadThrow<F, L> : MonadThrow<EitherTPartialOf<F, L>>, EitherTMonadError<F, L, Throwable> {
  override fun MF(): Monad<F>
  override fun AE(): ApplicativeError<F, Throwable>
}

@extension
@undocumented
interface EitherTFoldable<F, L> : Foldable<EitherTPartialOf<F, L>> {

  fun FFF(): Foldable<F>

  override fun <B, C> EitherTOf<F, L, B>.foldLeft(b: C, f: (C, B) -> C): C =
    fix().foldLeft(FFF(), b, f)

  override fun <B, C> EitherTOf<F, L, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(FFF(), lb, f)
}

@extension
@undocumented
interface EitherTTraverse<F, L> : Traverse<EitherTPartialOf<F, L>>, EitherTFunctor<F, L>, EitherTFoldable<F, L> {

  fun TF(): Traverse<F>

  override fun FF(): Functor<F> = TF()

  override fun FFF(): Foldable<F> = TF()

  override fun <A, B> EitherTOf<F, L, A>.map(f: (A) -> B): EitherT<F, L, B> =
    fix().map(TF(), f)

  override fun <G, B, C> EitherTOf<F, L, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<F, L, C>> =
    fix().traverse(TF(), AP, f)
}

@extension
@undocumented
interface EitherTSemigroupK<F, L> : SemigroupK<EitherTPartialOf<F, L>> {
  fun MF(): Monad<F>

  override fun <A> EitherTOf<F, L, A>.combineK(y: EitherTOf<F, L, A>): EitherT<F, L, A> =
    fix().combineK(MF(), y)
}

@extension
@undocumented
interface EitherTContravariant<F, L> : Contravariant<EitherTPartialOf<F, L>> {
  fun CF(): Contravariant<F>

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.contramap(f: (B) -> A): Kind<EitherTPartialOf<F, L>, B> =
    EitherT(
      CF().run { value().contramap<Either<L, A>, Either<L, B>> { it.map(f) } }
    )
}

@extension
@undocumented
interface EitherTDivide<F, L> : Divide<EitherTPartialOf<F, L>>, EitherTContravariant<F, L> {
  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  override fun <A, B, Z> divide(fa: Kind<EitherTPartialOf<F, L>, A>, fb: Kind<EitherTPartialOf<F, L>, B>, f: (Z) -> Tuple2<A, B>): Kind<EitherTPartialOf<F, L>, Z> =
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
interface EitherTDivisibleInstance<F, L> : Divisible<EitherTPartialOf<F, L>>, EitherTDivide<F, L> {

  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()

  override fun <A> conquer(): Kind<EitherTPartialOf<F, L>, A> =
    EitherT(
      DFF().conquer()
    )
}

@extension
@undocumented
interface EitherTDecidableInstance<F, L> : Decidable<EitherTPartialOf<F, L>>, EitherTDivisibleInstance<F, L> {

  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()

  override fun <A, B, Z> choose(fa: Kind<EitherTPartialOf<F, L>, A>, fb: Kind<EitherTPartialOf<F, L>, B>, f: (Z) -> Either<A, B>): Kind<EitherTPartialOf<F, L>, Z> =
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

@extension
interface EitherTAlternative<F, L> : Alternative<EitherTPartialOf<F, L>>, EitherTApplicative<F, L> {
  override fun AF(): Applicative<F> = MF()
  fun MF(): Monad<F>
  fun ME(): Monoid<L>

  override fun <A> empty(): Kind<EitherTPartialOf<F, L>, A> = EitherT(MF().just(ME().empty().left()))

  override fun <A> Kind<EitherTPartialOf<F, L>, A>.orElse(b: Kind<EitherTPartialOf<F, L>, A>): Kind<EitherTPartialOf<F, L>, A> =
    EitherT(
      MF().fx.monad {
        val l = !value()
        l.fold({ ll ->
          val r = !b.value()
          r.fold({
            ME().run { (ll + it).left() }
          }, {
            it.right()
          })
        }, {
          it.right()
        })
      }
    )
}

fun <F, A, B, C> EitherTOf<F, A, B>.foldLeft(FF: Foldable<F>, b: C, f: (C, B) -> C): C =
  FF.compose(Either.foldable<A>()).foldLC(value(), b, f)

fun <F, A, B, C> EitherTOf<F, A, B>.foldRight(FF: Foldable<F>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = FF.compose(Either.foldable<A>()).run {
  value().foldRC(lb, f)
}

fun <F, A, B, G, C> EitherTOf<F, A, B>.traverse(FF: Traverse<F>, GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<F, A, C>> {
  val fa: Kind<G, Kind<Nested<F, EitherPartialOf<A>>, C>> = ComposedTraverse(FF, Either.traverse<A>()).run { value().traverseC(f, GA) }
  val mapper: (Kind<Nested<F, EitherPartialOf<A>>, C>) -> EitherT<F, A, C> = { nested -> EitherT(FF.run { nested.unnest().map { it.fix() } }) }
  return GA.run { fa.map(mapper) }
}

fun <F, G, A, B> EitherTOf<F, A, Kind<G, B>>.sequence(FF: Traverse<F>, GA: Applicative<G>): Kind<G, EitherT<F, A, B>> =
  traverse(FF, GA, ::identity)

fun <F, L> EitherT.Companion.applicativeError(MF: Monad<F>): ApplicativeError<EitherTPartialOf<F, L>, L> =
  object : ApplicativeError<EitherTPartialOf<F, L>, L>, EitherTApplicative<F, L> {

    override fun AF(): Applicative<F> = MF

    override fun <A> raiseError(e: L): EitherTOf<F, L, A> =
      EitherT(MF.just(Left(e)))

    override fun <A> EitherTOf<F, L, A>.handleErrorWith(f: (L) -> EitherTOf<F, L, A>): EitherT<F, L, A> =
      handleErrorWith(this, f, MF)
  }

fun <F, L> EitherT.Companion.monadError(MF: Monad<F>): MonadError<EitherTPartialOf<F, L>, L> =
  object : MonadError<EitherTPartialOf<F, L>, L>, EitherTMonad<F, L> {
    override fun MF(): Monad<F> = MF

    override fun <A> raiseError(e: L): EitherTOf<F, L, A> =
      EitherT(MF.just(Left(e)))

    override fun <A> EitherTOf<F, L, A>.handleErrorWith(f: (L) -> EitherTOf<F, L, A>): EitherT<F, L, A> =
      handleErrorWith(this, f, MF())
  }

private fun <F, L, A> handleErrorWith(fa: EitherTOf<F, L, A>, f: (L) -> EitherTOf<F, L, A>, MF: Monad<F>): EitherT<F, L, A> =
  MF.run {
    EitherT(fa.value().flatMap {
      when (it) {
        is Either.Left -> f(it.a).value()
        is Either.Right -> just(it)
      }
    })
  }

fun <F, L, R> EitherT.Companion.fx(M: MonadThrow<F>, c: suspend MonadThrowSyntax<EitherTPartialOf<F, L>>.() -> R): EitherT<F, L, R> =
  EitherT.monadThrow<F, L>(M, M).fx.monadThrow(c).fix()
