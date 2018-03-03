package arrow.data

import arrow.Kind2
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SortedMapKTest : UnitSpec() {

    val EQ: Eq<Kind2<ForSortedMapK, String, Int>> = object : Eq<Kind2<ForSortedMapK, String, Int>> {
        override fun eqv(a: Kind2<ForSortedMapK, String, Int>, b: Kind2<ForSortedMapK, String, Int>): Boolean =
            a.fix()["key"] == b.fix()["key"]
    }

    init {

        "instances can be resolved implicitly" {
            functor<ForSortedMapK>() shouldNotBe null
            foldable<ForSortedMapK>() shouldNotBe null
            traverse<ForSortedMapK>() shouldNotBe null
            semigroup<SortedMapKOf<String, Int>>() shouldNotBe null
            monoid<SortedMapKOf<String, Int>>() shouldNotBe null
            show<SortedMapKOf<String, Int>>() shouldNotBe null
        }


        testLaws(
                ShowLaws.laws(show(), EQ) { sortedMapOf("key" to 1).k() },
                MonoidLaws.laws(SortedMapK.monoid<String, Int>(), sortedMapOf("key" to 1).k(), EQ),
                SemigroupLaws.laws(SortedMapK.monoid<String, Int>(),
                    sortedMapOf("key" to 1).k(),
                    sortedMapOf("key" to 2).k(),
                    sortedMapOf("key" to 3).k(),
                    EQ),
                TraverseLaws.laws(
                    SortedMapK.traverse<String>(),
                    SortedMapK.traverse<String>(),
                    { a: Int -> sortedMapOf("key" to a).k() },
                    EQ))

    }


}
