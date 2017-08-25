package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
    init {
        val applicative = NonEmptyList.applicative()

        testLaws(MonadLaws.laws(NonEmptyList.monad(), Eq.any()))
        testLaws(SemigroupKLaws.laws(
                NonEmptyList.semigroupK(),
                applicative,
                Eq.any()))
        testLaws(ComonadLaws.laws(NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any()))
        testLaws(TraverseLaws.laws(NonEmptyList.traverse(), applicative, { n: Int -> NonEmptyList.of(n) }, Eq.any()))

    }
}
