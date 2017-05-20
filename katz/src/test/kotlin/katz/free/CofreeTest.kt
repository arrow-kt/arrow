package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import katz.Cofree.Companion.unfold
import katz.ListT.ListF
import katz.Option.None
import katz.Option.Some
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
            val start: Cofree<Id.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it + 1) })
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
            val start: Cofree<Option.F, Int> = unfold(sideEffect.counter, { sideEffect.increment(); if (it == 5) None else Some(it + 1) }, Option)
            sideEffect.counter shouldBe 0
            start.run()
            sideEffect.counter shouldBe 6
        }

        "run should not blow up the stack" {
            val limit = 10000
            val counter = SideEffect()
            val startThousands: Cofree<Option.F, Int> = unfold(0, {
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
            val offset = 5
            val limit = offset
            fun stackSafeProgram(loops: SideEffect): Either<Int, CofreeKind<EitherF<Int>, Int>> = CofreeComonad<EitherF<Int>>().cobinding {
                val value = unfold(offset, { current ->
                    loops.increment()
                    if (current == limit)
                        Either.Left(current)
                    else
                        Either.Right(current + 1)
                }, EitherMonad<Int>()).tailForced().ev()
                yields(value)
            }

            val loops = SideEffect()
            val count = stackSafeProgram(loops)
            count shouldBe Either.Left(offset)
            loops.counter shouldBe limit
        }
    }
}