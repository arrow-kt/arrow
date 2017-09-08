package kategory

import io.kotlintest.KTestJUnitRunner
import kotlinx.coroutines.experimental.CommonPool
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <T> EQ(): Eq<HK<DeferredKWHK, T>> = Eq { a, b ->
        a.unsafeRunSync() == b.unsafeRunSync()
    }

    init {
        testLaws(AsyncLaws.laws(DeferredKW.asyncContext(), DeferredKW.monadError(), EQ(), EQ()))
    }
}