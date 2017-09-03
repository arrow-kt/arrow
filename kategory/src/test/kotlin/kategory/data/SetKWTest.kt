package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKWTest : UnitSpec() {
    val applicative = SetKW.applicative()

    init {
        testLaws(MonadLaws.laws(SetKW.monad(), Eq.any()))
        testLaws(SemigroupKLaws.laws(SetKW.semigroupK(), applicative, Eq.any()))
        testLaws(MonoidKLaws.laws(SetKW.monoidK(), applicative, Eq.any()))
        testLaws(TraverseLaws.laws(SetKW.traverse(), applicative, { n: Int -> SetKW(setOf(n)) }, Eq.any()))
    }
}