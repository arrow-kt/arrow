package arrow.core

/* FIXME(paco) this code sends gradle into an infinite loop, and turns IntelliJ into a memory churner
import arrow.instances.eq
import arrow.instances.monoid
import arrow.instances.tuple2.comonad.comonad
import arrow.instances.tuple2.eq.eq
import arrow.instances.tuple2.functor.functor
import arrow.instances.tuple2.monad.monad
import arrow.instances.tuple2.show.show
import arrow.instances.tuple2.traverse.traverse
import arrow.instances.tuple3.eq.eq
import arrow.instances.tuple3.show.show
import arrow.instances.tuple4.eq.eq
import arrow.instances.tuple4.show.show
import arrow.instances.tuple5.eq.eq
import arrow.instances.tuple5.show.show
import arrow.instances.tuple6.eq.eq
import arrow.instances.tuple6.show.show
import arrow.instances.tuple7.eq.eq
import arrow.instances.tuple7.show.show
import arrow.instances.tuple8.eq.eq
import arrow.instances.tuple8.show.show
import arrow.instances.tuple9.eq.eq
import arrow.instances.tuple9.show.show
import arrow.instances.tuple10.eq.eq
import arrow.instances.tuple10.show.show
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleTest : UnitSpec() {
  init {
            testLaws(
                    MonadLaws.laws(Tuple2.monad(Int.monoid()), Eq.any()),
                    ComonadLaws.laws(Tuple2.comonad(), { 0 toT it }, Eq.any()),
                    TraverseLaws.laws(Tuple2.traverse(), Tuple2.functor(), { 0 toT it }, Eq.any()),
                    EqLaws.laws(Tuple2.eq(Int.eq(), Int.eq())) { Tuple2(it, it) },
                    ShowLaws.laws(Tuple2.show(), Tuple2.eq(Int.eq(), Int.eq())) { Tuple2(it, it) },
                    EqLaws.laws(Tuple3.eq(Int.eq(), Int.eq(), Int.eq())) { Tuple3(it, it, it) },
                    ShowLaws.laws(Tuple3.show(), Tuple3.eq(Int.eq(), Int.eq(), Int.eq())) { Tuple3(it, it, it) },
                    EqLaws.laws(Tuple4.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple4(it, it, it, it) },
                    ShowLaws.laws(Tuple4.show(), Tuple4.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple4(it, it, it, it) },
                    EqLaws.laws(Tuple5.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple5(it, it, it, it, it) },
                    ShowLaws.laws(Tuple5.show(), Tuple5.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple5(it, it, it, it, it) },
                    EqLaws.laws(Tuple6.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple6(it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple6.show(), Tuple6.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple6(it, it, it, it, it, it) },
                    EqLaws.laws(Tuple7.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple7(it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple7.show(), Tuple7.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple7(it, it, it, it, it, it, it) },
                    EqLaws.laws(Tuple8.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple8(it, it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple8.show(), Tuple8.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple8(it, it, it, it, it, it, it, it) },
                    EqLaws.laws(Tuple9.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple9(it, it, it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple9.show(), Tuple9.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple9(it, it, it, it, it, it, it, it, it) },
                    EqLaws.laws(Tuple10.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple10(it, it, it, it, it, it, it, it, it, it) },
                    ShowLaws.laws(Tuple10.show(), Tuple10.eq(Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq(), Int.eq())) { Tuple10(it, it, it, it, it, it, it, it, it, it) }
            )
  }
}*/
