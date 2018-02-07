package arrow.data

import arrow.Kind
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
            functor<ForListKW>() shouldNotBe null
            applicative<ForListKW>() shouldNotBe null
            monad<ForListKW>() shouldNotBe null
            foldable<ForListKW>() shouldNotBe null
            traverse<ForListKW>() shouldNotBe null
            semigroupK<ForListKW>() shouldNotBe null
            semigroup<ListKW<Int>>() shouldNotBe null
            monoid<ListKW<Int>>() shouldNotBe null
            monoidK<ListKW<ForListKW>>() shouldNotBe null
            monadFilter<ForListKW>() shouldNotBe null
            monadCombine<ListKW<ForListKW>>() shouldNotBe null
            functorFilter<ListKW<ForListKW>>() shouldNotBe null
            monadFilter<ListKW<ForListKW>>() shouldNotBe null
            eq<ListKW<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { listOf(it).k() },
            SemigroupKLaws.laws(ListKW.semigroupK(), applicative, Eq.any()),
            MonoidKLaws.laws(ListKW.monoidK(), applicative, Eq.any()),
            TraverseLaws.laws(ListKW.traverse(), applicative, { n: Int -> ListKW(listOf(n)) }, Eq.any()),
            MonadCombineLaws.laws(ListKW.monadCombine(),
                { n -> ListKW(listOf(n)) },
                { n -> ListKW(listOf({ s: Int -> n * s })) },
                object : Eq<Kind<ForListKW, Int>> {
                    override fun eqv(a: Kind<ForListKW, Int>, b: Kind<ForListKW, Int>): Boolean =
                            a.reify().list == b.reify().list
                })
        )
    }
}
