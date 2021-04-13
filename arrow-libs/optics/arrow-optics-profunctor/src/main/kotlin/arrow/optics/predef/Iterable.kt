package arrow.optics.predef

import arrow.optics.FoldK
import arrow.optics.IxFold
import arrow.optics.IxFoldF
import arrow.optics.Optic
import arrow.optics.Optic_
import arrow.optics.compose
import arrow.optics.icomposeLeft
import arrow.optics.internal.Applicative
import arrow.optics.internal.Kind
import arrow.optics.ixFolding

@JvmName("iterable_folded")
fun <S: Iterable<A>, A> Optic.Companion.folded(): IxFold<Int, S, A> =
  Optic.ixFolding(object : IxFoldF<Int, S, A> {
    override fun <F> invoke(AF: Applicative<F>, s: S, f: (Int, A) -> Kind<F, Unit>): Kind<F, Unit> =
      s.foldIndexed(AF.pure(Unit)) { i, acc, a ->
        AF.ap(AF.map(acc) { { _: Unit -> Unit } }, f(i, a))
      }
  })

@JvmName("iterable_folded")
fun <K : FoldK, I, S, IA: Iterable<A>, A> Optic_<K, I, S, IA>.folded(): Optic_<FoldK, I, S, A> =
  compose(Optic.folded<IA, A>())
