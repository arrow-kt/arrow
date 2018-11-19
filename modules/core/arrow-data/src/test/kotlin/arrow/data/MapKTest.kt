package arrow.data

import arrow.Kind2
import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.semigroup
import arrow.instances.mapk.eq.eq
import arrow.instances.mapk.functor.functor
import arrow.instances.mapk.hash.hash
import arrow.instances.mapk.monoid.monoid
import arrow.instances.mapk.show.show
import arrow.instances.mapk.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForMapK, String, Int>> = object : Eq<Kind2<ForMapK, String, Int>> {
    override fun Kind2<ForMapK, String, Int>.eqv(b: Kind2<ForMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  init {
    val EQ_TC = MapK.eq(String.eq(), Int.eq())

    testLaws(
      ShowLaws.laws(MapK.show(), EQ_TC) { mapOf(it.toString() to it).k() },
      TraverseLaws.laws(MapK.traverse(), MapK.functor(), { a: Int -> mapOf("key" to a).k() }, EQ),
      MonoidLaws.laws(MapK.monoid<String, Int>(Int.semigroup()), mapOf("key" to 1).k(), EQ),
      SemigroupLaws.laws(MapK.monoid<String, Int>(Int.semigroup()),
        mapOf("key" to 1).k(),
        mapOf("key" to 2).k(),
        mapOf("key" to 3).k(),
        EQ),
      HashLaws.laws(MapK.hash(String.hash(), Int.hash()), EQ_TC) { mapOf("key" to it).k() }
    )
  }
}
