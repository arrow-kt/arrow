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


// Unsafe functor for this test only
class ListT<out A>(val all: List<A>) : HK<ListF, A> {
    class ListF private constructor()

    companion object : Functor<ListF> {
        override fun <A, B> map(fa: HK<ListF, A>, f: (A) -> B): HK<ListF, B> =
                ListT((fa as ListT<A>).all.map(f))

    }
}

@RunWith(KTestJUnitRunner::class)
class CofreeTest : UnitSpec() {

    init {
        "tailForced should evaluate and return" {
            val sideEffect = SideEffect()
            val start: Cofree<Id.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it) })
            sideEffect.counter shouldBe 0
            start.tailForced()
            sideEffect.counter shouldBe 1
        }

        "runTail should run once and return" {
            val sideEffect = SideEffect()
            val start: Cofree<Id.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it) })
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

        "run should not blow up the stack" {
            val limit = 10000
            val counter = SideEffect()
            val startThousands: Cofree<Option.F, Int> = unfold(0, {
                counter.increment()
                if (it == limit)
                    None
                else
                    Some(it + 1)
            }, Option)
            startThousands.run()
            counter.counter shouldBe limit
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
            val limit: Int = 10
            fun stackSafeProgram(current: Int, loops: SideEffect): Int = CofreeComonad<Id.F>().cobinding {
                val value = !unfold(current, { _ ->
                    loops.increment()
                    val a = if (current == limit) current else stackSafeProgram(current - 1, loops)
                    Id(a)
                }).run()
                yields(value)
            }

            val loops = SideEffect()
            val count = stackSafeProgram(limit, loops)
            count shouldBe limit
            loops.counter shouldBe limit
        }
    }
}