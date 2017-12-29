package arrow.instances

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.typeclasses.eq
import arrow.typeclasses.monoid
import arrow.typeclasses.semigroup

@RunWith(KTestJUnitRunner::class)
class UnitInstancesTest : UnitSpec() {
    init {
        "instances can be resolved implicitly" {
            semigroup<Unit>() shouldNotBe null
            monoid<Unit>() shouldNotBe null
            eq<Unit>() shouldNotBe null
        }

        testLaws(
                MonoidLaws.laws(monoid(), Unit, eq()),
                SemigroupLaws.laws(semigroup(), Unit, Unit, Unit, eq()),
                EqLaws.laws { Unit }
        )
    }
}
