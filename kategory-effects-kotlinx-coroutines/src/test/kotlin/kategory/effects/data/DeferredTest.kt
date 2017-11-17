package kategory.effects

import kategory.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <A> EQ(): Eq<HK<DeferredKWHK, A>> = Eq { a, b ->
        a.unsafeAttemptSync() == b.unsafeAttemptSync()
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

        "DeferredKW is awaitable" {
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                runBlocking {
                    val a = DeferredKW { x }.await()
                    val b = DeferredKW { y + a }.await()
                    val c = DeferredKW { z + b }.await()
                    c
                } == x + y + z
            })

        }
    }
}
