package arrow.core

/* FIXME(paco) this code sends gradle into an infinite loop, and turns IntelliJ into a memory churner
import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.tuple2.comonad.comonad
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.extensions.tuple2.functor.functor
import arrow.core.extensions.tuple2.monad.monad
import arrow.core.extensions.tuple2.show.show
import arrow.core.extensions.tuple2.traverse.traverse
import arrow.core.extensions.tuple3.eq.eq
import arrow.core.extensions.tuple3.show.show
import arrow.core.extensions.tuple4.eq.eq
import arrow.core.extensions.tuple4.show.show
import arrow.core.extensions.tuple5.eq.eq
import arrow.core.extensions.tuple5.show.show
import arrow.core.extensions.tuple6.eq.eq
import arrow.core.extensions.tuple6.show.show
import arrow.core.extensions.tuple7.eq.eq
import arrow.core.extensions.tuple7.show.show
import arrow.core.extensions.tuple8.eq.eq
import arrow.core.extensions.tuple8.show.show
import arrow.core.extensions.tuple9.eq.eq
import arrow.core.extensions.tuple9.show.show
import arrow.core.extensions.tuple10.eq.eq
import arrow.core.extensions.tuple10.show.show
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class TupleTest : UnitSpec() {
  init {
            testLaws(
                    MonadLaws.laws(Tuple2.monad(Int.monoid()), Eq.any()),
                    ComonadLaws.laws(Tuple2.comonad(), { 0 toT it }, Eq.any()),
                    TraverseLaws.laws(Tuple2.traverse(), Tuple2.functor(), { 0 toT it }, Eq.any()),
                    BitraverseLaws.laws(Tuple2.bitraverse(), { Right(it) }, Eq.any()),
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
