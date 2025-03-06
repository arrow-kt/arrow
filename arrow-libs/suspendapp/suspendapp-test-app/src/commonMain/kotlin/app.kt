import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun interface Work {
    suspend fun CoroutineScope.work()
}

sealed interface Mode : Work
data class Delay(val duration: Duration = 3.seconds): Mode, Work by Work({ delay(duration) })
data object Wait : Mode, Work by Work({ while(isActive) { delay(1000) }  })
data object Fail : Mode, Work by Work({ error("BOOM!") })
data object ChildFail : Mode, Work by Work({ launch { error("boom.") } })

fun app(mode: String?) = app(when(mode) {
    "delay" -> Delay()
    "wait" -> Wait
    "fail" -> Fail
    "childfail" -> ChildFail
    else -> Delay()
})

fun app(work: Work) {
    println("pre-suspendapp")
    SuspendApp(timeout = 10.seconds) {
        println("Running $work")
        resourceScope {
            onRelease {
                println("resource cleaning... ($it)")
                delay(2.seconds)
                println("resource clean complete")
            }
            with(work) { work() }
        }
    }
    println("post-suspendapp")
}