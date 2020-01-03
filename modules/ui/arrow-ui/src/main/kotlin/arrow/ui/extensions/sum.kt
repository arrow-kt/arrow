package arrow.ui.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.ui.Sum
import arrow.ui.SumPartialOf
import arrow.ui.fix
import arrow.undocumented

@extension
@undocumented
interface SumComonad<F, G> : Comonad<SumPartialOf<F, G>> {

  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.coflatMap(f: (Kind<SumPartialOf<F, G>, A>) -> B): Sum<F, G, B> =
    fix().coflatmap(CF(), CG(), f)

  override fun <A> Kind<SumPartialOf<F, G>, A>.extract(): A =
    fix().extract(CF(), CG())

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
    fix().map(CF(), CG(), f)
}

@extension
@undocumented
interface SumFunctor<F, G> : Functor<SumPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
    fix().map(FF(), FG(), f)
}

@extension
interface SumEq<F, G, A> : Eq<Sum<F, G, A>> {
  fun EQF(): Eq<Kind<F, A>>
  fun EQG(): Eq<Kind<G, A>>

  override fun Sum<F, G, A>.eqv(b: Sum<F, G, A>): Boolean =
    EQF().run { left.eqv(b.left) } &&
      EQG().run { right.eqv(b.right) }
}

@extension
interface SumHash<F, G, A> : Hash<Sum<F, G, A>>, SumEq<F, G, A> {
  fun HF(): Hash<Kind<F, A>>
  fun HG(): Hash<Kind<G, A>>

  override fun EQF(): Eq<Kind<F, A>> = HF()
  override fun EQG(): Eq<Kind<G, A>> = HG()

  override fun Sum<F, G, A>.hash(): Int = 31 * HF().run { left.hash() } + HG().run { right.hash() }
}

@extension
interface ContravariantSumInstance<F, G> : Contravariant<SumPartialOf<F, G>> {
  fun CF(): Contravariant<F>
  fun CG(): Contravariant<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.contramap(f: (B) -> A): Kind<SumPartialOf<F, G>, B> =
    Sum(
      CF().run { fix().left.contramap(f) },
      CG().run { fix().right.contramap(f) },
      fix().side
    )
}

@extension
interface DivideSumInstance<F, G> : Divide<SumPartialOf<F, G>>, ContravariantSumInstance<F, G> {
  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()
  fun DG(): Divide<G>
  override fun CG(): Contravariant<G> = DG()

  override fun <A, B, Z> divide(fa: Kind<SumPartialOf<F, G>, A>, fb: Kind<SumPartialOf<F, G>, B>, f: (Z) -> Tuple2<A, B>): Kind<SumPartialOf<F, G>, Z> =
    Sum(
      DF().divide(fa.fix().left, fb.fix().left, f),
      DG().divide(fa.fix().right, fb.fix().right, f)
    )
}

@extension
interface DivisibleSumInstance<F, G> : Divisible<SumPartialOf<F, G>>, DivideSumInstance<F, G> {
  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()
  fun DGG(): Divisible<G>
  override fun DG(): Divide<G> = DGG()

  override fun <A> conquer(): Kind<SumPartialOf<F, G>, A> =
    Sum(
      DFF().conquer(),
      DGG().conquer()
    )
}

@extension
interface DecidableSumInstance<F, G> : Decidable<SumPartialOf<F, G>>, DivisibleSumInstance<F, G> {
  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()
  fun DGGG(): Decidable<G>
  override fun DGG(): Divisible<G> = DGGG()

  override fun <A, B, Z> choose(fa: Kind<SumPartialOf<F, G>, A>, fb: Kind<SumPartialOf<F, G>, B>, f: (Z) -> Either<A, B>): Kind<SumPartialOf<F, G>, Z> =
    Sum(
      DFFF().choose(fa.fix().left, fb.fix().left, f),
      DGGG().choose(fa.fix().right, fb.fix().right, f)
    )
}

@extension
interface SumEqK<F, G> : EqK<SumPartialOf<F, G>> {

  fun EQKF(): EqK<F>
  fun EQKG(): EqK<G>

  override fun <V> Kind<SumPartialOf<F, G>, V>.eqK(other: Kind<SumPartialOf<F, G>, V>, EQ: Eq<V>): Boolean =
    (this.fix() to other.fix()).let {
      when (it.first.side) {
        is Sum.Side.Left -> when (it.second.side) {
          is Sum.Side.Left -> EQKF().liftEq(EQ).run {
            it.first.left.eqv(it.second.left)
          }
          else -> false
        }
        is Sum.Side.Right -> when (it.second.side) {
          is Sum.Side.Right -> {
            EQKG().liftEq(EQ).run {
              it.first.right.eqv(it.second.right)
            }
          }
          else -> false
        }
      }
    }
}
