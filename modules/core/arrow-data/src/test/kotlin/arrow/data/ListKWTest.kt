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
class ListKTest : UnitSpec() {
    val applicative = ListK.applicative()

    init {

        "instances can be resolved implicitly" {
            functor<ForListK>() shouldNotBe null
            applicative<ForListK>() shouldNotBe null
            monad<ForListK>() shouldNotBe null
            foldable<ForListK>() shouldNotBe null
            traverse<ForListK>() shouldNotBe null
            semigroupK<ForListK>() shouldNotBe null
            semigroup<ListK<Int>>() shouldNotBe null
            monoid<ListK<Int>>() shouldNotBe null
            monoidK<ListK<ForListK>>() shouldNotBe null
            monadFilter<ForListK>() shouldNotBe null
            monadCombine<ListK<ForListK>>() shouldNotBe null
            functorFilter<ListK<ForListK>>() shouldNotBe null
            monadFilter<ListK<ForListK>>() shouldNotBe null
            eq<ListK<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { listOf(it).k() },
            SemigroupKLaws.laws(ListK.semigroupK(), applicative, Eq.any()),
            MonoidKLaws.laws(ListK.monoidK(), applicative, Eq.any()),
            TraverseLaws.laws(ListK.traverse(), applicative, { n: Int -> ListK(listOf(n)) }, Eq.any()),
            MonadCombineLaws.laws(ListK.monadCombine(),
                { n -> ListK(listOf(n)) },
                { n -> ListK(listOf({ s: Int -> n * s })) },
                object : Eq<Kind<ForListK, Int>> {
                    override fun eqv(a: Kind<ForListK, Int>, b: Kind<ForListK, Int>): Boolean =
                            a.extract().list == b.extract().list
                })
        )
    }
}
