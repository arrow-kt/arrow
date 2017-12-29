package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Eq
import arrow.IsoLaws
import arrow.test.UnitSpec
import arrow.genFunctionAToB
import arrow.genMap
import arrow.genMapKW
import arrow.optics.instances.mapToMapKW
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