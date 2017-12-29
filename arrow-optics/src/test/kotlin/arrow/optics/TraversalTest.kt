package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.*
import arrow.core.Option
import arrow.core.toT
import arrow.data.k
import org.junit.runner.RunWith
import arrow.optics.PTraversal.Companion.fromTraversable

@RunWith(KTestJUnitRunner::class)
class TraversalTest : UnitSpec() {

    init {

        testLaws(
            TraversalLaws.laws(
                traversal = fromTraversable(),
                aGen = Gen.create { Gen.list(Gen.int()).generate().k() },
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
                aGen = genTuple(Gen.float(), Gen.float()),
                bGen = Gen.float(),
                funcGen = genFunctionAToB(Gen.float()),
                EQA = Eq.any(),
                EQB = Eq.any())
        )

        "Getting all targets of a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ListKWHK, Int, Int>().getAll(ints.k()) == ints.k()
            })
        }

        "Folding all the values of a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ListKWHK, Int, Int>().fold(ints.k()) == ints.sum()
            })
        }

        "Combining all the values of a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ListKWHK, Int, Int>().combineAll(ints.k()) == ints.sum()
            })
        }

        "Finding an number larger than 10" {
            forAll(Gen.list(Gen.choose(-100, 100)), { ints ->
                fromTraversable<ListKWHK, Int, Int>().find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
            })
        }

        "Get the length from a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ListKWHK, Int, Int>().size(ints.k()) == ints.size
            })
        }

    }

}
