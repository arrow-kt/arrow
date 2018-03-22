package arrow.data

import arrow.instances.IntEqInstance
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKTest : UnitSpec() {

    init {

        val EQ = SetK.eq(IntEqInstance)

        testLaws(
                EqLaws.laws(EQ) { SetK.pure(it) },
                ShowLaws.laws(SetK.show(), EQ) { SetK.pure(it) },
                SemigroupKLaws.laws(SetK.semigroupK(), { SetK.pure(it) }, Eq.any()),
                MonoidKLaws.laws(SetK.monoidK(), { SetK.pure(it) }, Eq.any()),
                FoldableLaws.laws(SetK.foldable(), { SetK.pure(it) }, Eq.any())
        )
    }
}
