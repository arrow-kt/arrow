package arrow.free

import arrow.HK
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import arrow.free.Cofree.Companion.unfold
import arrow.core.*
import arrow.data.*
import arrow.free.instances.comonad
import arrow.instances.functor
import arrow.instances.monad
import arrow.instances.traverse
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.comonad
import arrow.typeclasses.functor

@RunWith(KTestJUnitRunner::class)
class CofreeTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            functor<CofreeKindPartial<OptionHK>>() shouldNotBe null
            comonad<CofreeKindPartial<OptionHK>>() shouldNotBe null
        }

        testLaws(ComonadLaws.laws(Cofree.comonad<OptionHK>(), {
            val sideEffect = SideEffect()
            unfold(sideEffect.counter, {
                sideEffect.increment()
                if (it % 2 == 0) None else Some(it + 1)
            })
        }, Eq { a, b ->
            a.ev().run().extract() == b.ev().run().extract()
        }))

        "tailForced should evaluate and return" {
            val sideEffect = SideEffect()
            val start: Cofree<IdHK, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it + 1) })
            sideEffect.counter shouldBe 0
            start.tailForced()
            sideEffect.counter shouldBe 1
        }

        "runTail should run once and return" {
            val sideEffect = SideEffect()
            val start: Cofree<IdHK, Int> = unfold(sideEffect.counter, { sideEffect.increment(); Id(it) })
            sideEffect.counter shouldBe 0
            start.runTail()
            sideEffect.counter shouldBe 1
        }

        "run should fold until completion" {
            val sideEffect = SideEffect()
            val start: Cofree<OptionHK, Int> = unfold(sideEffect.counter, {
                sideEffect.increment()
                if (it == 5) None else Some(it + 1)
            })
            sideEffect.counter shouldBe 0
            start.run()
            sideEffect.counter shouldBe 6
            start.extract() shouldBe 0
        }

        "run with an stack-unsafe monad should blow up the stack" {
            try {
                val limit = 10000
                val counter = SideEffect()
                val startThousands: Cofree<OptionHK, Int> = unfold(counter.counter, {
                    counter.increment()
                    if (it == limit) None else Some(it + 1)
                })
                startThousands.run()
                throw AssertionError("Run should overflow on a stack-unsafe monad")
            } catch (e: StackOverflowError) {
                // Expected. For stack safety use cataM instead
            }
        }

        "run with an stack-safe monad should not blow up the stack" {
            val counter = SideEffect()
            val startThousands: Cofree<EvalHK, Int> = unfold(counter.counter, {
                counter.increment()
                Eval.now(it + 1)
            })
            startThousands.run()
            counter.counter shouldBe 1
        }

        val startHundred: Cofree<OptionHK, Int> = unfold(0, { if (it == 100) None else Some(it + 1) }, Option.functor())

        "mapBranchingRoot should modify the value of the functor" {
            val mapped = startHundred.mapBranchingRoot(object : FunctionK<OptionHK, OptionHK> {
                override fun <A> invoke(fa: HK<OptionHK, A>): HK<OptionHK, A> =
                        None
            })
            val expected = NonEmptyList.of(0)
            cofreeOptionToNel(mapped) shouldBe expected
        }

        "mapBranchingS/T should recur over S and T respectively" {
            val mappedS = startHundred.mapBranchingS(optionToList, ListKW.functor())
            val mappedT = startHundred.mapBranchingT(optionToList, ListKW.functor())
            val expected = NonEmptyList.fromListUnsafe((0..100).toList())
            cofreeListToNel(mappedS) shouldBe expected
            cofreeListToNel(mappedT) shouldBe expected
        }

        "cata should traverse the structure" {
            val cata: NonEmptyList<Int> = startHundred.cata<OptionHK, Int, NonEmptyList<Int>>(
                    { i, lb -> Eval.now(NonEmptyList(i, lb.ev().fold({ emptyList<Int>() }, { it.all }))) },
                    Option.traverse()
            ).value()

            val expected = NonEmptyList.fromListUnsafe((0..100).toList())

            cata shouldBe expected
        }

        val startTwoThousand: Cofree<OptionHK, Int> = unfold(0, { if (it == 2000) None else Some(it + 1) }, Option.functor())

        "cata with an stack-unsafe monad should blow up the stack" {
            try {
                startTwoThousand.cata<OptionHK, Int, NonEmptyList<Int>>(
                        { i, lb -> Eval.now(NonEmptyList(i, lb.ev().fold({ emptyList<Int>() }, { it.all }))) },
                        Option.traverse()
                ).value()
                throw AssertionError("Run should overflow on a stack-unsafe monad")
            } catch (e: StackOverflowError) {
                // Expected. For stack safety use cataM instead
            }
        }

        "cataM should traverse the structure in a stack-safe way on a monad" {
            val folder: (Int, HK<OptionHK, NonEmptyList<Int>>) -> EvalOption<NonEmptyList<Int>> = { i, lb ->
                if (i <= 2000) OptionT.pure(NonEmptyList(i, lb.ev().fold({ emptyList<Int>() }, { it.all }))) else OptionT.none()
            }
            val inclusion = object : FunctionK<EvalHK, EvalOptionF> {
                override fun <A> invoke(fa: HK<EvalHK, A>): HK<EvalOptionF, A> =
                        OptionT(fa.ev().map { Some(it) })
            }
            val cataHundred = startTwoThousand.cataM(folder, inclusion, Option.traverse(), OptionT.monad(Eval.monad())).ev().value.ev().value()
            val newCof = Cofree(Option.functor(), 2001, Eval.now(Some(startTwoThousand)))
            val cataHundredOne = newCof.cataM(folder, inclusion, Option.traverse(), OptionT.monad(Eval.monad())).ev().value.ev().value()

            cataHundred shouldBe Some(NonEmptyList.fromListUnsafe((0..2000).toList()))
            cataHundredOne shouldBe None
        }

    }
}

typealias EvalOption<A> = OptionTKind<EvalHK, A>

typealias EvalOptionF = OptionTKindPartial<EvalHK>