package arrow.test.laws

import arrow.Kind
import arrow.core.Const
import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.Tuple2
import arrow.core.const
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.eq
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.monoid
import arrow.core.fix
import arrow.core.toT
import arrow.core.value
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.mtl.typeclasses.ComposedApplicative
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Traverse
import io.kotlintest.properties.Gen
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

  fun <F> laws(TF: Traverse<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN = GENK.genK(Gen.intSmall())
    val EQ = EQK.liftEq(Int.eq())

    return FoldableLaws.laws(TF, GENK) +
        FunctorLaws.laws(TF, GENK, EQK) + listOf(
        Law("Traverse Laws: Identity") { TF.identityTraverse(TF, GEN, EQ) },
        Law("Traverse Laws: Sequential composition") { TF.sequentialComposition(GEN, EQ) },
        Law("Traverse Laws: Parallel composition") { TF.parallelComposition(GEN, EQ) },
        Law("Traverse Laws: FoldMap derived") { TF.foldMapDerived(GEN) },
        Law("Traverse Laws: Effect order preserved") { TF.effectOrderPreserved(GEN) }
      )
  }

  fun <F> Traverse<F>.identityTraverse(FF: Functor<F>, G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) = Id.applicative().run {
    val idApp = this
    forAll(Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)), G) { f: (Int) -> Kind<ForId, Int>, fa: Kind<F, Int> ->
      fa.traverse(idApp, f).extract().equalUnderTheLaw(FF.run {
        fa.map(f).map { it.extract() }
      }, EQ)
    }
  }

  fun <F> Traverse<F>.sequentialComposition(GEN: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) = Id.applicative().run {
    val idApp = this
    forAll(Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)),
      Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)),
      GEN) { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->

      val fa = fha.traverse(idApp, f).fix()
      val composed = fa.map { it.traverse(idApp, g) }.value().value()
      val expected = fha.traverse(ComposedApplicative(idApp, idApp)) { a: Int -> f(a).map(g).nest() }.unnest().extract().extract()
      composed.equalUnderTheLaw(expected, EQ)
    }
  }

  fun <F> Traverse<F>.parallelComposition(GEN: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) =
    forAll(Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)), Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)), GEN) { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->
      val TIA = object : Applicative<TIF> {
        override fun <A> just(a: A): Kind<TIF, A> =
          TIC(Id(a) toT Id(a))

        override fun <A, B> Kind<TIF, A>.ap(ff: Kind<TIF, (A) -> B>): Kind<TIF, B> {
          val (fam, fan) = fix().ti
          val (fm, fn) = ff.fix().ti
          return TIC(Id.applicative().run { fam.ap(fm) toT fan.ap(fn) })
        }
      }

      val TIEQ: Eq<TI<Kind<F, Int>>> = Eq { a, b ->
        with(EQ) {
          a.a.extract().eqv(b.a.extract()) && a.b.extract().eqv(b.b.extract())
        }
      }

      val seen: TI<Kind<F, Int>> = fha.traverse(TIA) { TIC(f(it) toT g(it)) }.fix().ti
      val expected: TI<Kind<F, Int>> = TIC(fha.traverse(Id.applicative(), f) toT fha.traverse(Id.applicative(), g)).ti

      seen.equalUnderTheLaw(expected, TIEQ)
    }

  fun <F> Traverse<F>.foldMapDerived(GEN: Gen<Kind<F, Int>>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), GEN) { f: (Int) -> Int, fa: Kind<F, Int> ->
      val traversed = fa.traverse(Const.applicative(Int.monoid())) { a -> f(a).const() }.value()
      val mapped = fa.foldMap(Int.monoid(), f)
      mapped.equalUnderTheLaw(traversed, Eq.any())
    }

  fun <F, A> Traverse<F>.effectOrderPreserved(GEN: Gen<Kind<F, A>>) = IO.concurrent().run {
    forAll(GEN) { fa: Kind<F, A> ->
      val foldableOrder = fa.foldLeft(emptyList()) { xs: List<A>, x: A -> xs + x }
      val effectOrder = Ref<List<A>>(emptyList()).flatMap { ref ->
        fa.traverse(IO.applicative()) { a -> ref.update { it + a } }.followedBy(ref.get())
      }.unsafeRunSync()
      effectOrder.equalUnderTheLaw(foldableOrder, Eq.any())
    }
  }
}
