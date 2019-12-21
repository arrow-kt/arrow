package arrow.free

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.ForId
import arrow.core.ForOption
import arrow.core.FunctionK
import arrow.core.Id
import arrow.core.ListK
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.extensions.eval.functor.functor
import arrow.core.extensions.eval.monad.monad
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.traverse.traverse
import arrow.core.fix
import arrow.core.value
import arrow.free.Cofree.Companion.unfold
import arrow.free.extensions.cofree.comonad.comonad
import arrow.mtl.OptionT
import arrow.mtl.OptionTOf
import arrow.mtl.OptionTPartialOf
import arrow.mtl.extensions.optiont.monad.monad
import arrow.mtl.value
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.generators.GenK
import arrow.test.generators.nonEmptyList
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class CofreeTest : UnitSpec() {

  init {

    val genk = object : GenK<CofreePartialOf<ForOption>> {
      private fun <A> NonEmptyList<A>.toCofree(): Cofree<ForOption, A> =
        Cofree(Option.functor(), this.head, Eval.later { Nel.fromList(this.tail).map { it.toCofree() } })

      override fun <A> genK(gen: Gen<A>): Gen<Kind<CofreePartialOf<ForOption>, A>> =
        Gen.nonEmptyList(gen).map {
          it.toCofree()
        }
    }

    val eqk = object : EqK<CofreePartialOf<ForOption>> {
      override fun <A> Kind<CofreePartialOf<ForOption>, A>.eqK(other: Kind<CofreePartialOf<ForOption>, A>, EQ: Eq<A>): Boolean {
        return this.fix().run() == other.fix().run().fix()
      }
    }

    testLaws(ComonadLaws.laws(Cofree.comonad(), genk, eqk))

    "tailForced should evaluate and return" {
      val sideEffect = SideEffect()
      val start: Cofree<ForId, Int> = unfold(Id.functor(), sideEffect.counter) { sideEffect.increment(); Id(it + 1) }
      sideEffect.counter shouldBe 0
      start.tailForced()
      sideEffect.counter shouldBe 1
    }

    "runTail should run once and return" {
      val sideEffect = SideEffect()
      val start: Cofree<ForId, Int> = unfold(Id.functor(), sideEffect.counter) { sideEffect.increment(); Id(it) }
      sideEffect.counter shouldBe 0
      start.runTail()
      sideEffect.counter shouldBe 1
    }

    "run should fold until completion" {
      val sideEffect = SideEffect()
      val start: Cofree<ForOption, Int> = unfold(Option.functor(), sideEffect.counter) {
        sideEffect.increment()
        if (it == 5) None else Some(it + 1)
      }
      sideEffect.counter shouldBe 0
      start.run()
      sideEffect.counter shouldBe 6
      start.extract() shouldBe 0
    }

    "run with an stack-unsafe monad should blow up the stack" {
      try {
        val limit = 10000
        val counter = SideEffect()
        val startThousands: Cofree<ForOption, Int> = unfold(Option.functor(), counter.counter) {
          counter.increment()
          if (it == limit) None else Some(it + 1)
        }
        startThousands.run()
        throw AssertionError("Run should overflow on a stack-unsafe monad")
      } catch (e: StackOverflowError) {
        // Expected. For stack safety use cataM instead
      }
    }

    "run with an stack-safe monad should not blow up the stack" {
      val counter = SideEffect()
      val startThousands: Cofree<ForEval, Int> = unfold(Eval.functor(), counter.counter) {
        counter.increment()
        Eval.now(it + 1)
      }
      startThousands.run()
      counter.counter shouldBe 1
    }

    val startHundred: Cofree<ForOption, Int> = unfold(Option.functor(), 0) { if (it == 100) None else Some(it + 1) }

    "mapBranchingRoot should modify the value of the functor" {
      val mapped = startHundred.mapBranchingRoot(object : FunctionK<ForOption, ForOption> {
        override fun <A> invoke(fa: Kind<ForOption, A>): Kind<ForOption, A> =
          None
      })
      val expected = NonEmptyList.of(0)
      cofreeOptionToNel(mapped) shouldBe expected
    }

    "mapBranchingS/T should recur over S and T respectively" {
      val mappedS = startHundred.mapBranchingS(optionToList, ListK.functor())
      val mappedT = startHundred.mapBranchingT(optionToList, ListK.functor())
      val expected = NonEmptyList.fromListUnsafe((0..100).toList())
      cofreeListToNel(mappedS) shouldBe expected
      cofreeListToNel(mappedT) shouldBe expected
    }

    "cata should traverse the structure" {
      val cata: NonEmptyList<Int> = startHundred.cata<NonEmptyList<Int>>(
        { i, lb -> Eval.now(NonEmptyList(i, lb.fix().fold({ emptyList<Int>() }, { it.all }))) },
        Option.traverse()
      ).value()

      val expected = NonEmptyList.fromListUnsafe((0..100).toList())

      cata shouldBe expected
    }

    val startTwoThousand: Cofree<ForOption, Int> = unfold(Option.functor(), 0) { if (it == 2000) None else Some(it + 1) }

    with(startTwoThousand) {
      "cata with an stack-unsafe monad should blow up the stack" {
        try {
          cata<NonEmptyList<Int>>(
            { i, lb -> Eval.now(NonEmptyList(i, lb.fix().fold({ emptyList<Int>() }, { it.all }))) },
            Option.traverse()
          ).value()
          throw AssertionError("Run should overflow on a stack-unsafe monad")
        } catch (e: StackOverflowError) {
          // Expected. For stack safety use cataM instead
        }
      }

      "cataM should traverse the structure in a stack-safe way on a monad" {
        val folder: (Int, Kind<ForOption, NonEmptyList<Int>>) -> EvalOption<NonEmptyList<Int>> = { i, lb ->
          if (i <= 2000)
            OptionT.just(Eval.applicative(), NonEmptyList(i, lb.fix().fold({ emptyList<Int>() }, { it.all })))
          else
            OptionT.none(Eval.applicative())
        }
        val inclusion = object : FunctionK<ForEval, EvalOptionF> {
          override fun <A> invoke(fa: Kind<ForEval, A>): Kind<EvalOptionF, A> =
            OptionT(fa.fix().map { Some(it) })
        }
        val cataHundred = cataM(OptionT.monad(Eval.monad()), Option.traverse(), inclusion, folder).value().value()
        val newCof = Cofree(Option.functor(), 2001, Eval.now(Some(startTwoThousand)))
        val cataHundredOne = newCof.cataM(OptionT.monad(Eval.monad()), Option.traverse(), inclusion, folder).value().value()

        cataHundred shouldBe Some(NonEmptyList.fromListUnsafe((0..2000).toList()))
        cataHundredOne shouldBe None
      }
    }
  }
}

typealias EvalOption<A> = OptionTOf<ForEval, A>

typealias EvalOptionF = OptionTPartialOf<ForEval>
