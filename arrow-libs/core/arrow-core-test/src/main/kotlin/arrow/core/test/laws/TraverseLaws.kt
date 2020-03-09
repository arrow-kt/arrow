package arrow.core.test.laws

import arrow.Kind
import arrow.core.Const
import arrow.core.Eval
import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.Tuple2
import arrow.core.const
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.eq
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.monoid
import arrow.core.fix
import arrow.core.toT
import arrow.core.value
import arrow.core.test.generators.GenK
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
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

class TIF private constructor()

interface Nested<out F, out G>

fun <F, G, A> Kind<F, Kind<G, A>>.nest(): Kind<Nested<F, G>, A> = this as Kind<Nested<F, G>, A>
fun <F, G, A> Kind<Nested<F, G>, A>.unnest(): Kind<F, Kind<G, A>> = this as Kind<F, Kind<G, A>>

fun <F, G> ComposedApplicative(apF: Applicative<F>, apG: Applicative<G>): Applicative<Nested<F, G>> = object : Applicative<Nested<F, G>> {
  override fun <A, B> Kind<Nested<F, G>, A>.ap(ff: Kind<Nested<F, G>, (A) -> B>): Kind<Nested<F, G>, B> =
    apF.run { unnest().ap(ff.unnest().map { gf -> { ga: Kind<G, A> -> apG.run { ga.ap(gf) } } }).nest() }

  override fun <A> just(a: A): Kind<Nested<F, G>, A> = apF.just(apG.just(a)).nest()
}

object TraverseLaws {
  fun <F> laws(TF: Traverse<F>, GA: Applicative<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN = GENK.genK(Gen.intSmall())
    val EQ = EQK.liftEq(Int.eq())

    return FoldableLaws.laws(TF, GA, GENK, EQK) +
      FunctorLaws.laws(TF, GENK, EQK) + listOf(
      Law("Traverse Laws: Identity") { TF.identityTraverse(TF, GEN, EQ) },
      Law("Traverse Laws: Sequential composition") { TF.sequentialComposition(GEN, EQ) },
      Law("Traverse Laws: Parallel composition") { TF.parallelComposition(GEN, EQ) },
      Law("Traverse Laws: FoldMap derived") { TF.foldMapDerived(GEN) },
      Law("Traverse Laws: Traverse is left to right") { TF.leftToRight(GEN) }
    )
  }

  fun <F> laws(TF: Traverse<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN = GENK.genK(Gen.intSmall())
    val EQ = EQK.liftEq(Int.eq())

    return FoldableLaws.laws(TF, GENK) +
      FunctorLaws.laws(TF, GENK, EQK) + listOf(
      Law("Traverse Laws: Identity") { TF.identityTraverse(TF, GEN, EQ) },
      Law("Traverse Laws: Sequential composition") { TF.sequentialComposition(GEN, EQ) },
      Law("Traverse Laws: Parallel composition") { TF.parallelComposition(GEN, EQ) },
      Law("Traverse Laws: FoldMap derived") { TF.foldMapDerived(GEN) },
      Law("Traverse Laws: Traverse is left to right") { TF.leftToRight(GEN) }
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

  fun <F> Traverse<F>.leftToRight(GEN: Gen<Kind<F, Int>>) =
    forAll(GEN) { fa ->
      val mutable = mutableListOf<Int>()
      fa.traverse(Eval.applicative()) { mutable.add(it); Eval.now(Unit) }.value()

      mutable.equalUnderTheLaw(fa.toList(), Eq.any())
    }
}
