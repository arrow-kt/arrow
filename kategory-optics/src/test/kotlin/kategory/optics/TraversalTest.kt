package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Eq
import kategory.ListKWHK
import kategory.TraversalLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genTuple
import kategory.k
import kategory.none
import kategory.some
import kategory.toT
import org.junit.runner.RunWith
import kategory.optics.PTraversal.Companion.fromTraversable

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
                fromTraversable<ListKWHK, Int, Int>().find(ints.k()) { it > 10 } == ints.firstOrNull { it > 10 }?.some() ?: none<Int>()
            })
        }

        "Get the length from a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ListKWHK, Int, Int>().size(ints.k()) == ints.size
            })
        }

    }

}
