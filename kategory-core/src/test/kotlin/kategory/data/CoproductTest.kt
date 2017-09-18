package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    val EQ: Eq<HK3<CoproductHK, IdHK, IdHK, Int>> = Eq { a, b ->
                a.ev().extract() == b.ev().extract()
    }

    init {

        "instances can be resolved implicitly" {
            functor<CoproductKindPartial<IdHK, NonEmptyListHK>>() shouldNotBe null
            comonad<CoproductKindPartial<IdHK, NonEmptyListHK>>()  shouldNotBe null
            foldable<CoproductKindPartial<IdHK, NonEmptyListHK>>() shouldNotBe null
            traverse<CoproductKindPartial<IdHK, NonEmptyListHK>>() shouldNotBe null
        }

        testLaws(TraverseLaws.laws(traverse(), functor(), { Coproduct(Either.Right(Id(it))) }, EQ))
        testLaws(ComonadLaws.laws(comonad(), { Coproduct(Either.Right(Id(it))) }, EQ))

    }
}
