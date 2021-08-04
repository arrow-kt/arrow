package arrow.core.test.laws

import arrow.continuations.Effect
import arrow.core.Either
import arrow.core.test.generators.throwable
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

private typealias EagerFxBlock<Eff, F, A> = (suspend Eff.() -> A) -> F
private typealias SuspendFxBlock<Eff, F, A> = suspend (suspend Eff.() -> A) -> F

public object FxLaws {

  public fun <Eff : Effect<*>, F, A> suspended(
    pureArb: Arb<F>,
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxSuspend: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ): List<Law> = listOf(
    Law("suspended fx can bind immediate values") { suspendedCanBindImmediateValues(G, eq, fxSuspend, invoke) },
    Law("suspended fx can bind suspended values") { suspendedCanBindSuspendedValues(G, eq, fxSuspend, invoke) },
    Law("suspended fx can bind immediate exceptions") {
      suspendedCanBindImmediateExceptions(
        pureArb,
        fxSuspend,
        invoke
      )
    },
    Law("suspended fx can bind suspended exceptions") {
      suspendedCanBindSuspendedExceptions(
        pureArb,
        fxSuspend,
        invoke
      )
    }
  )

  public fun <Eff : Effect<*>, F, A> eager(
    pureArb: Arb<F>,
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxEager: EagerFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ): List<Law> = listOf(
    Law("non-suspended fx can bind immediate values") { nonSuspendedCanBindImmediateValues(G, eq, fxEager, invoke) },
    Law("non-suspended fx can bind immediate exceptions") {
      nonSuspendedCanBindImmediateException(
        pureArb,
        fxEager,
        invoke
      )
    }
  )

  private suspend fun <Eff : Effect<*>, F, A> nonSuspendedCanBindImmediateValues(
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxBlock: EagerFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    checkAll(G) { f: F ->
      fxBlock {
        val res = invoke(f)
        res
      }.equalUnderTheLaw(f, eq)
    }
  }

  private suspend fun <Eff : Effect<*>, F, A> nonSuspendedCanBindImmediateException(
    G: Arb<F>,
    fxBlock: EagerFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    checkAll(G, Arb.throwable()) { f, exception ->
      Either.catch {
        fxBlock {
          val res = invoke(f)
          throw exception
          res
        }

        fail("It should never reach here. fx should've thrown $exception")
      } shouldBe Either.Left(exception)
    }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindImmediateValues(
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    checkAll(G) { value ->
      fxBlock {
        val res = invoke(value)
        res
      }.equalUnderTheLaw(value, eq)
    }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindSuspendedValues(
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    checkAll(10, G) { value ->
      fxBlock {
        val res = invoke(value.suspend())
        res
      }.equalUnderTheLaw(value, eq)
    }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindImmediateExceptions(
    G: Arb<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    checkAll(G, Arb.throwable()) { value, e ->
      Either.catch {
        fxBlock {
          val res = invoke(value)
          throw e
          res
        }
        fail("It should never reach here. fx should've thrown $e but found $e")
      } shouldBe Either.Left(e)
    }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindSuspendedExceptions(
    G: Arb<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    checkAll(10, G, Arb.throwable()) { value, e ->
      Either.catch {
        val res = fxBlock {
          val res = invoke(value)
          e.suspend()
          res
        }
        fail("It should never reach here. fx should've thrown $e but found $res")
      } shouldBe Either.Left(e)
    }
  }
}

// TODO expose for tests
internal suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

internal suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }
