package arrow.data

import arrow.core.*
import arrow.instances.IntMonoidInstance
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleTest : UnitSpec() {
    init {

        testLaws(
                MonadLaws.laws(Tuple2.monad(IntMonoidInstance), Eq.any()),
                ComonadLaws.laws(Tuple2.comonad(), { 0 toT it }, Eq.any()),
                TraverseLaws.laws(Tuple2.traverse(), Tuple2.functor(), { 0 toT it }, Eq.any())
                /* TODO(paco) another time
                EqLaws.laws { Tuple2(it, it) },
                ShowLaws.laws { Tuple2(it, it) },
                EqLaws.laws { Tuple3(it, it, it) },
                ShowLaws.laws { Tuple3(it, it, it) },
                EqLaws.laws { Tuple4(it, it, it, it) },
                ShowLaws.laws { Tuple4(it, it, it, it) },
                EqLaws.laws { Tuple5(it, it, it, it, it) },
                ShowLaws.laws { Tuple5(it, it, it, it, it) },
                EqLaws.laws { Tuple6(it, it, it, it, it, it) },
                ShowLaws.laws { Tuple6(it, it, it, it, it, it) },
                EqLaws.laws { Tuple7(it, it, it, it, it, it, it) },
                ShowLaws.laws { Tuple7(it, it, it, it, it, it, it) },
                EqLaws.laws { Tuple8(it, it, it, it, it, it, it, it) },
                ShowLaws.laws { Tuple8(it, it, it, it, it, it, it, it) },
                EqLaws.laws { Tuple9(it, it, it, it, it, it, it, it, it) },
                ShowLaws.laws { Tuple9(it, it, it, it, it, it, it, it, it) },
                EqLaws.laws { Tuple10(it, it, it, it, it, it, it, it, it, it) },
                ShowLaws.laws { Tuple10(it, it, it, it, it, it, it, it, it, it) }
                */
        )
    }
}
