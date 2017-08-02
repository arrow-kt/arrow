package kategory.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.ListKW
import kategory.UnitSpec
import org.junit.runner.RunWith

/**
 * Created by marc on 2/8/17.
 */
@RunWith(KTestJUnitRunner::class)
class ListKWTest : UnitSpec() {
    init {

        testLaws(kategory.MonadLaws.laws(kategory.NonEmptyList, kategory.Eq.any()))
        testLaws(kategory.SemigroupKLaws.laws(
                kategory.NonEmptyList.semigroupK(),
                kategory.NonEmptyList.applicative(),
                kategory.Eq.any()))

        "map should modify values" {
            kategory.ListKW.listOfK(14).map { it * 3 } shouldBe kategory.ListKW.listOfK(42)
        }

        "flatMap should modify entity" {
            val emptyListKW: ListKW<Int> = ListKW.listOfK()
            kategory.ListKW.listOfK(14).flatMap { kategory.ListKW.listOfK(it * 3) } shouldBe kategory.ListKW.listOfK(42)
            kategory.ListKW.listOfK(14).flatMap { emptyListKW } shouldBe emptyListKW

            emptyListKW.flatMap { kategory.ListKW.listOfK(it * 3) } shouldBe emptyListKW
        }

    }

}