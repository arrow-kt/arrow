package arrow.test.laws

import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.test.generators.toGNat
import arrow.test.generators.toInt
import arrow.test.laws.Law
import io.kotlintest.matchers.shouldEqual

object RecursiveLaws {
  inline fun <reified T> laws(CT: Corecursive<T>, RT: Recursive<T>): List<Law> = listOf(
    Law("Recursive Laws: Cata should be stack safe") {
      10000.toGNat(CT).toInt(RT) shouldEqual 10000
    }
  )
}
