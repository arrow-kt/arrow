package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.typeclasses.Apply
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BimonadLaws {

  private fun <F> bimonadLaws(
    BF: Bimonad<F>,
    EQK: EqK<F>
  ): List<Law> {
    val GEN = Gen.int()

    val EQ1 = EQK.liftEq(Int.eq())
    val EQ2 = EQK.liftEq(EQ1)
    val EQ3 = Int.eq()

    return listOf(
      Law("Bimonad Laws: Extract Identity") { BF.extractIsIdentity(GEN, EQ3) },
      Law("Bimonad Laws: CoflatMap Composition") { BF.coflatMapComposition(GEN, EQ2) },
      Law("Bimonad Laws: Extract FlatMap") { BF.extractFlatMap(GEN, EQ3) }
    )
  }

  fun <F> laws(
    BF: Bimonad<F>,
    M: Monad<F>,
    CM: Comonad<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(M, GENK, EQK) +
      ComonadLaws.laws(CM, GENK, EQK) +
      bimonadLaws(BF, EQK)

  fun <F> laws(
    BF: Bimonad<F>,
    M: Monad<F>,
    CM: Comonad<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(M, FF, AP, SL, GENK, EQK) +
      ComonadLaws.laws(CM, GENK, EQK) +
      bimonadLaws(BF, EQK)

  fun <F, A> Bimonad<F>.extractIsIdentity(G: Gen<A>, EQ: Eq<A>): Unit =
    forAll(
      G
    ) { a ->
      just(a).extract().equalUnderTheLaw(a, EQ)
    }

  fun <F, A> Bimonad<F>.extractFlatMap(G: Gen<A>, EQ: Eq<A>): Unit =
    forAll(
      G
    ) { a ->
      just(just(a)).flatten().extract().equalUnderTheLaw(just(just(a)).map { it.extract() }.extract(), EQ)
    }

  fun <F, A> Bimonad<F>.coflatMapComposition(G: Gen<A>, EQ: Eq<Kind<F, Kind<F, A>>>): Unit =
    forAll(
      G
    ) { a ->
      just(a).coflatMap { it }.equalUnderTheLaw(just(a).map { just(it) }, EQ) &&
        just(a).coflatMap { it }.equalUnderTheLaw(just(a).duplicate(), EQ)
    }
}
