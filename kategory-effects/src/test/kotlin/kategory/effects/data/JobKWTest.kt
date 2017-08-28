package kategory

import io.kotlintest.KTestJUnitRunner
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class JobKWTest : UnitSpec() {
    val EQ: Eq<HK<JobKWHK, Int>> = object : Eq<HK<JobKWHK, Int>> {
        override fun eqv(a: HK<JobKWHK, Int>, b: HK<JobKWHK, Int>): Boolean =
                runBlocking {
                    val resultA = AtomicInteger(Int.MIN_VALUE)
                    val resultB = AtomicInteger(Int.MAX_VALUE)
                    val success = AtomicBoolean(true)
                    a.runJob {
                        it.fold({ success.set(false) }, { resultA.set(it) })
                    }.join()
                    b.runJob({
                        it.fold({ success.set(false) }, { resultB.set(it) })
                    }).join()
                    success.get() && resultA.get() == resultB.get()
                }
    }

    val EQ_ERR: Eq<HK<JobKWHK, Int>> = object : Eq<HK<JobKWHK, Int>> {
        override fun eqv(a: HK<JobKWHK, Int>, b: HK<JobKWHK, Int>): Boolean =
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
        testLaws(AsyncLaws.laws(JobKW.asyncContext(EmptyCoroutineContext), JobKW.monadError(EmptyCoroutineContext), EQ, EQ_ERR))
    }
}