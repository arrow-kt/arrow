package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.core.Option
import arrow.core.eq
import arrow.core.getOrElse
import arrow.core.toT
import arrow.data.ListKW
import arrow.data.ListKWHK
import arrow.data.eq
import arrow.data.k
import org.junit.runner.RunWith
import arrow.optics.PTraversal.Companion.fromTraversable
import arrow.syntax.collections.firstOption
import arrow.syntax.foldable.combineAll
import arrow.syntax.option.toOption
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListKW
import arrow.test.generators.genTuple
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq

@RunWith(KTestJUnitRunner::class)
class TraversalTest : UnitSpec() {

    init {

        testLaws(
                TraversalLaws.laws(
                        traversal = fromTraversable(),
                        aGen = genListKW(Gen.int()),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int())
                ),

                SetterLaws.laws(
                        setter = fromTraversable<ListKWHK, Int, Int>().asSetter(),
                        aGen = genListKW(Gen.int()),
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
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().size(ints) == ints.size
            }
        }

        "asFold should behave as valid Fold: nonEmpty" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().nonEmpty(ints) == ints.isNotEmpty()
            }
        }

        "asFold should behave as valid Fold: isEmpty" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().isEmpty(ints) == ints.isEmpty()
            }
        }

        "asFold should behave as valid Fold: getAll" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().getAll(ints) == ints.k()
            }
        }

        "asFold should behave as valid Fold: combineAll" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().combineAll(ints) == ints.sum()
            }
        }

        "asFold should behave as valid Fold: fold" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().fold(ints) == ints.sum()
            }
        }

        "asFold should behave as valid Fold: headOption" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().headOption(ints) == ints.firstOption()
            }
        }

        "asFold should behave as valid Fold: lastOption" {
            forAll(genListKW(Gen.int())) { ints ->
                fromTraversable<ListKWHK, Int, Int>().asFold().lastOption(ints) == ints.lastOrNull()?.toOption()
            }
        }

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
