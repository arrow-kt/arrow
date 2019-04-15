package arrow

import arrow.core.Id
import arrow.data.Day
import arrow.data.ForStore
import arrow.data.Store
import arrow.data.Sum
import arrow.core.extensions.id.comonad.comonad
import arrow.data.extensions.store.comonad.comonad
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class ComonadicUisTest : FreeSpec() {

  private fun <A, B, C> Sum<Kind<ForStore, A>, Kind<ForStore, B>, C>.extract() =
    this.extract(Store.comonad(), Store.comonad())

  init {

    "Sum of two Stores" {
      val counterStore = Store(0) { "Counter value: $it" }
      val nameStore = Store("Cotel") { "Hey $it!" }

      val sum = Sum(counterStore, nameStore)

      sum.extract() shouldBe "Counter value: 0"

      sum.changeSide(Sum.Side.Right).extract() shouldBe "Hey Cotel!"

      sum
        .copy(left = counterStore.move(10))
        .extract() shouldBe "Counter value: 10"
    }

    "Day of two Ids" {
      val renderHtml = { left: String, right: Int -> """
          |<div>
          | <p>$left</p>
          | <p>$right</p>
          |</div>
        """.trimMargin()
      }
      val day = Day(Id.just("Hello"), Id.just(0), renderHtml)

      day.extract(Id.comonad(), Id.comonad()) shouldBe renderHtml("Hello", 0)
    }
  }
}
