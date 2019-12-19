package arrow.test.laws

import arrow.Kind
import arrow.test.laws.BimonadLaws.coflatMapComposition
import arrow.test.laws.BimonadLaws.extractFlatMap
import arrow.test.laws.BimonadLaws.extractIsIdentity
import arrow.typeclasses.Apply
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BimonadLaws {

  private fun <F> bimonadLaws(
    BF: Bimonad<F>,
    EQ2: Eq<Kind<F, Kind<F, Int>>>,
    EQ3: Eq<Int>
  ): List<Law> =
    listOf(
      Law("Bimonad Laws: Extract Identity") { BF.extractIsIdentity(EQ3) },
      Law("Bimonad Laws: CoflatMap Composition") { BF.coflatMapComposition(EQ2) },
      Law("Bimonad Laws: Extract FlatMap") { BF.extractFlatMap(EQ3) }
    )

  fun <F> laws(
    BF: Bimonad<F>,
    M: Monad<F>,
    CM: Comonad<F>,
    f: (Int) -> Kind<F, Int>,
    EQ1: Eq<Kind<F, Int>>,
    EQ2: Eq<Kind<F, Kind<F, Int>>>,
    EQ3: Eq<Int>
  ): List<Law> =
    MonadLaws.laws(M, EQ1) +
      ComonadLaws.laws(CM, f, EQ1) +
      bimonadLaws(BF, EQ2, EQ3)

  fun <F> laws(
    BF: Bimonad<F>,
    M: Monad<F>,
    CM: Comonad<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    f: (Int) -> Kind<F, Int>,
    EQ1: Eq<Kind<F, Int>>,
    EQ2: Eq<Kind<F, Kind<F, Int>>>,
    EQ3: Eq<Int>
  ): List<Law> =
    MonadLaws.laws(M, FF, AP, SL, EQ1) +
      ComonadLaws.laws(CM, f, EQ1) +
      bimonadLaws(BF, EQ2, EQ3)

  fun <F> Bimonad<F>.extractIsIdentity(EQ: Eq<Int>): Unit =
    forAll(
      Gen.int()
    ) { a ->
      just(a).extract().equalUnderTheLaw(a, EQ)
    }

  fun <F> Bimonad<F>.extractFlatMap(EQ: Eq<Int>): Unit =
    forAll(
      Gen.int()
    ) { ffa ->
      just(just(ffa)).flatten().extract().equalUnderTheLaw(just(just(ffa)).map { it.extract() }.extract(), EQ)
    }

  fun <F> Bimonad<F>.coflatMapComposition(EQ: Eq<Kind<F, Kind<F, Int>>>): Unit =
    forAll(
      Gen.int()
    ) { a ->
      just(a).coflatMap { it }.equalUnderTheLaw(just(a).map { just(it) }, EQ) &&
        just(a).coflatMap { it }.equalUnderTheLaw(just(a).duplicate(), EQ)
    }
}
