package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import kategory.laws.EqLaws
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
            SemigroupLaws.laws(MapKW.semigroup<String, Int>(SG),
                MapKW(mapOf("key" to 1)),
                MapKW(mapOf("key" to 2)),
                MapKW(mapOf("key" to 3)),
                Eq.any()),
            MonoidLaws.laws(MapKW.monoid<String, Int>(SG), MapKW(mapOf("key" to 1)), Eq.any()),
            EqLaws.laws { mapOf(it.toString() to it).k() },
            TraverseLaws.laws(MapKW.traverse(),
                MapKW.traverse(),
                { a: Int -> mapOf<String, Int>("key" to a).k() })
        )
    }
}
