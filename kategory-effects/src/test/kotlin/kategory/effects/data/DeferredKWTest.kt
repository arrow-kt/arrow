package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <T> EQ(): Eq<HK<DeferredKWHK, T>> = Eq { a: HK<DeferredKWHK, T>, b: HK<DeferredKWHK, T> ->
        a.ev().attempt() == b.ev().attempt()
    }

    init {
        testLaws(MonadErrorLaws.laws(DeferredKW.monadError(EmptyCoroutineContext), EQ(), EQ()))
    }
}