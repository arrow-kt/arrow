import arrow.continuations.SuspendApp
import arrow.continuations.SuspendAppScope
import arrow.continuations.exitApp
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun interface Work {
  suspend fun SuspendAppScope.work()
}

sealed interface Mode : Work
data class Delay(val duration: Duration = 3.seconds) : Mode, Work by Work({ delay(duration) })
data object Wait : Mode, Work by Work({
  while (isActive) {
    delay(1000)
  }
})

data object Fail : Mode, Work by Work({ error("BOOM!") })
data object ChildFail : Mode, Work by Work({ launch { error("boom.") } })
data object ExitApp : Mode, Work by Work({ exitApp(42) })
data object ChildExitApp : Mode, Work by Work({ async { exitApp(2) }.await() })
data object ChildLaunchExitApp : Mode, Work by Work({
  launch { exitApp(24) }
  awaitCancellation()
})

fun app(mode: String?) = app(
  when (mode) {
    "delay" -> Delay()
    "wait" -> Wait
    "fail" -> Fail
    "childfail" -> ChildFail
    "exitapp" -> ExitApp
    "childexitapp" -> ChildExitApp
    "childlaunchexitapp" -> ChildLaunchExitApp
    else -> Delay()
  }
)

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
