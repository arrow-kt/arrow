package kategory.effects

import kategory.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <A> EQ(): Eq<HK<DeferredKWHK, A>> = Eq { a, b ->
        a.unsafeRunSync() == b.unsafeRunSync()
    }

    init {
        testLaws(AsyncLaws.laws(DeferredKW.asyncContext(), DeferredKW.monadError(), EQ(), EQ()))

        "instances can be resolved implicitly"{
            functor<DeferredKWHK>() shouldNotBe null
            applicative<DeferredKWHK>() shouldNotBe null
            monad<DeferredKWHK>() shouldNotBe null
            monadError<DeferredKWHK, Throwable>() shouldNotBe null
            asyncContext<DeferredKWHK>() shouldNotBe null
        }
    }
}
