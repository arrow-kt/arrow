package kategory.effects.data

import io.kotlintest.KTestJUnitRunner
import kategory.*
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class JobWTest : UnitSpec() {
    val EQ: Eq<HK<JobWHK, Int>> = object : Eq<HK<JobWHK, Int>> {
        override fun eqv(a: HK<JobWHK, Int>, b: HK<JobWHK, Int>): Boolean =
                runBlocking {
                    val resultA = AtomicInteger(Int.MIN_VALUE)
                    val resultB = AtomicInteger(Int.MIN_VALUE)
                    a.runJob {
                        it.fold({}, { resultA.set(it) })
                    }.join()
                    b.runJob({
                        it.fold({}, { resultB.set(it) })
                    }).join()
                    resultA.get() == resultB.get()
                }
    }

    val EQ_ERR: Eq<HK<JobWHK, Int>> = object : Eq<HK<JobWHK, Int>> {
        override fun eqv(a: HK<JobWHK, Int>, b: HK<JobWHK, Int>): Boolean =
                runBlocking {
                    val jobA = a.runJob({})
                    jobA.join()
                    val jobB = b.runJob({})
                    jobB.join()
                    jobA.getCompletionException().javaClass == jobB.getCompletionException().javaClass
                }
    }

    init {
        testLaws(AsyncLaws.laws(JobW.asyncContext(EmptyCoroutineContext), JobW.monadError(EmptyCoroutineContext), EQ, EQ_ERR))
    }
}