package arrow.data

import arrow.HK2
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKWTest : UnitSpec() {

    val SG: Semigroup<Int> = object : Semigroup<Int> {
        override fun combine(a: Int, b: Int): Int =
                a * b
    }

    val EQ: Eq<HK2<MapKWHK, String, Int>> = object : Eq<HK2<MapKWHK, String, Int>> {
        override fun eqv(a: HK2<MapKWHK, String, Int>, b: HK2<MapKWHK, String, Int>): Boolean =
                a.ev()["key"] == b.ev()["key"]
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

        testLaws(
                EqLaws.laws { mapOf(it.toString() to it).k() },
                TraverseLaws.laws(MapKW.traverse(), MapKW.traverse(), { a: Int -> mapOf("key" to a).k() }),
                MonoidLaws.laws(MapKW.monoid<String, Int>(), mapOf("key" to 1).k(), EQ),
                SemigroupLaws.laws(MapKW.monoid<String, Int>(),
                        mapOf("key" to 1).k(),
                        mapOf("key" to 2).k(),
                        mapOf("key" to 3).k(),
                        EQ)
        )
    }
}
