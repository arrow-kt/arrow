package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import katz.Cofree.Companion.unfold
import katz.ListT.ListF
import katz.Option.None
import katz.Option.Some
import katz.free.cofreeListToNel
import katz.free.cofreeOptionToNel
import katz.free.optionToList
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger


// Unsafe functor for this test only
class ListT<out A>(val all: List<A>) : HK<ListF, A> {
    class ListF private constructor()

    companion object : Functor<ListF> {
        override fun <A, B> map(fa: HK<ListF, A>, f: (A) -> B): HK<ListF, B> =
                ListT((fa as ListT<A>).all.map(f))

    }
}

fun <A> HK<CoAdder.F, A>.ev() = this as CoAdder<A>

abstract class CoAdder<out A> : HK<CoAdder.F, A> {
    class F private constructor()

    abstract fun add(add: Int): Tuple2<Boolean, A>
    abstract fun clear(): A
    abstract fun total(): Tuple2<Int, A>

    companion object : Functor<F> {
        override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): CoAdder<B> =
                object : CoAdder<B>() {
                    override fun add(add: Int): Tuple2<Boolean, B> =
                            fa.ev().add(add).let { (acc, value) ->
                                acc toT f(value)
                            }

                    override fun clear(): B =
                            f(fa.ev().clear())

                    override fun total(): Tuple2<Int, B> =
                            fa.ev().total().let { (acc, value) ->
                                acc toT f(value)
                            }
                }
    }
}

typealias Limit = Int

typealias Count = Int

data class CoAdderInterpreter(val limit: Limit, val count: Count) : CoAdder<Tuple2<Limit, Count>>() {
    override fun add(add: Int): Tuple2<Boolean, Tuple2<Limit, Count>> =
            (count + add).let { next ->
                (next <= limit) toT (limit toT next)
            }

    override fun clear(): Tuple2<Limit, Count> =
            limit toT 0

    override fun total(): Tuple2<Int, Tuple2<Limit, Count>> =
            count toT (limit toT count)
}

@RunWith(KTestJUnitRunner::class)
class CofreeTest : UnitSpec() {

    init {
        "tailForced should evaluate and return" {
            val sideEffect = SideEffect()
            val start: Cofree<Id.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it) }, Id)
            sideEffect.counter shouldBe 0
            start.tailForced()
            sideEffect.counter shouldBe 1
        }

        "runTail should run once and return" {
            val sideEffect = SideEffect()
            val start: Cofree<Id.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it) }, Id)
            sideEffect.counter shouldBe 0
            start.runTail()
            sideEffect.counter shouldBe 1
        }

        "run should fold until completion" {
            val sideEffect = SideEffect()
            val start: Cofree<Option.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); if (sideEffect.counter == 5) None else Some(it) }, Option)
            sideEffect.counter shouldBe 0
            start.run()
            sideEffect.counter shouldBe 5
        }

        val startThousands: Cofree<Option.F, Int> = unfold(0, { if (it == 10000) None else Some(it + 1) }, Option)

        "run should not blow up the stack" {
            startThousands.run()
            startThousands.extract() shouldBe 10000
        }

        val startHundred: Cofree<Option.F, Int> = unfold(0, { if (it == 100) None else Some(it + 1) }, Option)

        "mapBranchingRoot should modify the value of the functor" {
            val mapped = startHundred.mapBranchingRoot(object : FunctionK<Option.F, Option.F> {
                override fun <A> invoke(fa: HK<Option.F, A>): HK<Option.F, A> =
                        None
            })
            val expected = NonEmptyList.of(0)
            cofreeOptionToNel(mapped) shouldBe expected
        }

        "mapBranchingS/T should recur over S and T respectively" {
            val mappedS = startHundred.mapBranchingS(optionToList, ListT)
            val mappedT = startHundred.mapBranchingT(optionToList, ListT)
            val expected = NonEmptyList.fromListUnsafe((0..100).toList())
            cofreeListToNel(mappedS) shouldBe expected
            cofreeListToNel(mappedT) shouldBe expected
        }

        "cofree should cobind correctly without blowing up the stack" {
            val limit: Int = 10000
            fun stackSafeProgram(current: Int, loops: AtomicInteger): Tuple2<Int, Int> = CofreeComonad<CoAdder.F>().cobinding {
                val value = !unfold((limit toT current), { stack ->
                    loops.incrementAndGet()
                    val (a, b) = if (current == 1) stack else stackSafeProgram(current - 1, loops)
                    CoAdderInterpreter(a, b)
                }, CoAdder).run()
                yields(value)
            }

            val loops = AtomicInteger()
            val (count, total) = stackSafeProgram(limit, loops)
            count shouldBe limit
            loops.toInt() shouldBe limit
        }
    }
}