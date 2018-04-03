package arrow.test.laws

import arrow.Kind
import arrow.core.*
import arrow.data.value
import arrow.free.Const
import arrow.free.const
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.*
import io.kotlintest.properties.forAll

typealias TI<A> = Tuple2<IdOf<A>, IdOf<A>>

typealias TIK<A> = Kind<TIF, A>

@Suppress("UNCHECKED_CAST")
fun <A> TIK<A>.fix(): TIC<A> =
  this as TIC<A>

data class TIC<out A>(val ti: TI<A>) : TIK<A>

class TIF {
  private constructor()
}

object TraverseLaws {
  // FIXME(paco): this implementation will crash the inliner. Wait for fix: https://youtrack.jetbrains.com/issue/KT-18660
  /*
  inline fun <F> laws(TF: Traverse<F>, AF: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
      FoldableLaws.laws(TF, { AF.just(it) }, Eq.any()) + FunctorLaws.laws(AF, EQ) + listOf(
              Law("Traverse Laws: Identity", { identityTraverse(TF, AF, { AF.just(it) }, EQ) }),
              Law("Traverse Laws: Sequential composition", { sequentialComposition(TF, { AF.just(it) }, EQ) }),
              Law("Traverse Laws: Parallel composition", { parallelComposition(TF, { AF.just(it) }, EQ) }),
              Law("Traverse Laws: FoldMap derived", { foldMapDerived(TF, { AF.just(it) }) })
      )
  */

  inline fun <F> laws(TF: Traverse<F>, FF: Functor<F>, noinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    FoldableLaws.laws(TF, cf, Eq.any()) + FunctorLaws.laws(FF, cf, EQ) + listOf(
      Law("Traverse Laws: Identity", { TF.identityTraverse(FF, cf, EQ) }),
      Law("Traverse Laws: Sequential composition", { TF.sequentialComposition(cf, EQ) }),
      Law("Traverse Laws: Parallel composition", { TF.parallelComposition(cf, EQ) }),
      Law("Traverse Laws: FoldMap derived", { TF.foldMapDerived(cf) })
    )

  fun <F> Traverse<F>.identityTraverse(FF: Functor<F>, cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) = Id.applicative().run {
    forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, fa: Kind<F, Int> ->
      fa.traverse(this, f).value().equalUnderTheLaw(FF.run { fa.map(f).map { it.value() } }, EQ)
    })
  }

  fun <F> Traverse<F>.sequentialComposition(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) = Id.applicative().run {
    forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->

      val fa = fha.traverse(this, f).fix()
      val composed = fa.map({ it.traverse(this, g) }).value.value()
      val expected = fha.traverse(ComposedApplicative(this, this), { a: Int -> f(a).map(g).nest() }).unnest().value().value()
      composed.equalUnderTheLaw(expected, EQ)
    })
  }

  fun <F> Traverse<F>.parallelComposition(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
    forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->
      val TIA = object : Applicative<TIF> {
        override fun <A> just(a: A): Kind<TIF, A> =
          TIC(Id(a) toT Id(a))

        override fun <A, B> Kind<TIF, A>.ap(ff: Kind<TIF, (A) -> B>): Kind<TIF, B> {
          val (fam, fan) = fix().ti
          val (fm, fn) = ff.fix().ti
          return TIC(Id.applicative().run { fam.ap(fm) toT fan.ap(fn) })
        }

      }

      val TIEQ: Eq<TI<Kind<F, Int>>> = Eq<TI<Kind<F, Int>>> { a, b ->
        with(EQ) {
          a.a.value().eqv(b.a.value()) && a.b.value().eqv(b.b.value())
        }
      }

      val seen: TI<Kind<F, Int>> = fha.traverse(TIA, { TIC(f(it) toT g(it)) }).fix().ti
      val expected: TI<Kind<F, Int>> = TIC(fha.traverse(Id.applicative(), f) toT fha.traverse(Id.applicative(), g)).ti

      seen.equalUnderTheLaw(expected, TIEQ)
    })

  fun <F> Traverse<F>.foldMapDerived(cf: (Int) -> Kind<F, Int>) =
    forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
      val traversed = fa.traverse(Const.applicative(Int.monoid()), { a -> f(a).const() }).value()
      val mapped = fa.foldMap(Int.monoid(), f)
      mapped.equalUnderTheLaw(traversed, Eq.any())
    })
}
