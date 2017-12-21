package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import arrow.laws.EqLaws
import org.junit.runner.RunWith

sealed class Ops<out A> : HK<Ops.F, A> {

    class F private constructor()

    data class Value(val a: Int) : Ops<Int>()
    data class Add(val a: Int, val y: Int) : Ops<Int>()
    data class Subtract(val a: Int, val y: Int) : Ops<Int>()

    companion object : FreeMonadInstance<Ops.F> {
        fun value(n: Int): Free<Ops.F, Int> = Free.liftF(Ops.Value(n))
        fun add(n: Int, y: Int): Free<Ops.F, Int> = Free.liftF(Ops.Add(n, y))
        fun subtract(n: Int, y: Int): Free<Ops.F, Int> = Free.liftF(Ops.Subtract(n, y))
    }
}

fun <A> HK<Ops.F, A>.ev(): Ops<A> = this as Ops<A>

@RunWith(KTestJUnitRunner::class)
class FreeTest : UnitSpec() {

    private val program = Ops.binding {
        val added = Ops.add(10, 10).bind()
        val subtracted = bind { Ops.subtract(added, 50) }
        yields(subtracted)
    }.ev()

    private fun stackSafeTestProgram(n: Int, stopAt: Int): Free<Ops.F, Int> = Ops.binding {
        val v = Ops.add(n, 1).bind()
        val r = bind { if (v < stopAt) stackSafeTestProgram(v, stopAt) else Free.pure(v) }
        yields(r)
    }.ev()

    init {

        "instances can be resolved implicitly" {
            functor<FreeKindPartial<OpsAp.F>>() shouldNotBe null
            applicative<FreeKindPartial<OpsAp.F>>()  shouldNotBe null
            monad<FreeKindPartial<OpsAp.F>>()  shouldNotBe null
        }

        val EQ: FreeEq<Ops.F, IdHK, Int> = FreeEq(idInterpreter)
        testLaws(
            EqLaws.laws<Free<Ops.F, Int>>(EQ, { Ops.value(it) }),
            MonadLaws.laws(Ops, EQ)
        )

        "Can interpret an ADT as Free operations" {
            program.foldMap(optionInterpreter, Option.monad()).ev() shouldBe Some(-30)
            program.foldMap(idInterpreter, Id.monad()).ev() shouldBe Id(-30)
            program.foldMap(nonEmptyListInterpreter, NonEmptyList.monad()).ev() shouldBe NonEmptyList.of(-30)
        }

        "foldMap is stack safe" {
            val n = 50000
            val hugeProg = stackSafeTestProgram(0, n)
            hugeProg.foldMap(idInterpreter, Id.monad()).value() shouldBe n
        }

    }
}
