package arrow.data

import arrow.Kind3
import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    val EQ: Eq<Kind3<ForCoproduct, ForId, ForId, Int>> = Eq { a, b ->
        a.fix().fix() == b.fix().fix()
    }

    init {
        val TF = Coproduct.traverse(Id.traverse(), Id.traverse())
        val FF = Coproduct.functor(Id.functor(), Id.functor())
        val CM = Coproduct.comonad(Id.comonad(), Id.comonad())

        testLaws(
                TraverseLaws.laws(TF, FF, { Coproduct(Right(Id(it))) }, EQ),
                ComonadLaws.laws(CM, { Coproduct(Right(Id(it))) }, EQ)
        )

    }
}
