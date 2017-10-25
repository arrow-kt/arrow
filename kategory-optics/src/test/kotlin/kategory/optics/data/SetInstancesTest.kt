package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IsoLaws
import kategory.SetKWMonoidInstanceImplicits
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.k
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