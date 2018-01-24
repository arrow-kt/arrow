package arrow.data

import arrow.HK
import arrow.mtl.functorFilter
import arrow.mtl.monadCombine
import arrow.mtl.monadFilter
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListKWTest : UnitSpec() {
    val applicative = ListKW.applicative()

    init {

        "instances can be resolved implicitly" {
            functor<ListKWHK>() shouldNotBe null
            applicative<ListKWHK>() shouldNotBe null
            monad<ListKWHK>() shouldNotBe null
            foldable<ListKWHK>() shouldNotBe null
            traverse<ListKWHK>() shouldNotBe null
            semigroupK<ListKWHK>() shouldNotBe null
            semigroup<ListKW<Int>>() shouldNotBe null
            monoid<ListKW<Int>>() shouldNotBe null
            monoidK<ListKW<ListKWHK>>() shouldNotBe null
            monadFilter<ListKWHK>() shouldNotBe null
            monadCombine<ListKW<ListKWHK>>() shouldNotBe null
            functorFilter<ListKW<ListKWHK>>() shouldNotBe null
            monadFilter<ListKW<ListKWHK>>() shouldNotBe null
            eq<ListKW<Int>>() shouldNotBe null
            show<ListKW<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { listOf(it).k() },
            ShowLaws.laws { listOf(it).k() },
            SemigroupKLaws.laws(ListKW.semigroupK(), applicative, Eq.any()),
            MonoidKLaws.laws(ListKW.monoidK(), applicative, Eq.any()),
            TraverseLaws.laws(ListKW.traverse(), applicative, { n: Int -> ListKW(listOf(n)) }, Eq.any()),
            MonadCombineLaws.laws(ListKW.monadCombine(),
                { n -> ListKW(listOf(n)) },
                { n -> ListKW(listOf({ s: Int -> n * s })) },
                object : Eq<HK<ListKWHK, Int>> {
                    override fun eqv(a: HK<ListKWHK, Int>, b: HK<ListKWHK, Int>): Boolean =
                            a.ev().list == b.ev().list
                })
        )
    }
}
