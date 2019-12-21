package arrow.ui

import arrow.Kind
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.ui.extensions.store.comonad.comonad
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class StoreTest : UnitSpec() {

  init {

    fun <F> gk() = object : GenK<StorePartialOf<F>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<StorePartialOf<F>, A>> =
        gen.map {
          Store(it) {
            it
          }
        } as Gen<Kind<StorePartialOf<F>, A>>
    }

    val EQK = object : EqK<StorePartialOf<Int>> {
      override fun <A> Kind<StorePartialOf<Int>, A>.eqK(other: Kind<StorePartialOf<Int>, A>, EQ: Eq<A>): Boolean {
        return this.fix().extract() == other.fix().extract()
      }
    }

    testLaws(
      ComonadLaws.laws(Store.comonad(), gk(), EQK)
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
