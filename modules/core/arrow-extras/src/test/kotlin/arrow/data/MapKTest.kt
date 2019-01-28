package arrow.data

import arrow.Kind2
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.semigroup
import arrow.data.extensions.mapk.eq.eq
import arrow.data.extensions.mapk.functor.functor
import arrow.data.extensions.mapk.hash.hash
import arrow.data.extensions.mapk.monoid.monoid
import arrow.data.extensions.mapk.show.show
import arrow.data.extensions.mapk.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.generators.genMapK
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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
      MonoidLaws.laws(MapK.monoid<String, Int>(Int.semigroup()), genMapK(Gen.string(), Gen.int()), EQ),
      SemigroupLaws.laws(MapK.monoid<String, Int>(Int.semigroup()),
        mapOf("key" to 1).k(),
        mapOf("key" to 2).k(),
        mapOf("key" to 3).k(),
        EQ),
      HashLaws.laws(MapK.hash(String.hash(), Int.hash()), EQ_TC) { mapOf("key" to it).k() }
    )
  }
}
