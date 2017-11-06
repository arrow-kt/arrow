package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IsoLaws
import kategory.ListKWMonadCombineInstance
import kategory.ListKWMonoidInstanceImplicits
import kategory.NonEmptyListSemigroupInstanceImplicits
import kategory.OptionMonoidInstanceImplicits
import kategory.OptionalLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genNonEmptyList
import kategory.genOption
import kategory.isos
import kategory.k
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
