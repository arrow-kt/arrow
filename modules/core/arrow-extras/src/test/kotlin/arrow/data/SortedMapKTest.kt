package arrow.data

import arrow.Kind2
import arrow.core.extensions.monoid
import arrow.data.extensions.functor
import arrow.data.extensions.monoid
import arrow.data.extensions.show
import arrow.data.extensions.traverse
import arrow.test.UnitSpec
import arrow.test.generators.genSortedMapK
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class SortedMapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForSortedMapK, String, Int>> = object : Eq<Kind2<ForSortedMapK, String, Int>> {
    override fun Kind2<ForSortedMapK, String, Int>.eqv(b: Kind2<ForSortedMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  init {

    testLaws(
      ShowLaws.laws(SortedMapK.show(), EQ) { sortedMapOf("key" to 1).k() },
      MonoidLaws.laws(SortedMapK.monoid<String, Int>(Int.monoid()), genSortedMapK(Gen.string(), Gen.int()), EQ),
      SemigroupLaws.laws(SortedMapK.monoid<String, Int>(Int.monoid()),
        sortedMapOf("key" to 1).k(),
        sortedMapOf("key" to 2).k(),
        sortedMapOf("key" to 3).k(),
        EQ),
      TraverseLaws.laws(
        SortedMapK.traverse<String>(),
        SortedMapK.functor<String>(),
        { a: Int -> sortedMapOf("key" to a).k() },
        EQ))

  }

}
