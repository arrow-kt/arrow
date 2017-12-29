package arrow

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll


object OrderLaws {

    inline fun <reified F> laws(O: Order<F> = order(), fGen: Gen<F>, funcGen: Gen<(F) -> F>): List<Law> = listOf(
            Law("Order law: reflexivity equality", { reflexitivityEq(O, fGen) }),
            Law("Order law: symmetry equality", { symmetryEq(O, fGen) }),
            Law("Order law: antisymmetry equality", { antisymmetryEq(O, fGen, funcGen) }),
            Law("Order law: transitivity equality", { transitivityEq(O, fGen) }),
            Law("Order law: reflexivity partial order", { reflexivityPartialOrder(O, fGen) }),
            Law("Order law: antisymmetry partial order", { antisymmetryPartialOrder(O, fGen) }),
            Law("Order law: transitivity partial order", { transitivityPartialOrder(O, fGen) }),
            Law("Order law: greater than or equal partial order", { greaterThanOrEqualPartialOrder(O, fGen) }),
            Law("Order law: lesser than partial order", { lesserThanPartialOrder(O, fGen) }),
            Law("Order law: greater than partial order", { greaterThanPartialOrder(O, fGen) }),
            Law("Order law: totality order", { totalityOrder(O, fGen) }),
            Law("Order law: compare order", { compareOrder(O, fGen) }),
            Law("Order law: min order", { minOrder(O, fGen) }),
            Law("Order law: max order", { maxOrder(O, fGen) })
    )

    inline fun <reified F> reflexitivityEq(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, { x ->
                x.equalUnderTheLaw(x, O)
            })

    inline fun <reified F> symmetryEq(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                O.eqv(x, y).equalUnderTheLaw(O.eqv(y, x), Eq.any())
            })

    inline fun <reified F> antisymmetryEq(O: Order<F>, fGen: Gen<F>, funcGen: Gen<(F) -> F>) =
            forAll(fGen, fGen, funcGen, { x, y, f ->
                !O.eqv(x, y) || O.eqv(f(x), f(y))
            })

    inline fun <reified F> transitivityEq(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, fGen, { x, y, z ->
                !(O.eqv(x, y) && O.eqv(y, z)) || O.eqv(x, z)
            })

    inline fun <reified F> reflexivityPartialOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, { x ->
                O.lte(x, x)
            })

    inline fun <reified F> antisymmetryPartialOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                !(O.lte(x, y) && O.lte(y, x)) || O.eqv(x, y)
            })

    inline fun <reified F> transitivityPartialOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, fGen, { x, y, z ->
                !(O.lte(x, y) && O.lte(y, x)) || O.lte(x, z)
            })

    inline fun <reified F> greaterThanOrEqualPartialOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                O.lte(x, y) == O.gte(y, x)
            })

    inline fun <reified F> lesserThanPartialOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                O.lt(x, y) == (O.lte(x, y) && O.neqv(x, y))
            })

    inline fun <reified F> greaterThanPartialOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                O.lt(x, y) == O.gt(y, x)
            })

    inline fun <reified F> totalityOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                O.lte(x, y) || O.lte(y, x)
            })

    inline fun <reified F> compareOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                val c = O.compare(x, y)
                ((c < 0) == O.lt(x, y)) && ((c == 0) == O.eqv(x, y)) && ((c > 0) == O.gt(x, y))
            })

    inline fun <reified F> minOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                val c = O.compare(x, y)
                val m = O.min(x, y)
                if (c < 0) m == x
                else if (c == 0) (m == x) && (m == y)
                else m == y
            })

    inline fun <reified F> maxOrder(O: Order<F>, fGen: Gen<F>) =
            forAll(fGen, fGen, { x, y ->
                val c = O.compare(x, y)
                val m = O.max(x, y)
                if (c < 0) m == y
                else if (c == 0) (m == x) && (m == y)
                else m == x
            })

}
