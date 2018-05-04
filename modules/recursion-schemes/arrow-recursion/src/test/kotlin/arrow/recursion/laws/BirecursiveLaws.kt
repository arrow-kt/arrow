package arrow.recursion.laws

import arrow.core.Option
import arrow.core.functor
import arrow.recursion.hylo
import arrow.recursion.typeclasses.Birecursive
import arrow.test.laws.Law
import io.kotlintest.properties.forAll

object BirecursiveLaws {
  inline fun <reified T> laws(BT: Birecursive<T>): List<Law> = BT.run {
    CorecursiveLaws.laws(BT) + RecursiveLaws.laws(BT, BT) + listOf(
      Law("Birecursive Laws: ana . cata == hylo") {
        forAll(intGen) {
          val composed = it
            .ana(Option.functor(), toGNatCoalgebra())
            .cata(Option.functor(), fromGNatAlgebra())
          val hylo = hylo(Option.functor(), fromGNatAlgebra(), toGNatCoalgebra(), it)
          hylo == composed
        }
      }
    )
  }
}
