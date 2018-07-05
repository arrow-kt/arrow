package arrow.data

import arrow.Kind2
import arrow.instances.IntMonoidInstance
import arrow.instances.ForSortedMapK
import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SortedMapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForSortedMapK, String, Int>> = object : Eq<Kind2<ForSortedMapK, String, Int>> {
    override fun Kind2<ForSortedMapK, String, Int>.eqv(b: Kind2<ForSortedMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  init {

    ForSortedMapK<String>() extensions {
      testLaws(
        ShowLaws.laws(SortedMapK.show(), EQ) { sortedMapOf("key" to 1).k() },
        MonoidLaws.laws(SortedMapK.monoid<String, Int>(Int.monoid()), sortedMapOf("key" to 1).k(), EQ),
        SemigroupLaws.laws(SortedMapK.monoid<String, Int>(Int.monoid()),
          sortedMapOf("key" to 1).k(),
          sortedMapOf("key" to 2).k(),
          sortedMapOf("key" to 3).k(),
          EQ),
        TraverseLaws.laws(
          this,
          this,
          { a: Int -> sortedMapOf("key" to a).k() },
          EQ))
    }

  }

}
