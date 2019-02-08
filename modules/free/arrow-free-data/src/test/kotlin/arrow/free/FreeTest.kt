package arrow.free

import arrow.core.*
import arrow.data.NonEmptyList
import arrow.data.fix
import arrow.free.extensions.FreeEq
import arrow.free.extensions.FreeMonad
import arrow.free.extensions.free.eq.eq
import arrow.free.extensions.free.monad.monad
import arrow.higherkind
import arrow.core.extensions.id.monad.monad
import arrow.data.extensions.nonemptylist.monad.monad
import arrow.core.extensions.option.monad.monad
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

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

@RunWith(KotlinTestRunner::class)
class FreeTest : UnitSpec() {

  private val program = Ops.fx {
    val (added) = Ops.add(10, 10)
    val subtracted = bind { Ops.subtract(added, 50) }
    subtracted
  }.fix()

  private fun stackSafeTestProgram(n: Int, stopAt: Int): Free<ForOps, Int> = Ops.fx {
    val (v) = Ops.add(n, 1)
    val r = bind { if (v < stopAt) stackSafeTestProgram(v, stopAt) else Free.just(v) }
    r
  }.fix()

  init {

    val IdMonad = Id.monad()

    val EQ: FreeEq<ForOps, ForId, Int> = Free.eq(IdMonad, idInterpreter)

    testLaws(
      EqLaws.laws(EQ) { Ops.value(it) },
      MonadLaws.laws(Ops, EQ),
      MonadLaws.laws(Free.monad(), EQ)
    )

    "Can interpret an ADT as Free operations" {
      program.foldMap(optionInterpreter, Option.monad()).fix() shouldBe Some(-30)
      program.foldMap(idInterpreter, IdMonad).fix() shouldBe Id(-30)
      program.foldMap(nonEmptyListInterpreter, NonEmptyList.monad()).fix() shouldBe NonEmptyList.of(-30)
    }

    "foldMap is stack safe" {
      val n = 50000
      val hugeProg = stackSafeTestProgram(0, n)
      hugeProg.foldMap(idInterpreter, IdMonad).value() shouldBe n
    }

  }
}
