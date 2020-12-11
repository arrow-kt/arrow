package arrow.core.test.laws

import arrow.continuations.Effect
import arrow.core.test.generators.throwable
import arrow.typeclasses.Eq
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

private typealias EagerFxBlock<Eff, F, A> = (suspend Eff.() -> A) -> F
private typealias SuspendFxBlock<Eff, F, A> = suspend (suspend Eff.() -> A) -> F

object FxLaws {

  fun <Eff : Effect<*>, F, A> suspended(
    pureGen: Gen<F>,
    G: Gen<F>,
    EQ: Eq<F>,
    fxSuspend: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ): List<Law> = listOf(
    Law("suspended fx can bind immediate values") { suspendedCanBindImmediateValues(G, EQ, fxSuspend, invoke) },
    Law("suspended fx can bind suspended values") { suspendedCanBindSuspendedValues(G, EQ, fxSuspend, invoke) },
    Law("suspended fx can bind immediate exceptions") { suspendedCanBindImmediateExceptions(pureGen, fxSuspend, invoke) },
    Law("suspended fx can bind suspended exceptions") { suspendedCanBindSuspendedExceptions(pureGen, fxSuspend, invoke) }
  )

  fun <Eff : Effect<*>, F, A> eager(
    pureGen: Gen<F>,
    G: Gen<F>,
    EQ: Eq<F>,
    fxEager: EagerFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ): List<Law> = listOf(
    Law("non-suspended fx can bind immediate values") { nonSuspendedCanBindImmediateValues(G, EQ, fxEager, invoke) },
    Law("non-suspended fx can bind immediate exceptions") { nonSuspendedCanBindImmediateException(pureGen, fxEager, invoke) }
  )

  private suspend fun <Eff : Effect<*>, F, A> nonSuspendedCanBindImmediateValues(
    G: Gen<F>,
    EQ: Eq<F>,
    fxBlock: EagerFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    forAll(G) { f: F ->
      fxBlock {
        val res = invoke(f)
        res
      }.equalUnderTheLaw(f, EQ)
    }
  }

  private fun <Eff : Effect<*>, F, A> nonSuspendedCanBindImmediateException(
    G: Gen<F>,
    fxBlock: EagerFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    forAll(G, Gen.throwable()) { f, exception ->
      shouldThrow<Throwable> {
        fxBlock {
          val res = invoke(f)
          throw exception
          res
        }

        fail("It should never reach here. fx should've thrown $exception")
      } == exception
    }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindImmediateValues(
    G: Gen<F>,
    EQ: Eq<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    G.random()
      .take(1001)
      .forEach { f ->
        fxBlock {
          val res = invoke(f)
          res
        }.equalUnderTheLaw(f, EQ)
      }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindSuspendedValues(
    G: Gen<F>,
    EQ: Eq<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    G.random()
      .take(10)
      .forEach { f ->
        fxBlock {
          val res = invoke(f.suspend())
          res
        }.equalUnderTheLaw(f, EQ)
      }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindImmediateExceptions(
    G: Gen<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    Gen.bind(G, Gen.throwable(), ::Pair)
      .random()
      .take(1001)
      .forEach { (f, exception) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = invoke(f)
            throw exception
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindSuspendedExceptions(
    G: Gen<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    Gen.bind(G, Gen.throwable(), ::Pair)
      .random()
      .take(10)
      .forEach { (f, exception) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = invoke(f)
            exception.suspend()
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }
}

// TODO expose for tests
internal suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(Continuation(Dispatchers.Default) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }

internal suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(Dispatchers.Default) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }
