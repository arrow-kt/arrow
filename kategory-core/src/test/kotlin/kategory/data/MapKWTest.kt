package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKWTest : UnitSpec() {

    val EQ: Eq<HK2<MapKWHK, String, Int>> = object : Eq<HK2<MapKWHK, String, Int>> {
        override fun eqv(a: HK2<MapKWHK, String, Int>, b: HK2<MapKWHK, String, Int>): Boolean =
                a.ev().get("key") == b.ev().get("key")
    }

    val SG: Semigroup<Int> = object : Semigroup<Int> {
        override fun combine(a: Int, b: Int): Int {
            return a * b
        }
    }


    init {

        "instances can be resolved implicitly" {
            functor<MapKWHK>() shouldNotBe null
            foldable<MapKWHK>() shouldNotBe null
            traverse<MapKWHK>() shouldNotBe null
            semigroup<MapKWKind<String, Int>>() shouldNotBe null
            monoid<MapKWKind<String, Int>>() shouldNotBe null
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

        testLaws(TraverseLaws.laws(MapKW.traverse<String>(), MapKW.traverse<String>(), { a: Int -> mapOf<String, Int>("key" to a).k() }, EQ))
    }
}