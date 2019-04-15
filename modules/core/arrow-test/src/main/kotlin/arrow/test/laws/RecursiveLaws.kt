package arrow.test.laws

import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.test.generators.toGNat
import arrow.test.generators.toInt
import io.kotlintest.shouldBe

object RecursiveLaws {
  inline fun <reified T> laws(CT: Corecursive<T>, RT: Recursive<T>): List<Law> = listOf(
    Law("Recursive Laws: Cata should be stack safe") {
      RT.toInt(CT.toGNat(10000)) shouldBe 10000
    }
  )
}
