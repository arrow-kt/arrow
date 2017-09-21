package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.IntMonoid
import kategory.ListKWHK
import kategory.ListKWKind
import kategory.ListKWMonoidInstance
import kategory.UnitSpec
import kategory.k
import kategory.none
import kategory.some
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FoldTest : UnitSpec() {

    init {

        val intFold = Fold.fromFoldable<ListKWHK, Int>()
        val stringFold = Fold.fromFoldable<ListKWHK, String>()

        "fold select" {
            val select = Fold.select<List<Int>> { it.contains(1) }

            forAll(Gen.list(Gen.int()), { ints ->
                select.getAll<List<Int>, List<Int>>(object : ListKWMonoidInstance<List<Int>> {}, a = ints).list.firstOrNull() ==
                        ints.let { if (it.contains(1)) it else null }
            })
        }

        "folding a list of ints" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.fold(a = ints.k()) == ints.sum()
            })
        }

        "folding and mapping a list of strings" {
            forAll(Gen.list(Gen.int()), { ints ->
                stringFold.foldMap(IntMonoid, ints.map(Int::toString).k(), String::toInt) == ints.sum()
            })
        }

        "getting forall values" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.getAll<ListKWKind<Int>, Int>(object : ListKWMonoidInstance<Int> {}, ints.k()) == ints.k()
            })
        }

        "Getting the length" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.length(ints.k()) == ints.size
            })
        }

        "find" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.find(ints.k()) { it > 10 } == ints.firstOrNull { it > 2 }?.some() ?: none()
            })
        }

        "forall" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.forall(ints.k()) { it % 2 == 0 } == ints.all { it % 2 == 0 }
            })
        }

        "empty" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.isEmpty(ints.k()) == ints.isEmpty()
            })
        }

        "nonEmpty" {
            forAll(Gen.list(Gen.int()), { ints ->
                intFold.nonEmpty(ints.k()) == ints.isNotEmpty()
            })
        }

    }

}