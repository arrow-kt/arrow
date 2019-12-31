package arrow.test.laws

import arrow.Kind2
import arrow.core.Eval
import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.test.generators.GenK2
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BifoldableLaws {

  fun <F> laws(BF: Bifoldable<F>, GENK: GenK2<F>): List<Law> {

    val EQ = Int.eq()
    val GEN = GENK.genK(Gen.int(), Gen.int())

    return listOf(
      Law("Bifoldable Laws: Left bifold consistent with BifoldMap") { BF.bifoldLeftConsistentWithBifoldMap(GEN, EQ) },
      Law("Bifoldable Laws: Right bifold consistent with BifoldMap") { BF.bifoldRightConsistentWithBifoldMap(GEN, EQ) }
    )
  }

  fun <F> Bifoldable<F>.bifoldLeftConsistentWithBifoldMap(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.functionAToB<Int, Int>(Gen.intSmall()), G
    ) { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
      with(Int.monoid()) {
        val expected = fab.bifoldLeft(Int.monoid().empty(), { c: Int, a: Int -> c.combine(f(a)) },
          { c: Int, b: Int -> c.combine(g(b)) })
        expected.equalUnderTheLaw(fab.bifoldMap(this, f, g), EQ)
      }
    }

  fun <F> Bifoldable<F>.bifoldRightConsistentWithBifoldMap(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.functionAToB<Int, Int>(Gen.intSmall()), G
    ) { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
      with(Int.monoid()) {
        val expected = fab.bifoldRight(Eval.Later { Int.monoid().empty() }, { a: Int, ec: Eval<Int> -> ec.map { c -> f(a).combine(c) } },
          { b: Int, ec: Eval<Int> -> ec.map { c -> g(b).combine(c) } })
        expected.value().equalUnderTheLaw(fab.bifoldMap(this, f, g), EQ)
      }
    }
}
