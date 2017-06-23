package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class GlobalInstancesTest : UnitSpec() {

    inline fun <reified F> testTypeclassHierarchyInference(a : Any) {
        functor<F>() shouldBe a
        applicative<F>() shouldBe a
        monad<F>() shouldBe a
    }

    init {

        "Id inference" {
            testTypeclassHierarchyInference<Id.F>(Id)
        }

        "NonEmptyList monad inference" {
            testTypeclassHierarchyInference<NonEmptyList.F>(NonEmptyList)
        }

        "Option monad inference" {
            testTypeclassHierarchyInference<Option.F>(Option)
        }
    }
}
