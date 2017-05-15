package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

sealed class Ops<out A> : HK<Ops.F, A> {

    class F private constructor()

    data class Value(val a: Int) : Ops<Int>()
    data class Add(val a: Int, val y: Int) : Ops<Int>()
    data class Subtract(val a: Int, val y: Int) : Ops<Int>()

    companion object : FreeMonad<Ops.F> {
        fun value(n: Int): Free<Ops.F, Int> = Free.liftF(Ops.Value(n))
        fun add(n: Int, y: Int): Free<Ops.F, Int> = Free.liftF(Ops.Add(n, y))
        fun subtract(n: Int, y: Int): Free<Ops.F, Int> = Free.liftF(Ops.Subtract(n, y))
    }
}

fun <A> HK<Ops.F, A>.ev(): Ops<A> = this as Ops<A>

val optionInterpreter: FunctionK<Ops.F, Option.F> = object : FunctionK<Ops.F, Option.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Option.Some(op.a + op.y)
            is Ops.Subtract -> Option.Some(op.a - op.y)
            is Ops.Value -> Option.Some(op.a)
        } as Option<A>
    }
}

val nonEmptyListInterpter: FunctionK<Ops.F, NonEmptyList.F> = object : FunctionK<Ops.F, NonEmptyList.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): NonEmptyList<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> NonEmptyList.of(op.a + op.y)
            is Ops.Subtract -> NonEmptyList.of(op.a - op.y)
            is Ops.Value -> NonEmptyList.of(op.a)
        } as NonEmptyList<A>
    }
}

val idInterpreter: FunctionK<Ops.F, Id.F> = object : FunctionK<Ops.F, Id.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Id<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Id(op.a + op.y)
            is Ops.Subtract -> Id(op.a - op.y)
            is Ops.Value -> Id(op.a)
        } as Id<A>
    }
}

@RunWith(KTestJUnitRunner::class)
class FreeTest : UnitSpec() {

    val program = Ops.binding {
        val added = !Ops.add(10, 10)
        val substracted = !Ops.subtract(added, 50)
        yields(substracted)
    }.ev()

    fun stackSafeTestProgram(n: Int, stopAt: Int): Free<Ops.F, Int> = Ops.binding {
        val v = !Ops.add(n, 1)
        val r = !if (v < stopAt) stackSafeTestProgram(v, stopAt) else Free.pure<Ops.F, Int>(v)
        yields(r)
    }.ev()

    init {

        "Can interpret an ADT as Free operations" {
            program.foldMap(optionInterpreter, Option).ev() shouldBe Option.Some(-30)
            program.foldMap(idInterpreter, Id).ev() shouldBe Id(-30)
            program.foldMap(nonEmptyListInterpter, NonEmptyList).ev() shouldBe NonEmptyList.of(-30)
        }

        "foldMap is stack safe" {
            val n = 50000
            val hugeProg = stackSafeTestProgram(0, n)
            hugeProg.foldMap(idInterpreter, Id).value() shouldBe n
        }

    }
}
