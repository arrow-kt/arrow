package arrow.free

import arrow.Kind
import arrow.core.ForId
import arrow.core.FunctionK
import arrow.core.Id
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.id.foldable.foldable
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.id.traverse.traverse
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.extensions.option.monad.monad
import arrow.core.value
import arrow.free.extensions.FreeEq
import arrow.free.extensions.FreeMonad
import arrow.free.extensions.free.eq.eq
import arrow.free.extensions.free.foldable.foldable
import arrow.free.extensions.free.functor.functor
import arrow.free.extensions.free.monad.monad
import arrow.free.extensions.free.traverse.traverse
import arrow.higherkind
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

@higherkind
sealed class Ops<out A> : OpsOf<A> {

  data class Value(val a: Int) : Ops<Int>()
  data class Add(val a: Int, val y: Int) : Ops<Int>()
  data class Subtract(val a: Int, val y: Int) : Ops<Int>()

  companion object : FreeMonad<ForOps> {
    fun value(n: Int): Free<ForOps, Int> = Free.liftF(Value(n))
    fun add(n: Int, y: Int): Free<ForOps, Int> = Free.liftF(Add(n, y))
    fun subtract(n: Int, y: Int): Free<ForOps, Int> = Free.liftF(Subtract(n, y))
  }
}

class FreeTest : UnitSpec() {

  private val program = Ops.fx.monad {
    val (added) = Ops.add(10, 10)
    val subtracted = !Ops.subtract(added, 50)
    subtracted
  }.fix()

  private fun stackSafeTestProgram(n: Int, stopAt: Int): Free<ForOps, Int> = Ops.fx.monad {
    val (v) = Ops.add(n, 1)
    val r = !if (v < stopAt) stackSafeTestProgram(v, stopAt) else Free.just(v)
    r
  }.fix()

  init {

    val IdMonad = Id.monad()

    val EQ: FreeEq<ForOps, ForId, Int> = Free.eq(IdMonad, idInterpreter)

    val cf: (Int) -> Free<ForId, Int> = { it.free() }

    val EQ1 = Free.eq<ForId, ForId, Int>(Id.monad(), FunctionK.id())

    val G = Gen.int().map(cf) as Gen<Kind<FreePartialOf<ForId>, Int>>

    val EQK = object : EqK<FreePartialOf<ForOps>> {
      override fun <A> Kind<FreePartialOf<ForOps>, A>.eqK(other: Kind<FreePartialOf<ForOps>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let {
          Free.eq<ForOps, ForId, A>(IdMonad, idInterpreter).run {
            it.first.eqv(it.second)
          }
        }
    }

    testLaws(
      EqLaws.laws(EQ, Gen.ops(Gen.int())),
      MonadLaws.laws(Ops, EQK),
      MonadLaws.laws(Free.monad(), EQK),
      FoldableLaws.laws(Free.foldable(Id.foldable()), G),
      TraverseLaws.laws(Free.traverse(Id.traverse()), Free.functor(), G, EQ1)
    )

    "Can interpret an ADT as Free operations to Option" {
      program.foldMap(optionInterpreter, Option.monad()) shouldBe Some(-30)
    }

    "Can interpret an ADT as Free operations to Id" {
      program.foldMap(idInterpreter, IdMonad) shouldBe Id(-30)
    }

    "Can interpret an ADT as Free operations to NonEmptyList" {
      program.foldMap(nonEmptyListInterpreter, NonEmptyList.monad()) shouldBe NonEmptyList.of(-30)
    }

    "foldMap is stack safe" {
      val n = 50000
      val hugeProg = stackSafeTestProgram(0, n)
      hugeProg.foldMap(idInterpreter, IdMonad).value() shouldBe n
    }
  }
}

private fun Gen.Companion.ops(gen: Gen<Int>) =
  gen.map { Ops.value(it) }
