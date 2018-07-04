package arrow.test.laws

import arrow.core.Option
import arrow.core.functor
import arrow.recursion.hylo
import arrow.recursion.typeclasses.Birecursive
import arrow.test.generators.fromGNatAlgebra
import arrow.test.generators.toGNatCoalgebra
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BirecursiveLaws {
  inline fun <reified T> laws(BT: Birecursive<T>): List<Law> = BT.run {
    CorecursiveLaws.laws(BT) + RecursiveLaws.laws(BT, BT) + listOf(
      Law("Birecursive Laws: ana . cata == hylo") {
        forAll(Gen.choose(0, 1000)) {
          Option.functor().run {
            val composed = cata(ana(it, toGNatCoalgebra()), fromGNatAlgebra())
            val hylo = hylo(fromGNatAlgebra(), toGNatCoalgebra(), it)
            hylo == composed
          }
        }
      }
    )
  }
}
