package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.IsoLaws
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genMap
import arrow.test.generators.genMapKW
import arrow.test.generators.genSetKW
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapInstancesTest : UnitSpec() {

    init {
        testLaws(IsoLaws.laws(
                iso = mapToMapKW(),
                aGen = genMap(Gen.string(), Gen.int()),
                bGen = genMapKW(Gen.string(), Gen.int()),
                funcGen = genFunctionAToB(genMapKW(Gen.string(), Gen.int()))
        ))

        testLaws(IsoLaws.laws(
                iso = mapKWToSetKW(),
                aGen = genMapKW(Gen.string(), Gen.create { Unit }),
                bGen = genSetKW(Gen.string()),
                funcGen = genFunctionAToB(genSetKW(Gen.string())),
                EQA = Eq.any()
        ))
    }

}