package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object OrderLaws {

    inline fun <reified F> laws(O: Order<F>, fGen: Gen<F>, funcGen: Gen<(F) -> F>): List<Law> =
            EqLaws.laws(O, { fGen.generate() }) + listOf(
                    Law("Order law: reflexivity equality", { O.reflexitivityEq(fGen) }),
                    Law("Order law: symmetry equality", { O.symmetryEq(fGen) }),
                    Law("Order law: antisymmetry equality", { O.antisymmetryEq(fGen, funcGen) }),
                    Law("Order law: transitivity equality", { O.transitivityEq(fGen) }),
                    Law("Order law: reflexivity partial order", { O.reflexivityPartialOrder(fGen) }),
                    Law("Order law: antisymmetry partial order", { O.antisymmetryPartialOrder(fGen) }),
                    Law("Order law: transitivity partial order", { O.transitivityPartialOrder(fGen) }),
                    Law("Order law: greater than or equal partial order", { O.greaterThanOrEqualPartialOrder(fGen) }),
                    Law("Order law: lesser than partial order", { O.lesserThanPartialOrder(fGen) }),
                    Law("Order law: greater than partial order", { O.greaterThanPartialOrder(fGen) }),
                    Law("Order law: totality order", { O.totalityOrder(fGen) }),
                    Law("Order law: compare order", { O.compareOrder(fGen) }),
                    Law("Order law: min order", { O.minOrder(fGen) }),
                    Law("Order law: max order", { O.maxOrder(fGen) })
            )

    inline fun <reified F> Order<F>.reflexitivityEq(fGen: Gen<F>) =
            forAll(fGen, { x ->
                x.equalUnderTheLaw(x, this)
            })

    inline fun <reified F> Order<F>.symmetryEq(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                x.eqv(y).equalUnderTheLaw(y.eqv(x), Eq.any())
            })

    inline fun <reified F> Order<F>.antisymmetryEq(fGen: Gen<F>, funcGen: Gen<(F) -> F>) =
            forAll(fGen, fGen, funcGen, { x, y, f ->
                !x.eqv(y) || f(x).eqv(f(y))
            })

    inline fun <reified F> Order<F>.transitivityEq(fGen: Gen<F>) =
            forAll(fGen, fGen, fGen, { x, y, z ->
                !(x.eqv(y) && y.eqv(z)) || x.eqv(z)
            })

    inline fun <reified F> Order<F>.reflexivityPartialOrder(fGen: Gen<F>) =
            forAll(fGen, { x ->
                x.lte(x)
            })

    inline fun <reified F> Order<F>.antisymmetryPartialOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                !(x.lte(y) && y.lte(x)) || x.eqv(y)
            })

    inline fun <reified F> Order<F>.transitivityPartialOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, fGen, { x, y, z ->
                !(x.lte(y) && y.lte(x)) || x.lte(z)
            })

    inline fun <reified F> Order<F>.greaterThanOrEqualPartialOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                x.lte(y) == y.gte(x)
            })

    inline fun <reified F> Order<F>.lesserThanPartialOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                x.lt(y) == (x.lte(y) && x.neqv(y))
            })

    inline fun <reified F> Order<F>.greaterThanPartialOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                x.lt(y) == y.gt(x)
            })

    inline fun <reified F> Order<F>.totalityOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                x.lte(y) || y.lte(x)
            })

    inline fun <reified F> Order<F>.compareOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                val c = x.compare(y)
                ((c < 0) == x.lt(y)) && ((c == 0) == x.eqv(y)) && ((c > 0) == x.gt(y))
            })

    inline fun <reified F> Order<F>.minOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                val c = x.compare(y)
                val m = x.min(y)
                if (c < 0) m == x
                else if (c == 0) (m == x) && (m == y)
                else m == y
            })

    inline fun <reified F> Order<F>.maxOrder(fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                val c = x.compare(y)
                val m = x.max(y)
                if (c < 0) m == y
                else if (c == 0) (m == x) && (m == y)
                else m == x
            })
}
