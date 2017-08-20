package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListKWTest : UnitSpec() {
    val applicative = ListKW.applicative()
    init {
        testLaws(kategory.MonadLaws.laws(ListKW, Eq.any()))
        testLaws(kategory.SemigroupKLaws.laws(
                kategory.ListKW.semigroupK(),
                applicative,
                Eq.any()))
        testLaws(TraverseLaws.laws(ListKW.traverse(), applicative, { n: Int -> ListKW(listOf(n)) }, Eq.any()))
    }
}