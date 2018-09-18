package arrow

import arrow.data.ForStore
import arrow.data.Store
import arrow.data.Sum
import arrow.data.comonad
import arrow.data.functor
import io.kotlintest.matchers.shouldBe
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
