package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import kategory.laws.EqLaws
import org.junit.runner.RunWith

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

        testLaws(MonadLaws.laws(NonEmptyList.monad(), Eq.any()))
        testLaws(SemigroupKLaws.laws(
                NonEmptyList.semigroupK(),
                applicative,
                Eq.any()))
        testLaws(ComonadLaws.laws(NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any()))
        testLaws(TraverseLaws.laws(NonEmptyList.traverse(), applicative, { n: Int -> NonEmptyList.of(n) }, Eq.any()))
        testLaws(EqLaws.laws { it.nel() })

    }
}
