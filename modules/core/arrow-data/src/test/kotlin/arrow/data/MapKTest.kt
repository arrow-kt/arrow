package arrow.data

import arrow.Kind2
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapKTest : UnitSpec() {

    val EQ: Eq<Kind2<ForMapK, String, Int>> = object : Eq<Kind2<ForMapK, String, Int>> {
        override fun eqv(a: Kind2<ForMapK, String, Int>, b: Kind2<ForMapK, String, Int>): Boolean =
                a.fix()["key"] == b.fix()["key"]
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
                TraverseLaws.laws(MapK.traverse(), MapK.traverse(), { a: Int -> mapOf("key" to a).k() }),
                MonoidLaws.laws(MapK.monoid(), mapOf("key" to 1).k(), EQ),
                SemigroupLaws.laws(MapK.monoid(),
                        mapOf("key" to 1).k(),
                        mapOf("key" to 2).k(),
                        mapOf("key" to 3).k(),
                        EQ)
        )
    }
}
