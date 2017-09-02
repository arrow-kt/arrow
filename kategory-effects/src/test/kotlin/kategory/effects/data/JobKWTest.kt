package kategory

import io.kotlintest.KTestJUnitRunner
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class JobWTest : UnitSpec() {
    private fun <A> EQ(): Eq<HK<JobWHK, A>> = object : Eq<HK<JobWHK, A>> {
        override fun eqv(a: HK<JobWHK, A>, b: HK<JobWHK, A>): Boolean =
                runBlocking {
                    val resultA = AtomicReference<A>()
                    val resultB = AtomicReference<A>()
                    val success = AtomicBoolean(true)
                    a.runJob {
                        it.fold({ success.set(false) }, { resultA.set(it) })
                    }.join()
                    b.runJob({
                        it.fold({ success.set(false) }, { resultB.set(it) })
                    }).join()
                    success.get() && resultA.get() != null && resultA.get() == resultB.get()
                }
    }

    val EQ_ERR: Eq<HK<JobWHK, Int>> = object : Eq<HK<JobWHK, Int>> {
        override fun eqv(a: HK<JobWHK, Int>, b: HK<JobWHK, Int>): Boolean =
                runBlocking {
                    val resultA = AtomicInteger(Int.MIN_VALUE)
                    val resultB = AtomicInteger(Int.MIN_VALUE)
                    val errorA = AtomicReference<Throwable>()
                    val errorB = AtomicReference<Throwable>()
                    a.runJob({ it.fold({ errorA.set(it) }, { resultA.set(it) }) }).apply { join() }
                    b.runJob({ it.fold({ errorB.set(it) }, { resultB.set(it) }) }).apply { join() }
                    errorA.get() == errorB.get() && resultA.get() == resultB.get()
                }
    }

    init {
        testLaws(AsyncLaws.laws(JobW.asyncContext(EmptyCoroutineContext), JobW.monadError(EmptyCoroutineContext), EQ(), EQ(), EQ_ERR))
    }
}