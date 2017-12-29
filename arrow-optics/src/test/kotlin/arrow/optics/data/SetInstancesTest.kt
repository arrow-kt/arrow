package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.IsoLaws
import arrow.SetKWMonoidInstanceImplicits
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
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