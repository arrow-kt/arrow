package arrow

import arrow.data.SortedMapKW
import arrow.data.k
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec

@RunWith(KTestJUnitRunner::class)
class SortedMapKWTest : UnitSpec() {

    val EQ: Eq<HK2<SortedMapKWHK, String, Int>> = object : Eq<HK2<SortedMapKWHK, String, Int>> {
        override fun eqv(a: HK2<SortedMapKWHK, String, Int>, b: HK2<SortedMapKWHK, String, Int>): Boolean =
            a.ev()["key"] == b.ev()["key"]
    }

    init {

        "instances can be resolved implicitly" {
            functor<SortedMapKWHK>() shouldNotBe null
            foldable<SortedMapKWHK>() shouldNotBe null
            traverse<SortedMapKWHK>() shouldNotBe null
            semigroup<SortedMapKWKind<String, Int>>() shouldNotBe null
            monoid<SortedMapKWKind<String, Int>>() shouldNotBe null
        }


        testLaws(
                MonoidLaws.laws(SortedMapKW.monoid<String, Int>(), sortedMapOf("key" to 1).k(), EQ),
                SemigroupLaws.laws(SortedMapKW.monoid<String, Int>(),
                    sortedMapOf("key" to 1).k(),
                    sortedMapOf("key" to 2).k(),
                    sortedMapOf("key" to 3).k(),
                    EQ),
                TraverseLaws.laws(
                    SortedMapKW.traverse<String>(),
                    SortedMapKW.traverse<String>(),
                    { a: Int -> sortedMapOf("key" to a).k() },
                    EQ))

    }


}
