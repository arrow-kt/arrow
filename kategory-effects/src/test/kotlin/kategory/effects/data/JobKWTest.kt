package kategory

import io.kotlintest.KTestJUnitRunner
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@RunWith(KTestJUnitRunner::class)
class JobWTest : UnitSpec() {
    private fun <A> EQ(): Eq<HK<JobKWHK, A>> = Eq { a, b ->
        runBlocking {
            val resultA = AtomicReference<A>()
            val resultB = AtomicReference<A>()
            val success = AtomicBoolean(true)
            a.runJob(CommonPool) {
                it.fold({ success.set(false) }, { resultA.set(it) })
            }.join()
            b.runJob(CommonPool) {
                it.fold({ success.set(false) }, { resultB.set(it) })
            }.join()
            success.get() && resultA.get() != null && resultA.get() == resultB.get()
        }
    }

    val EQ_ERR: Eq<HK<JobKWHK, Int>> = Eq { a, b ->
        runBlocking {
            val resultA = AtomicInteger(Int.MIN_VALUE)
            val resultB = AtomicInteger(Int.MIN_VALUE)
            val errorA = AtomicReference<Throwable>()
            val errorB = AtomicReference<Throwable>()
            a.runJob(CommonPool) { it.fold({ errorA.set(it) }, { resultA.set(it) }) }.apply { join() }
            b.runJob(CommonPool) { it.fold({ errorB.set(it) }, { resultB.set(it) }) }.apply { join() }
            errorA.get() == errorB.get() && resultA.get() == resultB.get()
        }
    }

    init {
        testLaws(AsyncLaws.laws(JobKW.asyncContext(), JobKW.monadError(), EQ(), EQ(), EQ_ERR))
    }
}