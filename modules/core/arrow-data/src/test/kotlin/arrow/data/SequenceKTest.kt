package arrow.data

import arrow.Kind
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SequenceKTest : UnitSpec() {
    val applicative = SequenceK.applicative()

    init {

        "instances can be resolved implicitly" {
            functor<ForSequenceK>() shouldNotBe null
            applicative<ForSequenceK>() shouldNotBe null
            monad<ForSequenceK>() shouldNotBe null
            foldable<ForSequenceK>() shouldNotBe null
            traverse<ForSequenceK>() shouldNotBe null
            semigroupK<ForSequenceK>() shouldNotBe null
            monoidK<ForSequenceK>() shouldNotBe null
            semigroup<SequenceK<Int>>() shouldNotBe null
            monoid<SequenceK<Int>>() shouldNotBe null
            eq<SequenceK<Int>>() shouldNotBe null
            show<SequenceK<Int>>() shouldNotBe null
        }

        val eq: Eq<Kind<ForSequenceK, Int>> = object : Eq<Kind<ForSequenceK, Int>> {
            override fun eqv(a: Kind<ForSequenceK, Int>, b: Kind<ForSequenceK, Int>): Boolean =
                    a.toList() == b.toList()
        }

        val show: Show<Kind<ForSequenceK, Int>> = object : Show<Kind<ForSequenceK, Int>> {
            override fun show(a: Kind<ForSequenceK, Int>): String =
                    a.toList().toString()
        }

        testLaws(
            EqLaws.laws { sequenceOf(it).k() },
            ShowLaws.laws(show, eq) { sequenceOf(it).k() },
            MonadLaws.laws(SequenceK.monad(), eq),
            MonoidKLaws.laws(SequenceK.monoidK(), applicative, eq),
            TraverseLaws.laws(SequenceK.traverse(), applicative, { n: Int -> SequenceK(sequenceOf(n)) }, eq)
        )
    }
}
