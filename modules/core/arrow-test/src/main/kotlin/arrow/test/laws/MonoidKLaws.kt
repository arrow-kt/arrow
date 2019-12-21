package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.list.foldable.fold
import arrow.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.MonoidK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidKLaws {

  fun <F> laws(SGK: MonoidK<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN = GENK.genK(Gen.int())
    val EQ = EQK.liftEq(Int.eq())

    return SemigroupKLaws.laws(SGK, GENK, EQK) + listOf(
        Law("MonoidK Laws: Left identity") { SGK.monoidKLeftIdentity(GEN, EQ) },
        Law("MonoidK Laws: Right identity") { SGK.monoidKRightIdentity(GEN, EQ) },
        Law("MonoidK Laws: Fold with Monoid instance") { SGK.monoidKFold(GEN, EQ) })
  }

  fun <F> MonoidK<F>.monoidKLeftIdentity(GEN: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GEN) { fa: Kind<F, Int> ->
      empty<Int>().combineK(fa).equalUnderTheLaw(fa, EQ)
    }

  fun <F> MonoidK<F>.monoidKRightIdentity(GEN: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GEN) { fa: Kind<F, Int> ->
      fa.combineK(empty<Int>()).equalUnderTheLaw(fa, EQ)
    }

  fun <F> MonoidK<F>.monoidKFold(GEN: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val mo = this
    forAll(GEN) { fa: Kind<F, Int> ->
      listOf(fa).fold(mo.algebra()).equalUnderTheLaw(fa, EQ)
    }
  }
}
