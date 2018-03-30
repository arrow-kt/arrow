package arrow.test.laws

import arrow.Kind2
import arrow.core.Eval
import arrow.instances.IntMonoidInstance
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Eq
import io.kotlintest.properties.forAll

object BifoldableLaws {
  inline fun <F> laws(BF: Bifoldable<F>, noinline cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Int>): List<Law> =
    listOf(
      Law("Bifoldable Laws: Left bifold consistent with BifoldMap", { BF.bifoldLeftConsistentWithBifoldMap(cf, EQ) }),
      Law("Bifoldable Laws: Right bifold consistent with BifoldMap", { BF.bifoldRightConsistentWithBifoldMap(cf, EQ) })
    )

  fun <F> Bifoldable<F>.bifoldLeftConsistentWithBifoldMap(cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Int>) =
    forAll(genFunctionAToB<Int, Int>(genIntSmall()), genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf),
      { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
        with(IntMonoidInstance) {
          val expected = fab.bifoldLeft(IntMonoidInstance.empty(), { c: Int, a: Int -> c.combine(f(a)) },
            { c: Int, b: Int -> c.combine(g(b)) })
          expected.equalUnderTheLaw(fab.bifoldMap(this, f, g), EQ)
        }
      })

  fun <F> Bifoldable<F>.bifoldRightConsistentWithBifoldMap(cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Int>) =
    forAll(genFunctionAToB<Int, Int>(genIntSmall()), genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf),
      { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
        with(IntMonoidInstance) {
          val expected = fab.bifoldRight(Eval.Later({ IntMonoidInstance.empty() }), { a: Int, ec: Eval<Int> -> ec.map({ c -> f(a).combine(c) }) },
            { b: Int, ec: Eval<Int> -> ec.map({ c -> g(b).combine(c) }) })
          expected.value().equalUnderTheLaw(fab.bifoldMap(this, f, g), EQ)
        }
      })
}
