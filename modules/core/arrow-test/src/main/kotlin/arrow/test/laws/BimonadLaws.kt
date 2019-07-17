package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BimonadLaws {
  fun <F> laws(BF: Bimonad<F>,
               f1: (Int) -> Kind<F, Int>,
               EQ1: Eq<Int>,
               f2: (Int) -> Kind<F, Kind<F, Int>>,
               EQ2: Eq<Kind<F, Kind<F, Int>>>): List<Law> =
    listOf(
      Law("Bimonad Laws: Extract Identity") { BF.extractIsIdentity(f1, EQ1) },
      Law("Bimonad Laws: CoflatMap Composition") { BF.coflatMapComposition(f1, EQ2) },
      Law("Bimonad Laws: Extract FlatMap") { BF.extractFlatMap(f2, EQ1) }
    )

  fun <F> Bimonad<F>.extractIsIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Int>): Unit =
    forAll(
      Gen.int().map(f),
      Gen.int()
    ) { fa, a ->
      fa.extract().equalUnderTheLaw(a, EQ)
    }

  fun <F> Bimonad<F>.extractFlatMap(f: (Int) -> Kind<F, Kind<F, Int>>, EQ: Eq<Int>): Unit =
    TODO()

  fun <F> Bimonad<F>.coflatMapComposition(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Kind<F, Int>>>): Unit =
    TODO()
}
