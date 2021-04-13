package arrow.optics.predef

import arrow.optics.Fold
import arrow.optics.FoldF
import arrow.optics.FoldK
import arrow.optics.Optic
import arrow.optics.Optic_
import arrow.optics.compose
import arrow.optics.folding
import arrow.optics.internal.Applicative
import arrow.optics.internal.Kind

@JvmName("iterable_folded")
fun <S: Iterable<A>, A> Optic.Companion.folded(): Fold<S, A> =
  Optic.folding(object : FoldF<S, A> {
    override fun <F> invoke(AF: Applicative<F>, s: S, f: (A) -> Kind<F, Unit>): Kind<F, Unit> =
      s.fold(AF.pure(Unit)) { acc, a ->
        AF.ap(AF.map(acc) { { _: Unit -> Unit } }, f(a))
      }
  })

@JvmName("iterable_folded")
fun <K : FoldK, S, IA: Iterable<A>, A> Optic_<K, S, IA>.folded(): Optic_<FoldK, S, A> =
  compose(arrow.optics.Optic.folded<IA, A>())
