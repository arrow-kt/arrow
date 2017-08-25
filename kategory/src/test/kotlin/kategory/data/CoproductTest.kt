package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    val EQ: Eq<HK3<CoproductHK, IdHK, IdHK, Int>> = object : Eq<HK3<CoproductHK, IdHK, IdHK, Int>> {
        override fun eqv(a: CoproductKind<IdHK, IdHK, Int>, b: CoproductKind<IdHK, IdHK, Int>): Boolean =
                a.ev().extract() == b.ev().extract()
    }

    init {
        testLaws(TraverseLaws.laws(Coproduct.traverse<IdHK, IdHK>(), Coproduct.functor<IdHK, IdHK>(), { Coproduct(Either.Right(Id(it))) }, EQ))
        testLaws(ComonadLaws.laws(Coproduct.comonad<IdHK, IdHK>(), { Coproduct(Either.Right(Id(it))) }, EQ))
    }
}
