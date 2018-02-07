package arrow.data

import arrow.Kind2
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SortedMapKWTest : UnitSpec() {

    val EQ: Eq<Kind2<ForSortedMapKW, String, Int>> = object : Eq<Kind2<ForSortedMapKW, String, Int>> {
        override fun eqv(a: Kind2<ForSortedMapKW, String, Int>, b: Kind2<ForSortedMapKW, String, Int>): Boolean =
            a.reify()["key"] == b.reify()["key"]
    }

    init {

        "instances can be resolved implicitly" {
            functor<ForSortedMapKW>() shouldNotBe null
            foldable<ForSortedMapKW>() shouldNotBe null
            traverse<ForSortedMapKW>() shouldNotBe null
            semigroup<SortedMapKWOf<String, Int>>() shouldNotBe null
            monoid<SortedMapKWOf<String, Int>>() shouldNotBe null
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
