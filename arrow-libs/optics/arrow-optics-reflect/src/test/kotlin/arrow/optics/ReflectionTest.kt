package arrow.optics

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

data class Person(val name: String, val friends: List<String>)

sealed interface Cutlery
object Fork : Cutlery
object Spoon : Cutlery

class ReflectionTest {
  @Test fun lensesForFieldGet() = runTest {
    checkAll(Arb.string(), Arb.list(Arb.string())) { nm, fs ->
      val p = Person(nm, fs.toMutableList())
      Person::name.lens.get(p) shouldBe nm
    }
  }

  @Test fun lensesForFieldSet() = runTest {
    checkAll(Arb.string(), Arb.list(Arb.string())) { nm, fs ->
      val p = Person(nm, fs.toMutableList())
      val m = Person::name.lens.modify(p) { it.capitalize() }
      m shouldBe Person(nm.capitalize(), fs)
    }
  }

  @Test fun traversalForListSet() = runTest {
    checkAll(Arb.string(), Arb.list(Arb.string())) { nm, fs ->
      val p = Person(nm, fs)
      val m = Person::friends.every.modify(p) { it.capitalize() }
      m shouldBe Person(nm, fs.map { it.capitalize() })
    }
  }

  @Test fun instances() = runTest {
    val things = listOf(Fork, Spoon, Fork)
    val forks = Every.list<Cutlery>() compose instance<Cutlery, Fork>()
    val spoons = Every.list<Cutlery>() compose instance<Cutlery, Spoon>()
    forks.size(things) shouldBe 2
    spoons.size(things) shouldBe 1
  }
}
