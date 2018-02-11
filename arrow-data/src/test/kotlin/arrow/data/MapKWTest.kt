package arrow.data

import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MapKWLaws
import arrow.test.laws.TraverseLaws
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
                MapKWLaws.laws(MapKW.monoid(SG), Gen.string(), Gen.int())
        )
    }
}
