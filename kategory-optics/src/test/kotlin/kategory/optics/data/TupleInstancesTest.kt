package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.LensLaws
import kategory.NonEmptyList
import kategory.Try
import kategory.UnitSpec
import kategory.applicative
import kategory.genFunctionAToB
import kategory.genTuple
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleInstancesTest : UnitSpec() {

    init {

        testLaws(LensLaws.laws(
                lens = firstTuple2(),
                aGen = genTuple(Gen.int(), Gen.string()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = NonEmptyList.applicative()
        ))

        testLaws(LensLaws.laws(
                lens = secondTuple2(),
                aGen = genTuple(Gen.int(), Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = Try.applicative()
        ))

    }

}