package kategory.data

import io.kotlintest.KTestJUnitRunner
import kategory.Eq
import kategory.ListKW
import kategory.TraverseLaws
import kategory.UnitSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListKWTest : UnitSpec() {
    init {
        val applicative = ListKW.applicative()
        testLaws(kategory.MonadLaws.laws(ListKW, kategory.Eq.any()))
        testLaws(kategory.SemigroupKLaws.laws(
                kategory.ListKW.semigroupK(),
                applicative,
                kategory.Eq.any()))
        testLaws(TraverseLaws.laws(ListKW.traverse(), applicative, { applicative.pure(it) }, Eq.any()))

        /*"map should modify values" {
            kategory.ListKW.listOfK(14).map { it * 3 } shouldBe kategory.ListKW.listOfK(42)
        }

        "flatMap should modify entity" {
            val emptyListKW: ListKW<Int> = ListKW.listOfK()

            kategory.ListKW.listOfK(14).flatMap { kategory.ListKW.listOfK(it * 3) } shouldBe kategory.ListKW.listOfK(42)
            kategory.ListKW.listOfK(14).flatMap { emptyListKW } shouldBe emptyListKW

            emptyListKW.flatMap { kategory.ListKW.listOfK(it * 3) } shouldBe emptyListKW
        }

        "ListKWMonad.flatMap should be consistent with ListKW#flatMap" {
            val nel = ListKW.listOfK(1, 2)
            val nel2 = ListKW.listOfK(1, 2)
            nel.flatMap { nel2 } shouldBe ListKW.flatMap(nel) { nel2 }
        }*/

        /*"ListKWMonad.binding should for comprehend over ListKW" {
            val result = ListKW.binding {
                val x_p: ListKW<Int> = ListKW.listOfK()
                val x = !ListKW.listOfK(1)
                val y = ListKW.listOfK(2).bind()
                val z = bind { ListKW.listOfK(3) }
                yields(x_p + x + y + z)
            }
            result shouldBe ListKW.listOfK(6)
        }

        "ListKWMonad.binding should for comprehend over complex ListKW" {
            val result = ListKW.binding {
                val x_p: ListKW<Int> = ListKW.listOfK()
                val x = !ListKW.listOfK(1, 2)
                val y = ListKW.listOfK(3).bind()
                val z = bind { ListKW.listOfK(4) }
                yields(x_p, x + y + z)
            }
            result shouldBe ListKW.listOfK(8, 9)
        }*/

    }

}