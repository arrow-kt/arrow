package arrow

import arrow.data.SetKW
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.FoldableLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class SetKWTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            foldable<SetKWHK>() shouldNotBe null
            semigroupK<SetKWHK>() shouldNotBe null
            monoidK<SetKWHK>() shouldNotBe null
            semigroup<SetKW<Int>>() shouldNotBe null
            monoid<SetKW<Int>>() shouldNotBe null
            eq<SetKW<Int>>() shouldNotBe null
        }
        
        testLaws(
            EqLaws.laws { SetKW.pure(it) },
            SemigroupKLaws.laws(SetKW.semigroupK(), { SetKW.pure(it) }, Eq.any()),
            MonoidKLaws.laws(SetKW.monoidK(), { SetKW.pure(it) }, Eq.any()),
            FoldableLaws.laws(SetKW.foldable(), { SetKW.pure(it) }, Eq.any())
        )
    }
}
