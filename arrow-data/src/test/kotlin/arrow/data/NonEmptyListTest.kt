package arrow

import arrow.data.NonEmptyList
import arrow.data.nel
import arrow.instances.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.*

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<NonEmptyListHK>() shouldNotBe null
            applicative<NonEmptyListHK>() shouldNotBe null
            monad<NonEmptyListHK>() shouldNotBe null
            bimonad<NonEmptyListHK>() shouldNotBe null
            comonad<NonEmptyListHK>() shouldNotBe null
            foldable<NonEmptyListHK>() shouldNotBe null
            traverse<NonEmptyListHK>() shouldNotBe null
            semigroupK<NonEmptyListHK>() shouldNotBe null
            semigroup<NonEmptyList<Int>>() shouldNotBe null
            eq<NonEmptyList<Int>>() shouldNotBe null
        }

        val applicative = NonEmptyList.applicative()

        testLaws(
            EqLaws.laws { it.nel() },
            MonadLaws.laws(NonEmptyList.monad(), Eq.any()),
            SemigroupKLaws.laws(
                NonEmptyList.semigroupK(),
                applicative,
                Eq.any()),
            ComonadLaws.laws(NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any()),
            TraverseLaws.laws(NonEmptyList.traverse(), applicative, { n: Int -> NonEmptyList.of(n) }, Eq.any())
        )

    }
}
