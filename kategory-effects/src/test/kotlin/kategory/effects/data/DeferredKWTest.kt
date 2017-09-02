package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    val EQ: Eq<HK<DeferredKWHK, Int>> = object : Eq<HK<DeferredKWHK, Int>> {
        override fun eqv(a: HK<DeferredKWHK, Int>, b: HK<DeferredKWHK, Int>): Boolean =
                a.ev().attempt() == b.ev().attempt()
    }

    init {
        testLaws(MonadErrorLaws.laws(DeferredKW.monadError(EmptyCoroutineContext), EQ, EQ))
    }
}