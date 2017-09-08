package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListKWTest : UnitSpec() {
    val applicative = ListKW.applicative()

    init {
        testLaws(MonadLaws.laws(ListKW.monad(), Eq.any()))
        testLaws(SemigroupKLaws.laws(ListKW.semigroupK(), applicative, Eq.any()))
        testLaws(MonoidKLaws.laws(ListKW.monoidK(), applicative, Eq.any()))
        testLaws(TraverseLaws.laws(ListKW.traverse(), applicative, { n: Int -> ListKW(listOf(n)) }, Eq.any()))
    }
}