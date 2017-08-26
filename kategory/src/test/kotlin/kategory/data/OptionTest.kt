package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import kategory.Option.None
import kategory.Option.Some
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest : UnitSpec() {

    object OptionError : RuntimeException()

    init {

        testLaws(MonadErrorLaws.laws(Option.monadError<Throwable>(OptionError), Eq.any()))
        testLaws(TraverseLaws.laws(Option.traverse(), Option.monad(), ::Some, Eq.any()))

        "fromNullable should work for both null and non-null values of nullable types" {
            forAll { a: Int? ->
                // This seems to be generating only non-null values, so it is complemented by the next test
                val o: Option<Int> = Option.fromNullable(a)
                if (a == null) o == None else o == Some(a)
            }
        }

        "fromNullable should return none for null values of nullable types" {
            val a: Int? = null
            Option.fromNullable(a) shouldBe None
        }

    }

}
