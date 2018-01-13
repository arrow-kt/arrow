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
class MapKWTest : UnitSpec() {

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
            SemigroupLaws.laws(MapKW.semigroup(), { n -> MapKW(mapOf("key" to n)) }, Eq.any()),
            MonoidLaws.laws(MapKW.monoid(), { n -> mapOf("key" to n).k() }, Eq.any()),
            EqLaws.laws { mapOf(it.toString() to it).k() },
            TraverseLaws.laws(MapKW.traverse(),
                MapKW.traverse(),
                { a: Int -> mapOf<String, Int>("key" to a).k() })
        )
    }
}
