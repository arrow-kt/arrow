package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Eq
import arrow.Id
import arrow.IntMonoid
import arrow.IsoLaws
import arrow.UnitSpec
import arrow.genFunctionAToB
import arrow.optics.idToType
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