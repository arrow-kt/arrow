package arrow.data

import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKTest : UnitSpec() {

    val SG: Semigroup<Int> = object : Semigroup<Int> {
        override fun combine(a: Int, b: Int): Int =
                a * b
    }

    init {

        "instances can be resolved implicitly" {
            functor<ForMapK>() shouldNotBe null
            foldable<ForMapK>() shouldNotBe null
            traverse<ForMapK>() shouldNotBe null
            semigroup<MapKOf<String, Int>>() shouldNotBe null
            monoid<MapKOf<String, Int>>() shouldNotBe null
            eq<MapK<String, Int>>() shouldNotBe null
        }

        val monoid = MapK.monoid<String, Int>(SG)

        "Monoid Laws: identity" {
            val identity = monoid.empty()

            forAll { a: Int ->
                val map = mapOf("key" to a).k()
                monoid.combine(identity, map)["key"] == map["key"]
            }

            forAll { a: Int ->
                val map = mapOf("key" to a).k()
                monoid.combine(map, identity)["key"] == map["key"]
            }
        }

        "Semigroup laws: associativity" {
            forAll { a: Int, b: Int, c: Int ->
                val mapA = mapOf("key" to a).k()
                val mapB = mapOf("key" to b).k()
                val mapC = mapOf("key" to c).k()

                monoid.combine(mapA, monoid.combine(mapB, mapC)) == monoid.combine(monoid.combine(mapA, mapB), mapC)
            }
        }

        testLaws(
            EqLaws.laws { mapOf(it.toString() to it).k() },
            TraverseLaws.laws(MapK.traverse<String>(), MapK.traverse<String>(), { a: Int -> mapOf<String, Int>("key" to a).k() })
        )
    }
}
