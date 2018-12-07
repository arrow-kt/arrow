package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.data.Sum
import arrow.data.SumPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*

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
  override fun CF(): Divide<F>
  override fun CG(): Divide<G>

  override fun <A, B, Z> divide(fa: Kind<SumPartialOf<F, G>, A>, fb: Kind<SumPartialOf<F, G>, B>, f: (Z) -> Tuple2<A, B>): Kind<SumPartialOf<F, G>, Z> =
    Sum(
      CF().divide(fa.fix().left, fb.fix().left, f),
      CG().divide(fa.fix().right, fb.fix().right, f)
    )
}

@extension
interface DivisibleSumInstance<F, G> : Divisible<SumPartialOf<F, G>>, DivideSumInstance<F, G> {
  override fun CF(): Divisible<F>
  override fun CG(): Divisible<G>

  override fun <A> conquer(): Kind<SumPartialOf<F, G>, A> =
    Sum(
      CF().conquer(),
      CG().conquer()
    )
}

@extension
interface DecidableSumInstance<F, G> : Decidable<SumPartialOf<F, G>>, DivisibleSumInstance<F, G> {
  override fun CF(): Decidable<F>
  override fun CG(): Decidable<G>

  override fun <A, B, Z> choose(fa: Kind<SumPartialOf<F, G>, A>, fb: Kind<SumPartialOf<F, G>, B>, f: (Z) -> Either<A, B>): Kind<SumPartialOf<F, G>, Z> =
    Sum(
      CF().choose(fa.fix().left, fb.fix().left, f),
      CG().choose(fa.fix().right, fb.fix().right, f)
    )
}

@extension
interface ComonadSumInstance<F, G> : Comonad<SumPartialOf<F, G>> {

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
interface FunctorSumInstance<F, G> : Functor<SumPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
      fix().map(FF(), FG(), f)
}

@extension
interface EqSumInstance<F, G, A> : Eq<Sum<F, G, A>> {
  fun EQF(): Eq<Kind<F, A>>
  fun EQG(): Eq<Kind<G, A>>

  override fun Sum<F, G, A>.eqv(b: Sum<F, G, A>): Boolean =
    EQF().run { left.eqv(b.left) } &&
      EQG().run { right.eqv(b.right) }
}

@extension
interface HashSumInstance<F, G, A> : Hash<Sum<F, G, A>>, EqSumInstance<F, G, A> {
  fun HF(): Hash<Kind<F, A>>
  fun HG(): Hash<Kind<G, A>>

  override fun EQF(): Eq<Kind<F, A>> = HF()
  override fun EQG(): Eq<Kind<G, A>> = HG()

  override fun Sum<F, G, A>.hash(): Int = 31 * HF().run { left.hash() } + HG().run { right.hash() }
}

class SumContext<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) : ComonadSumInstance<F, G> {
  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class SumContextPartiallyApplied<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: SumContext<F, G>.() -> A): A =
      f(SumContext(CF, CG))
}

fun <F, G> ForSum(CF: Comonad<F>, CG: Comonad<G>): SumContextPartiallyApplied<F, G> =
    SumContextPartiallyApplied(CF, CG)