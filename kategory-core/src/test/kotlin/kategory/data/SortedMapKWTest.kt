package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SortedMapKWTest : UnitSpec() {

    val EQ: Eq<HK2<SortedMapKWHK, String, Int>> = object : Eq<HK2<SortedMapKWHK, String, Int>> {
        override fun eqv(a: HK2<SortedMapKWHK, String, Int>, b: HK2<SortedMapKWHK, String, Int>): Boolean =
            a.ev().get("key") == b.ev().get("key")
    }

    val SG: Semigroup<Int> = object : Semigroup<Int> {
        override fun combine(a: Int, b: Int): Int {
            return a*b
        }
    }

    init {

        "instances can be resolved implicitly" {
            functor<SortedMapKWHK>() shouldNotBe null
            foldable<SortedMapKWHK>() shouldNotBe null
            traverse<SortedMapKWHK>() shouldNotBe null
            semigroup<SortedMapKWKind<String, Int>>() shouldNotBe null
            monoid<SortedMapKWKind<String, Int>>() shouldNotBe null
        }

        val monoid = SortedMapKW.monoid<String, Int>(SG)

        "Monoid Laws: identity" {
            val identity = monoid.empty()

            forAll { a: Int ->
                val map: SortedMapKW<String, Int> = sortedMapOf("key" to a).k()
                val result : SortedMapKW<String, Int> = monoid.combine(identity, map).ev()
                result["key"] == map["key"]
            }

            forAll { a: Int ->
                val map: SortedMapKW<String, Int> = sortedMapOf("key" to a).k()
                val result: SortedMapKW<String, Int> = monoid.combine(map, identity).ev()
                result["key"] == map["key"]
            }
        }

        "Semigroup Laws: associativity" {
            forAll { a: Int, b: Int, c: Int ->
                val mapA = sortedMapOf("A" to a).k()
                val mapB = sortedMapOf("B" to b).k()
                val mapC = sortedMapOf("C" to c).k()

                monoid.combine(mapA, monoid.combine(mapB, mapC)) == monoid.combine(monoid.combine(mapA, mapB), mapC)
            }
        }

        testLaws(TraverseLaws.laws(
                SortedMapKW.traverse<String>(),
                SortedMapKW.traverse<String>(),
                { a: Int -> sortedMapOf("key" to a).k() },
                EQ))

    }


}
