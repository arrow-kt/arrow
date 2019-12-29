package arrow.test.laws

import arrow.Kind2
import arrow.core.andThen
import arrow.core.extensions.eq
import arrow.test.generators.GenK2
import arrow.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import arrow.typeclasses.Profunctor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ProfunctorLaws {

  fun <F> laws(PF: Profunctor<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> {

    val G = GENK.genK(Gen.int(), Gen.int())
    val EQ = EQK.liftEq(Int.eq(), Int.eq())

    return listOf(
      Law("Profunctor Laws: Identity") { PF.identity(G, EQ) },
      Law("Profunctor Laws: Composition") { PF.composition(G, EQ) },
      Law("Profunctor Laws: Lmap Identity") { PF.lMapIdentity(G, EQ) },
      Law("Profunctor Laws: Rmap Identity") { PF.rMapIdentity(G, EQ) },
      Law("Profunctor Laws: Lmap Composition") { PF.lMapComposition(G, EQ) },
      Law("Profunctor Laws: Rmap Composition") { PF.rMapComposition(G, EQ) }
      )
  }

  fun <F> Profunctor<F>.identity(f: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(f) { fa: Kind2<F, Int, Int> ->
      fa.dimap<Int, Int, Int, Int>({ it }, { it }).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Profunctor<F>.composition(f: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(
      f,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind2<F, Int, Int>, ff, g, x, y ->
      fa.dimap(ff, g).dimap(x, y).equalUnderTheLaw(fa.dimap(x andThen ff, g andThen y), EQ)
    }

  fun <F> Profunctor<F>.lMapIdentity(f: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(f) { fa: Kind2<F, Int, Int> ->
      fa.lmap<Int, Int, Int> { it }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Profunctor<F>.rMapIdentity(f: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(f) { fa: Kind2<F, Int, Int> ->
      fa.rmap { it }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Profunctor<F>.lMapComposition(f: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(
      f,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind2<F, Int, Int>, ff, g ->
      fa.lmap(g).lmap(ff).equalUnderTheLaw(fa.lmap(ff andThen g), EQ)
    }

  fun <F> Profunctor<F>.rMapComposition(f: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(
      f,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind2<F, Int, Int>, ff, g ->
      fa.lmap(ff).lmap(g).equalUnderTheLaw(fa.lmap(ff andThen g), EQ)
    }
}
