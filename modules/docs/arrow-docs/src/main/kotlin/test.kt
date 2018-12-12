import arrow.effects.FluxK
import arrow.effects.MonoK
import arrow.effects.typeclasses.ExitCase
import arrow.effects.value
import io.kotlintest.matchers.shouldBe
import kotlinx.coroutines.timeunit.TimeUnit
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {

  lateinit var ec: ExitCase<Throwable>
  val countDownLatch = CountDownLatch(1)
  MonoK.just(Unit)
    .bracketCase(
      use = {
        println("use")
        MonoK.async<Nothing> { }
      },
      release = { _, exitCase ->
        MonoK {
          ec = exitCase
          println("Release: $exitCase")
          countDownLatch.countDown()
        }
      }
    )
    .value()
    .subscribe()
    .dispose()

  countDownLatch.await(100, TimeUnit.MILLISECONDS)
  ec shouldBe ExitCase.Cancelled
//  ec shouldBe Unit

}