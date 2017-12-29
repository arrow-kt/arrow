package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Eq
import arrow.IsoLaws
import arrow.SetKWMonoidInstanceImplicits
import arrow.UnitSpec
import arrow.genFunctionAToB
import arrow.optics.instances.setToSetKW
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetInstancesTest : UnitSpec() {

    init {

        testLaws(IsoLaws.laws(
                iso = setToSetKW(),
                aGen = Gen.set(Gen.int()),
                bGen = Gen.create { Gen.set(Gen.int()).generate().k() },
                funcGen = genFunctionAToB(Gen.create { Gen.set(Gen.int()).generate().k() }),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = SetKWMonoidInstanceImplicits.instance()
        ))

    }

}