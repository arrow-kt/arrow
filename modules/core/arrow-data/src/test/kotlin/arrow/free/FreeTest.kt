package arrow.free

import arrow.Kind
import arrow.core.*
import arrow.data.NonEmptyList
import arrow.data.fix
import arrow.data.monad
import arrow.free.instances.FreeEq
import arrow.free.instances.FreeMonadInstance
import arrow.free.instances.eq
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadLaws
import arrow.typeclasses.binding
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

sealed class Ops<out A> : Kind<Ops.F, A> {

  class F private constructor()

  data class Value(val a: Int) : Ops<Int>()
  data class Add(val a: Int, val y: Int) : Ops<Int>()
  data class Subtract(val a: Int, val y: Int) : Ops<Int>()

  companion object : FreeMonadInstance<F> {
    fun value(n: Int): Free<F, Int> = Free.liftF(Value(n))
    fun add(n: Int, y: Int): Free<F, Int> = Free.liftF(Add(n, y))
    fun subtract(n: Int, y: Int): Free<F, Int> = Free.liftF(Subtract(n, y))
  }
}

fun <A> Kind<Ops.F, A>.fix(): Ops<A> = this as Ops<A>

@RunWith(KTestJUnitRunner::class)
class FreeTest : UnitSpec() {

  private val program = Ops.binding {
    val added = Ops.add(10, 10).bind()
    val subtracted = bind { Ops.subtract(added, 50) }
    subtracted
  }.fix()

  private fun stackSafeTestProgram(n: Int, stopAt: Int): Free<Ops.F, Int> = Ops.binding {
    val v = Ops.add(n, 1).bind()
    val r = bind { if (v < stopAt) stackSafeTestProgram(v, stopAt) else Free.just(v) }
    r
  }.fix()

  init {

    val IdMonad = Id.monad()

    val EQ: FreeEq<Ops.F, ForId, Int> = Free.eq(idInterpreter, IdMonad)
    testLaws(
      EqLaws.laws<Free<Ops.F, Int>>(EQ, { Ops.value(it) }),
      MonadLaws.laws(Ops, EQ)
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
