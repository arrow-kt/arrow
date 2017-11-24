package kategory.effects

import kategory.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class LiveDataKWTest : UnitSpec() {
    fun <A> EQ(): Eq<HK<LiveDataKWHK, A>> = Eq { a, b ->
        a.value().getValue() == b.value().getValue()
    }

    init {

        testLaws(MonadLaws.laws(LiveDataKW.monad(), EQ()))

        "instances can be resolved implicitly"{
            functor<LiveDataKWHK>() shouldNotBe null
            applicative<LiveDataKWHK>() shouldNotBe null
            monad<LiveDataKWHK>() shouldNotBe null
        }
    }
}
