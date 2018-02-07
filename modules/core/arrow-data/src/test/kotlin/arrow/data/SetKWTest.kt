package arrow.data

import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            foldable<ForSetK>() shouldNotBe null
            semigroupK<ForSetK>() shouldNotBe null
            monoidK<ForSetK>() shouldNotBe null
            semigroup<SetK<Int>>() shouldNotBe null
            monoid<SetK<Int>>() shouldNotBe null
            eq<SetK<Int>>() shouldNotBe null
        }
        
        testLaws(
            EqLaws.laws { SetK.pure(it) },
            SemigroupKLaws.laws(SetK.semigroupK(), { SetK.pure(it) }, Eq.any()),
            MonoidKLaws.laws(SetK.monoidK(), { SetK.pure(it) }, Eq.any()),
            FoldableLaws.laws(SetK.foldable(), { SetK.pure(it) }, Eq.any())
        )
    }
}
