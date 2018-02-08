package arrow.optics

import arrow.core.Option
import arrow.core.eq
import arrow.core.toT
import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.eq
import arrow.data.k
import arrow.optics.PTraversal.Companion.fromTraversable
import arrow.syntax.collections.firstOption
import arrow.syntax.option.toOption
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListK
import arrow.test.generators.genTuple
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TraversalTest : UnitSpec() {

    init {

        testLaws(
                TraversalLaws.laws(
                        traversal = fromTraversable(),
                        aGen = genListK(Gen.int()),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any(),
                        EQOptionB = Option.eq(Eq.any()),
                        EQListB = ListK.eq(Eq.any())
                ),

                SetterLaws.laws(
                        setter = fromTraversable<ForListK, Int, Int>().asSetter(),
                        aGen = genListK(Gen.int()),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int())
                )
        )

        testLaws(TraversalLaws.laws(
                traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
                aGen = genTuple(Gen.float(), Gen.float()),
                bGen = Gen.float(),
                funcGen = genFunctionAToB(Gen.float())
        ))

        "asFold should behave as valid Fold: size" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().size(ints) == ints.size
            }
        }

        "asFold should behave as valid Fold: nonEmpty" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().nonEmpty(ints) == ints.isNotEmpty()
            }
        }

        "asFold should behave as valid Fold: isEmpty" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().isEmpty(ints) == ints.isEmpty()
            }
        }

        "asFold should behave as valid Fold: getAll" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().getAll(ints) == ints.k()
            }
        }

        "asFold should behave as valid Fold: combineAll" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().combineAll(ints) == ints.sum()
            }
        }

        "asFold should behave as valid Fold: fold" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().fold(ints) == ints.sum()
            }
        }

        "asFold should behave as valid Fold: headOption" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().headOption(ints) == ints.firstOption()
            }
        }

        "asFold should behave as valid Fold: lastOption" {
            forAll(genListK(Gen.int())) { ints ->
                fromTraversable<ForListK, Int, Int>().asFold().lastOption(ints) == ints.lastOrNull()?.toOption()
            }
        }

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
