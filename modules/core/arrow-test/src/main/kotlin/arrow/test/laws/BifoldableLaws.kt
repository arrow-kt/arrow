package arrow.test.laws

import arrow.Kind2
import arrow.core.Eval
import arrow.core.extensions.monoid
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BifoldableLaws {

  fun <F> laws(BF: Bifoldable<F>, cf: Gen<Kind2<F, Int, Int>>, EQ: Eq<Int>): List<Law> =
    listOf(
      Law("Bifoldable Laws: Left bifold consistent with BifoldMap") { BF.bifoldLeftConsistentWithBifoldMap(cf, EQ) },
      Law("Bifoldable Laws: Right bifold consistent with BifoldMap") { BF.bifoldRightConsistentWithBifoldMap(cf, EQ) }
    )

  fun <F> Bifoldable<F>.bifoldLeftConsistentWithBifoldMap(cf: Gen<Kind2<F, Int, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.functionAToB<Int, Int>(Gen.intSmall()), cf
    ) { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
      with(Int.monoid()) {
        val expected = fab.bifoldLeft(Int.monoid().empty(), { c: Int, a: Int -> c.combine(f(a)) },
          { c: Int, b: Int -> c.combine(g(b)) })
        expected.equalUnderTheLaw(fab.bifoldMap(this, f, g), EQ)
      }
    }

  fun <F> Bifoldable<F>.bifoldRightConsistentWithBifoldMap(cf: Gen<Kind2<F, Int, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.functionAToB<Int, Int>(Gen.intSmall()), cf
    ) { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
      with(Int.monoid()) {
        val expected = fab.bifoldRight(Eval.Later { Int.monoid().empty() }, { a: Int, ec: Eval<Int> -> ec.map { c -> f(a).combine(c) } },
          { b: Int, ec: Eval<Int> -> ec.map { c -> g(b).combine(c) } })
        expected.value().equalUnderTheLaw(fab.bifoldMap(this, f, g), EQ)
      }
    }
}
