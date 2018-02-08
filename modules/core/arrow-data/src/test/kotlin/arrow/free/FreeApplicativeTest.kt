package arrow.free

import arrow.Kind
import arrow.core.*
import arrow.data.NonEmptyList
import arrow.data.applicative
import arrow.extract
import arrow.free.instances.FreeApplicativeApplicativeInstance
import arrow.free.instances.FreeApplicativeEq
import arrow.syntax.applicative.tupled
import arrow.syntax.tuples.plus
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.typeclasses.applicative
import arrow.typeclasses.functor
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

sealed class OpsAp<out A> : Kind<OpsAp.F, A> {

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

fun <A> Kind<OpsAp.F, A>.extract(): OpsAp<A> = this as OpsAp<A>

@RunWith(KTestJUnitRunner::class)
class FreeApplicativeTest : UnitSpec() {

    private val program = OpsAp.tupled(OpsAp.value(1), OpsAp.add(3, 4), OpsAp.subtract(3, 4)).extract()

    init {

        "instances can be resolved implicitly" {
            functor<FreeApplicativePartialOf<OpsAp.F>>() shouldNotBe null
            applicative<FreeApplicativePartialOf<OpsAp.F>>()  shouldNotBe null
        }

        val EQ: FreeApplicativeEq<OpsAp.F, ForId, Int> = FreeApplicativeEq(idApInterpreter)
        testLaws(
            EqLaws.laws<FreeApplicative<OpsAp.F, Int>>(EQ, { OpsAp.value(it) }),
            ApplicativeLaws.laws(OpsAp, EQ)
        )

        "Can interpret an ADT as FreeApplicative operations" {
            val result: Tuple3<Int, Int, Int> = (1 toT 7) + -1
            program.foldMap(optionApInterpreter, Option.applicative()).extract() shouldBe Some(result)
            program.foldMap(idApInterpreter, Id.applicative()).extract() shouldBe Id(result)
            program.foldMap(nonEmptyListApInterpreter, NonEmptyList.applicative()).extract() shouldBe NonEmptyList.of(result)
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
