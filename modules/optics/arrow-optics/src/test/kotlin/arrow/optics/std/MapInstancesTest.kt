package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.IsoLaws
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genMap
import arrow.test.generators.genMapK
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapInstancesTest : UnitSpec() {

    init {
        testLaws(IsoLaws.laws(
                iso = mapToMapK(),
                aGen = genMap(Gen.string(), Gen.int()),
                bGen = genMapK(Gen.string(), Gen.int()),
                funcGen = genFunctionAToB(genMapK(Gen.string(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any()
        ))
    }

}