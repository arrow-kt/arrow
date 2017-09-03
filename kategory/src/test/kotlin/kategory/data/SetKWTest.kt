package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKWTest : UnitSpec() {

    init {
        testLaws(SemigroupKLaws.laws(SetKW.semigroupK(), { SetKW.pure(it) }, Eq.any()))
        testLaws(MonoidKLaws.laws(SetKW.monoidK(), { SetKW.pure(it) }, Eq.any()))
        testLaws(FoldableLaws.laws(SetKW.foldable(), { SetKW.pure(it) }, Eq.any()))
    }
}