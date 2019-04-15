package arrow.test.laws

import arrow.recursion.typeclasses.Corecursive
import arrow.test.generators.toGNat

object CorecursiveLaws {

  inline fun <reified T> laws(CT: Corecursive<T>): List<Law> = listOf(
    Law("Corecursive Laws: Ana should be stack safe") {
      CT.toGNat(10000)
    }
  )
}
