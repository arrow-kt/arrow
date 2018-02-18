package arrow.data

import arrow.HK2
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKTest : UnitSpec() {

    val EQ: Eq<HK2<MapKWHK, String, Int>> = object : Eq<HK2<MapKWHK, String, Int>> {
        override fun eqv(a: HK2<MapKWHK, String, Int>, b: HK2<MapKWHK, String, Int>): Boolean =
                a.ev()["key"] == b.ev()["key"]
    }

    init {

        "instances can be resolved implicitly" {
            functor<ForMapK>() shouldNotBe null
            foldable<ForMapK>() shouldNotBe null
            traverse<ForMapK>() shouldNotBe null
            semigroup<MapKOf<String, Int>>() shouldNotBe null
            monoid<MapKOf<String, Int>>() shouldNotBe null
            eq<MapK<String, Int>>() shouldNotBe null
            show<MapK<String, Int>>() shouldNotBe null
        }

        testLaws(
                EqLaws.laws { mapOf(it.toString() to it).k() },
                ShowLaws.laws { mapOf(it.toString() to it).k() },
                TraverseLaws.laws(MapKW.traverse(), MapKW.traverse(), { a: Int -> mapOf("key" to a).k() }),
                MonoidLaws.laws(MapKW.monoid(), mapOf("key" to 1).k(), EQ),
                SemigroupLaws.laws(MapKW.monoid(),
                        mapOf("key" to 1).k(),
                        mapOf("key" to 2).k(),
                        mapOf("key" to 3).k(),
                        EQ)
        )
    }
}
