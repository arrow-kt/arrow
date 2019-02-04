package arrow.effects

import arrow.effects.typeclasses.suspended.*
import arrow.effects.typeclasses.suspended.fx.async.async
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  fun <A> EQ(): Eq<FxOf<A>> = Eq { a, b ->
    runBlocking {
      try {
        a.invoke() == b.invoke()
      } catch (e: Throwable) {
        val errA = try {
          a.invoke()
          throw IllegalArgumentException()
        } catch (err: Throwable) {
          err
        }
        val errB = try {
          b.invoke()
          throw IllegalStateException()
        } catch (err: Throwable) {
          err
        }

        errA == errB
      }
    }
  }

  init {
    testLaws(AsyncLaws.laws(Fx.async(), EQ(), EQ()))
  }

}