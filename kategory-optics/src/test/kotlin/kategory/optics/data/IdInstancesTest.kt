package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.Id
import kategory.IntMonoid
import kategory.IsoLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.optics.idToType
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdInstancesTest : UnitSpec() {

    init {
        testLaws(IsoLaws.laws(
                iso = idToType(),
                aGen = Gen.create { Id(Gen.int().generate()) },
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = IntMonoid
        ))
    }

}