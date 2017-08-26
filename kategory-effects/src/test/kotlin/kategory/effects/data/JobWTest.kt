package kategory

import io.kotlintest.KTestJUnitRunner
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class JobWTest : UnitSpec() {
    val EQ: Eq<HK<JobWHK, Int>> = object : Eq<HK<JobWHK, Int>> {
        override fun eqv(a: HK<JobWHK, Int>, b: HK<JobWHK, Int>): Boolean =
                runBlocking {
                    val resultA = AtomicInteger(Int.MIN_VALUE)
                    val resultB = AtomicInteger(Int.MAX_VALUE)
                    a.runJob {
                        it.fold({ throw it }, { resultA.set(it) })
                    }.join()
                    b.runJob({
                        it.fold({ throw it }, { resultB.set(it) })
                    }).join()
                    resultA.get() == resultB.get()
                }
    }

    val EQ_ERR: Eq<HK<JobWHK, Int>> = object : Eq<HK<JobWHK, Int>> {
        override fun eqv(a: HK<JobWHK, Int>, b: HK<JobWHK, Int>): Boolean =
                runBlocking {
                    val errorA = AtomicReference<Throwable>()
                    val errorB = AtomicReference<Throwable>()
                    a.runJob({ it.fold({ errorA.set(it) }, { }) }).apply { join() }
                    b.runJob({ it.fold({ errorB.set(it) }, { }) }).apply { join() }
                    errorA.get() == errorB.get()
                }
    }

    init {
        testLaws(AsyncLaws.laws(JobW.asyncContext(EmptyCoroutineContext), JobW.monadError(EmptyCoroutineContext), EQ, EQ_ERR))
    }
}