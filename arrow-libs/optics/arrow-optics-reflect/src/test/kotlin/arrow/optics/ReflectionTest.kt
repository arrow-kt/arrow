package arrow.optics

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

data class Person(val name: String, val friends: List<String>)

sealed interface Cutlery
data object Fork : Cutlery
data object Spoon : Cutlery

class ReflectionTest {
  private val asciiString: Arb<String> = Arb.string(codepoints = Codepoint.ascii())

  @Test fun lensesForFieldGet() = runTest {
    checkAll(asciiString, Arb.list(asciiString, 1..20)) { nm, fs ->
      val p = Person(nm, fs.toMutableList())
      Person::name.lens.get(p) shouldBe nm
    }
  }

  @Test fun lensesForFieldSet() = runTest {
    checkAll(asciiString, Arb.list(asciiString, 1..20)) { nm, fs ->
      val p = Person(nm, fs.toMutableList())
      val m = Person::name.lens.modify(p) { it.lowercase() }
      m shouldBe Person(nm.lowercase(), fs)
    }
  }

  @Test fun traversalForListSet() = runTest {
    checkAll(asciiString, Arb.list(asciiString, 1..20)) { nm, fs ->
      val p = Person(nm, fs)
      val m = Person::friends.every.modify(p) { it.lowercase() }
      m shouldBe Person(nm, fs.map { it.lowercase() })
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
