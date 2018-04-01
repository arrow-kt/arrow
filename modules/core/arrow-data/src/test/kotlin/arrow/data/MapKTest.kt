package arrow.data

import arrow.Kind2
import arrow.instances.IntEqInstance
import arrow.instances.IntSemigroupInstance
import arrow.instances.StringEqInstance
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
    val EQ_TC = MapK.eq(StringEqInstance, IntEqInstance)

    testLaws(
      EqLaws.laws(EQ_TC) { mapOf(it.toString() to it).k() },
      ShowLaws.laws(MapK.show(), EQ_TC) { mapOf(it.toString() to it).k() },
      TraverseLaws.laws(MapK.traverse(), MapK.traverse(), { a: Int -> mapOf("key" to a).k() }, EQ),
      MonoidLaws.laws(MapK.monoid<String, Int>(IntSemigroupInstance), mapOf("key" to 1).k(), EQ),
      SemigroupLaws.laws(MapK.monoid<String, Int>(IntSemigroupInstance),
        mapOf("key" to 1).k(),
        mapOf("key" to 2).k(),
        mapOf("key" to 3).k(),
        EQ)
    )
  }
}
