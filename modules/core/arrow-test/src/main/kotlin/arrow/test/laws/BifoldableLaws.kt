package arrow.test.laws

import arrow.*
import arrow.core.Eval
import arrow.instances.IntMonoid
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Eq
import arrow.typeclasses.bifoldable
import io.kotlintest.properties.forAll

object BifoldableLaws {
    inline fun <reified F> laws(BF: Bifoldable<F> = bifoldable<F>(), crossinline cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Int>): List<Law> =
            listOf(
                    Law("Bifoldable Laws: Left bifold consistent with BifoldMap", { bifoldLeftConsistentWithBifoldMap(BF, cf, EQ) }),
                    Law("Bifoldable Laws: Right bifold consistent with BifoldMap", { bifoldRightConsistentWithBifoldMap(BF, cf, EQ) })
            )

    inline fun <reified F> bifoldLeftConsistentWithBifoldMap(BF: Bifoldable<F>, crossinline cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf),
                    { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
                        val expected = BF.bifoldLeft(fab, IntMonoid.empty(),
                                { c: Int, a: Int -> IntMonoid.combine(c, f(a)) },
                                { c: Int, b: Int -> IntMonoid.combine(c, g(b)) })
                        expected.equalUnderTheLaw(BF.bifoldMap(fab, f, g, IntMonoid), EQ)
                    })

    inline fun <reified F> bifoldRightConsistentWithBifoldMap(BF: Bifoldable<F>, crossinline cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf),
                    { f: (Int) -> Int, g: (Int) -> Int, fab: Kind2<F, Int, Int> ->
                        val expected = BF.bifoldRight(fab, Eval.Later({ IntMonoid.empty() }),
                                { a: Int, ec: Eval<Int> -> ec.map({ c -> IntMonoid.combine(f(a), c) }) },
                                { b: Int, ec: Eval<Int> -> ec.map({ c -> IntMonoid.combine(g(b), c) }) })
                        expected.value().equalUnderTheLaw(BF.bifoldMap(fab, f, g, IntMonoid), EQ)
                    })
}
