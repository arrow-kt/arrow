package arrow

import arrow.data.MapKW
import arrow.data.k
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import arrow.laws.EqLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKWTest : UnitSpec() {

    val SG: Semigroup<Int> = object : Semigroup<Int> {
        override fun combine(a: Int, b: Int): Int =
                a * b
    }

    init {

        "instances can be resolved implicitly" {
            functor<MapKWHK>() shouldNotBe null
            foldable<MapKWHK>() shouldNotBe null
            traverse<MapKWHK>() shouldNotBe null
            semigroup<MapKWKind<String, Int>>() shouldNotBe null
            monoid<MapKWKind<String, Int>>() shouldNotBe null
            eq<MapKW<String, Int>>() shouldNotBe null
        }

        val monoid = MapKW.monoid<String, Int>(SG)

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
            TraverseLaws.laws(MapKW.traverse<String>(), MapKW.traverse<String>(), { a: Int -> mapOf<String, Int>("key" to a).k() })
        )
    }
}
