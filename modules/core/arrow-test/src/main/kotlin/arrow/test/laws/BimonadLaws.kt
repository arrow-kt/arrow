package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BimonadLaws {

  fun <F> laws(
    BF: Bimonad<F>,
    M: Monad<F>,
    CM: Comonad<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val EQ1 = EQK.liftEq(Int.eq())
    val EQ2 = EQK.liftEq(EQ1)
    val EQ3 = Int.eq()

    val GEN = GENK.genK(Gen.int())

    return MonadLaws.laws(M, EQK) +
      ComonadLaws.laws(CM, GEN, EQK) +
      listOf(
        Law("Bimonad Laws: Extract Identity") { BF.extractIsIdentity(Gen.int(), EQ3) },
        Law("Bimonad Laws: CoflatMap Composition") { BF.coflatMapComposition(Gen.int(), EQ2) },
        Law("Bimonad Laws: Extract FlatMap") { BF.extractFlatMap(Gen.int(), EQ3) }
      )
  }

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
