package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKWTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            foldable<SetKWHK>() shouldNotBe null
            semigroupK<SetKWHK>() shouldNotBe null
            monoidK<SetKWHK>() shouldNotBe null
            semigroup<SetKW<Int>>() shouldNotBe null
            monoid<SetKW<Int>>() shouldNotBe null
        }
        
        testLaws(SemigroupKLaws.laws(SetKW.semigroupK(), { SetKW.pure(it) }, Eq.any()))
        testLaws(MonoidKLaws.laws(SetKW.monoidK(), { SetKW.pure(it) }, Eq.any()))
        testLaws(FoldableLaws.laws(SetKW.foldable(), { SetKW.pure(it) }, Eq.any()))
    }
}