package arrow

import arrow.ui.ForStore
import arrow.ui.Store
import arrow.ui.Sum
import arrow.ui.extensions.store.comonad.comonad
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
  }
}
