package arrow.ui

import arrow.ui.extensions.store.comonad.comonad
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.shouldBe

class StoreTest : UnitSpec() {

  init {

    val intStore = { x: Int -> Store(x) { it } }

    val EQ = object : Eq<StoreOf<Int, Int>> {
      override fun StoreOf<Int, Int>.eqv(b: StoreOf<Int, Int>): Boolean =
        this.fix().extract() == b.fix().extract()
    }

    testLaws(
      ComonadLaws.laws(Store.comonad(), intStore, EQ)
    )

    val greetingStore = { name: String -> Store(name) { "Hi $it!" } }

    "extract should render the current state" {
      val store = greetingStore("Cotel")

      store.extract() shouldBe "Hi Cotel!"
    }

    "extend should create a new Store from the current one" {
      val store = greetingStore("Cotel")
        .coflatMap { (state) ->
          if (state == "Cotel") "This is my master" else "This is not my master"
        }

      store.extract() shouldBe "This is my master"
    }

    "map should modify the render result" {
      val store = greetingStore("Cotel")
        .map { it.toUpperCase() }

      store.extract() shouldBe "HI COTEL!"
    }
  }
}
