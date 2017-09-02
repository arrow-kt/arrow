package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <T> EQ(): Eq<HK<DeferredKWHK, T>> = Eq { a, b ->
        a.ev().attempt() == b.ev().attempt()
    }

    init {
        testLaws(AsyncLaws.laws(DeferredKW.asyncContext(EmptyCoroutineContext), DeferredKW.monadError(EmptyCoroutineContext), EQ(), EQ()))
    }
}