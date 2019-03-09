package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoidal
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidalLaws {

  fun <F> laws(MDAL: Monoidal<F>, AP: Applicative<F>, EQ: Eq<Kind<F,  Tuple2<Int, Int>>>): List<Law> =
    listOf(
      Law("Monoidal Laws: Left identity") { MDAL.monoidalLeftIdentity(AP::just, EQ) },
      Law("Monoidal Laws: Right identity") { MDAL.monoidalRightIdentity(AP::just, EQ) }
    )

  fun <F> laws(MDAL: Monoidal<F>, f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>): List<Law> =
    listOf(
      Law("Monoidal Laws: Left identity") { MDAL.monoidalLeftIdentity(f, EQ) },
      Law("Monoidal Laws: Right identity") { MDAL.monoidalRightIdentity(f, EQ) }
    )

  private fun <F> Monoidal<F>.monoidalLeftIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>): Unit =
    forAll(Gen.int().map(f)) { fa: Kind<F, Int> ->
      identity<Int>().product(fa).equalUnderTheLaw(identity(), EQ)
    }

  private fun <F> Monoidal<F>.monoidalRightIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>): Unit =
    forAll(Gen.int().map(f)) { fa: Kind<F, Int> ->
      fa.product(identity<Int>()).equalUnderTheLaw(identity(), EQ)
    }
}
