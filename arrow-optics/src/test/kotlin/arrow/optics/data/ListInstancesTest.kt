package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.IsoLaws
import arrow.core.OptionMonoidInstanceImplicits
import arrow.data.ListKWMonoidInstanceImplicits
import arrow.data.NonEmptyListSemigroupInstanceImplicits
import arrow.data.k
import arrow.test.laws.OptionalLaws
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genOption
import arrow.optics.instances.listHead
import arrow.optics.instances.listTail
import arrow.optics.instances.listToListKW
import arrow.optics.instances.listToOptionNel
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListInstancesTest : UnitSpec() {

    init {

        testLaws(
            OptionalLaws.laws(
                optional = listHead(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            OptionalLaws.laws(
                optional = listTail(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.list(Gen.int()),
                funcGen = genFunctionAToB(Gen.list(Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            IsoLaws.laws(
                iso = listToOptionNel(),
                aGen = Gen.list(Gen.int()),
                bGen = genOption(genNonEmptyList(Gen.int())),
                funcGen = genFunctionAToB(genOption(genNonEmptyList(Gen.int()))),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = OptionMonoidInstanceImplicits.instance(NonEmptyListSemigroupInstanceImplicits.instance<Int>())),

            IsoLaws.laws(
                iso = listToListKW(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.create { Gen.list(Gen.int()).generate().k() },
                funcGen = genFunctionAToB(Gen.create { Gen.list(Gen.int()).generate().k() }),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = ListKWMonoidInstanceImplicits.instance())
        )

    }

}
