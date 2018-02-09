package arrow.data

import arrow.instances.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<ForNonEmptyList>() shouldNotBe null
            applicative<ForNonEmptyList>() shouldNotBe null
            monad<ForNonEmptyList>() shouldNotBe null
            bimonad<ForNonEmptyList>() shouldNotBe null
            comonad<ForNonEmptyList>() shouldNotBe null
            foldable<ForNonEmptyList>() shouldNotBe null
            traverse<ForNonEmptyList>() shouldNotBe null
            semigroupK<ForNonEmptyList>() shouldNotBe null
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
