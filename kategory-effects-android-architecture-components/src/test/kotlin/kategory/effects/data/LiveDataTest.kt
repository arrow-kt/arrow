package kategory.effects

import kategory.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

import android.arch.core.executor.ArchTaskExecutor
import android.arch.core.executor.TaskExecutor

@RunWith(KTestJUnitRunner::class)
class LiveDataKWTest : UnitSpec() {
    fun <A> EQ(): Eq<HK<LiveDataKWHK, A>> = Eq { a, b ->
        val aa = a.value().apply { observeForever { } }.getValue()
        val bb = b.value().apply { observeForever { } }.getValue()
        aa == bb && aa != null
    }

    init {
        cookArchitectureComponents()

        testLaws(MonadLaws.laws(LiveDataKW.monad(), EQ()))

        "instances can be resolved implicitly"{
            functor<LiveDataKWHK>() shouldNotBe null
            applicative<LiveDataKWHK>() shouldNotBe null
            monad<LiveDataKWHK>() shouldNotBe null
        }
    }

    fun cookArchitectureComponents() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean = true
        })
    }
}
