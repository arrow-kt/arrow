package arrow.data

import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleTest : UnitSpec() {
  init {
    /* FIXME(paco) this code sends gradle into an infinite loop, and turns IntelliJ into a memory churner
            testLaws(
                    MonadLaws.laws(Tuple2.monad(IntMonoidInstance), Eq.any()),
                    ComonadLaws.laws(Tuple2.comonad(), { 0 toT it }, Eq.any()),
                    TraverseLaws.laws(Tuple2.traverse(), Tuple2.functor(), { 0 toT it }, Eq.any()),
                    EqLaws.laws(Tuple2.eq(IntEqInstance, IntEqInstance)) { Tuple2(it, it) },
                    ShowLaws.laws(Tuple2.show(), Tuple2.eq(IntEqInstance, IntEqInstance)) { Tuple2(it, it) },
                    EqLaws.laws(Tuple3.eq(IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple3(it, it, it) },
                    ShowLaws.laws(Tuple3.show(), Tuple3.eq(IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple3(it, it, it) },
                    EqLaws.laws(Tuple4.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple4(it, it, it, it) },
                    ShowLaws.laws(Tuple4.show(), Tuple4.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple4(it, it, it, it) },
                    EqLaws.laws(Tuple5.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple5(it, it, it, it, it) },
                    ShowLaws.laws(Tuple5.show(), Tuple5.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple5(it, it, it, it, it) },
                    EqLaws.laws(Tuple6.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple6(it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple6.show(), Tuple6.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple6(it, it, it, it, it, it) },
                    EqLaws.laws(Tuple7.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple7(it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple7.show(), Tuple7.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple7(it, it, it, it, it, it, it) },
                    EqLaws.laws(Tuple8.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple8(it, it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple8.show(), Tuple8.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple8(it, it, it, it, it, it, it, it) },
                    EqLaws.laws(Tuple9.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple9(it, it, it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple9.show(), Tuple9.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple9(it, it, it, it, it, it, it, it, it) },
                    EqLaws.laws(Tuple10.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple10(it, it, it, it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple10.show(), Tuple10.eq(IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance, IntEqInstance)) { Tuple10(it, it, it, it, it, it, it, it, it, it) }
            )
    */
  }
}
