package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <T> EQ(): Eq<HK<DeferredKWHK, T>> = object : Eq<HK<DeferredKWHK, T>> {
        override fun eqv(a: HK<DeferredKWHK, T>, b: HK<DeferredKWHK, T>): Boolean =
                a.ev().attempt() == b.ev().attempt()
    }

    init {
        testLaws(MonadErrorLaws.laws(DeferredKW.monadError(EmptyCoroutineContext), EQ(), EQ()))
    }
}