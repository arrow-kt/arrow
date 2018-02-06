package arrow.data

import arrow.HK
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SequenceKWTest : UnitSpec() {
    val applicative = SequenceKW.applicative()

    init {

        "instances can be resolved implicitly" {
            functor<ForSequenceKW>() shouldNotBe null
            applicative<ForSequenceKW>() shouldNotBe null
            monad<ForSequenceKW>() shouldNotBe null
            foldable<ForSequenceKW>() shouldNotBe null
            traverse<ForSequenceKW>() shouldNotBe null
            semigroupK<ForSequenceKW>() shouldNotBe null
            monoidK<ForSequenceKW>() shouldNotBe null
            semigroup<SequenceKW<Int>>() shouldNotBe null
            monoid<SequenceKW<Int>>() shouldNotBe null
            eq<SequenceKW<Int>>() shouldNotBe null
        }

        val eq: Eq<HK<ForSequenceKW, Int>> = object : Eq<HK<ForSequenceKW, Int>> {
            override fun eqv(a: HK<ForSequenceKW, Int>, b: HK<ForSequenceKW, Int>): Boolean =
                    a.toList() == b.toList()
        }

        testLaws(
            EqLaws.laws { sequenceOf(it).k() },
            MonadLaws.laws(SequenceKW.monad(), eq),
            MonoidKLaws.laws(SequenceKW.monoidK(), applicative, eq),
            TraverseLaws.laws(SequenceKW.traverse(), applicative, { n: Int -> SequenceKW(sequenceOf(n)) }, eq)
        )
    }
}
