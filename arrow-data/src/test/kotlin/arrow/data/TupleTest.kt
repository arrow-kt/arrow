package arrow.data

import arrow.core.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.ShowLaws
import arrow.typeclasses.eq
import arrow.typeclasses.monoid
import arrow.typeclasses.show

@RunWith(KTestJUnitRunner::class)
class TupleTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            monoid<Tuple2<Int, Int>>() shouldNotBe null
            
            eq<Tuple2<Int, Int>>() shouldNotBe null
            show<Tuple2<Int, Int>>() shouldNotBe null
            eq<Tuple3<Int, Int, Int>>() shouldNotBe null
            show<Tuple3<Int, Int, Int>>() shouldNotBe null
            eq<Tuple4<Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple4<Int, Int, Int, Int>>() shouldNotBe null
            eq<Tuple5<Int, Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple5<Int, Int, Int, Int, Int>>() shouldNotBe null
            eq<Tuple6<Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple6<Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            eq<Tuple7<Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple7<Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            eq<Tuple8<Int, Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple8<Int, Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            eq<Tuple9<Int, Int, Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple9<Int, Int, Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            eq<Tuple10<Int, Int, Int, Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
            show<Tuple10<Int, Int, Int, Int, Int, Int, Int, Int, Int, Int>>() shouldNotBe null
        }

        testLaws(
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
        )
    }

}
