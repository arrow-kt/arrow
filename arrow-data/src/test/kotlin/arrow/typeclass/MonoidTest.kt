package arrow.typeclass

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import arrow.test.UnitSpec
import arrow.combineAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MonoidTest : UnitSpec() {

    init {

        "Combining all for a list of ints should be same as sum" {
            forAll({ ints: List<Int> ->
                ints.combineAll() == ints.sum()
            })
        }

    }

}