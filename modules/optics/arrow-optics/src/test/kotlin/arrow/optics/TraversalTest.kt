package arrow.optics

import arrow.core.Option
import arrow.core.eq
import arrow.core.toT
import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.eq
import arrow.data.k
import arrow.optics.PTraversal.Companion.fromTraversable
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genTuple
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TraversalTest : UnitSpec() {

    init {

        testLaws(TraversalLaws.laws(
                        traversal = fromTraversable(),
                        aGen = Gen.create { Gen.list(Gen.int()).generate().k() },
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any(),
                        EQOptionB = Option.eq(Eq.any()),
                        EQListB = ListK.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                        traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
                        aGen = genTuple(Gen.float(), Gen.float()),
                        bGen = Gen.float(),
                        funcGen = genFunctionAToB(Gen.float()),
                        EQA = Eq.any(),
                        EQOptionB = Option.eq(Eq.any()),
                        EQListB = ListK.eq(Eq.any())
        ))

        "Getting all targets of a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ForListK, Int, Int>().getAll(ints.k()) == ints.k()
            })
        }

        "Folding all the values of a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ForListK, Int, Int>().fold(ints.k()) == ints.sum()
            })
        }

        "Combining all the values of a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ForListK, Int, Int>().combineAll(ints.k()) == ints.sum()
            })
        }

        "Finding an number larger than 10" {
            forAll(Gen.list(Gen.choose(-100, 100)), { ints ->
                fromTraversable<ForListK, Int, Int>().find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
            })
        }

        "Get the length from a traversal" {
            forAll(Gen.list(Gen.int()), { ints ->
                fromTraversable<ForListK, Int, Int>().size(ints.k()) == ints.size
            })
        }

    }

}
