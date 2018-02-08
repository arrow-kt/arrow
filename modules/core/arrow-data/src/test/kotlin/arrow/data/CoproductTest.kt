package arrow.data

import arrow.Kind3
import arrow.core.*
import arrow.syntax.comonad.extract
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    val EQ: Eq<Kind3<ForCoproduct, ForId, ForId, Int>> = Eq { a, b ->
        a.extract().extract() == b.extract().extract()
    }

    init {

        "instances can be resolved implicitly" {
            functor<CoproductPartialOf<ForId, ForNonEmptyList>>() shouldNotBe null
            comonad<CoproductPartialOf<ForId, ForNonEmptyList>>()  shouldNotBe null
            foldable<CoproductPartialOf<ForId, ForNonEmptyList>>() shouldNotBe null
            traverse<CoproductPartialOf<ForId, ForNonEmptyList>>() shouldNotBe null
        }

        testLaws(
            TraverseLaws.laws(traverse(), functor(), { Coproduct(Right(Id(it))) }, EQ),
            ComonadLaws.laws(comonad(), { Coproduct(Right(Id(it))) }, EQ)
        )

    }
}
