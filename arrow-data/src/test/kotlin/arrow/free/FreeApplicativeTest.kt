package arrow

import arrow.core.*
import arrow.data.NonEmptyList
import arrow.free.FreeApplicative
import arrow.free.foldK
import arrow.free.instances.FreeApplicativeApplicativeInstance
import arrow.free.instances.FreeApplicativeEq
import arrow.instances.FreeApplicativeApplicativeInstance
import arrow.instances.FreeApplicativeEq
import arrow.instances.applicative
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws

sealed class OpsAp<out A> : HK<OpsAp.F, A> {

    class F private constructor()

    data class Value(val a: Int) : OpsAp<Int>()
    data class Add(val a: Int, val y: Int) : OpsAp<Int>()
    data class Subtract(val a: Int, val y: Int) : OpsAp<Int>()

    companion object : FreeApplicativeApplicativeInstance<F> {
        fun value(n: Int): FreeApplicative<F, Int> = FreeApplicative.liftF(Value(n))
        fun add(n: Int, y: Int): FreeApplicative<F, Int> = FreeApplicative.liftF(Add(n, y))
        fun subtract(n: Int, y: Int): FreeApplicative<F, Int> = FreeApplicative.liftF(Subtract(n, y))
    }
}

fun <A> HK<OpsAp.F, A>.ev(): OpsAp<A> = this as OpsAp<A>

@RunWith(KTestJUnitRunner::class)
class FreeApplicativeTest : UnitSpec() {

    private val program = OpsAp.tupled(OpsAp.value(1), OpsAp.add(3, 4), OpsAp.subtract(3, 4)).ev()

    init {

        "instances can be resolved implicitly" {
            functor<FreeApplicativeKindPartial<OpsAp.F>>() shouldNotBe null
            applicative<FreeApplicativeKindPartial<OpsAp.F>>()  shouldNotBe null
        }

        val EQ: FreeApplicativeEq<OpsAp.F, IdHK, Int> = FreeApplicativeEq(idApInterpreter)
        testLaws(
            EqLaws.laws<FreeApplicative<OpsAp.F, Int>>(EQ, { OpsAp.value(it) }),
            ApplicativeLaws.laws(OpsAp, EQ)
        )

        "Can interpret an ADT as FreeApplicative operations" {
            val result: Tuple3<Int, Int, Int> = (1 toT 7) + -1
            program.foldMap(optionApInterpreter, Option.applicative()).ev() shouldBe Some(result)
            program.foldMap(idApInterpreter, Id.applicative()).ev() shouldBe Id(result)
            program.foldMap(nonEmptyListApInterpreter, NonEmptyList.applicative()).ev() shouldBe NonEmptyList.of(result)
        }

        "fold is stack safe" {
            val loops = 10000
            val start = 333
            val r = FreeApplicative.liftF(NonEmptyList.of(start))
            val rr = (1..loops).toList().fold(r, { v, _ -> v.ap(FreeApplicative.liftF(NonEmptyList.of({ a: Int -> a + 1 }))) })
            rr.foldK() shouldBe NonEmptyList.of(start + loops)
            val rx = (1..loops).toList().foldRight(r, { _, v -> v.ap(FreeApplicative.liftF(NonEmptyList.of({ a: Int -> a + 1 }))) })
            rx.foldK() shouldBe NonEmptyList.of(start + loops)
        }
    }
}
