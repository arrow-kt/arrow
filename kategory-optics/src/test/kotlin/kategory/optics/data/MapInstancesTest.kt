package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IsoLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genMap
import kategory.genMapKW
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapInstancesTest : UnitSpec() {

    init {
        testLaws(IsoLaws.laws(
                iso = mapToMapKW(),
                aGen = genMap(Gen.string(), Gen.int()),
                bGen = genMapKW(Gen.string(), Gen.int()),
                funcGen = genFunctionAToB(genMapKW(Gen.string(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any()
        ))
    }

}