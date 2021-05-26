package arrow.core.test.laws

import arrow.continuations.Effect
import arrow.core.test.generators.throwable
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.checkAll
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

  fun <Eff : Effect<*>, F, A> eager(
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
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    G.samples()
      .take(1001)
      .forEach { f ->
        fxBlock {
          val res = invoke(f.value)
          res
        }.equalUnderTheLaw(f.value, eq)
      }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindSuspendedValues(
    G: Arb<F>,
    eq: (F, F) -> Boolean,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    G.samples()
      .take(10)
      .forEach { f ->
        fxBlock {
          val res = invoke(f.value.suspend())
          res
        }.equalUnderTheLaw(f.value, eq)
      }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindImmediateExceptions(
    G: Arb<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    Arb.bind(G, Arb.throwable(), ::Pair)
      .samples()
      .take(1001)
      .forEach { (f, _) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = invoke(f.first)
            throw f.second
            res
          }
          fail("It should never reach here. fx should've thrown ${f.second}")
        } shouldBe f.second
      }
  }

  private suspend fun <Eff : Effect<*>, F, A> suspendedCanBindSuspendedExceptions(
    G: Arb<F>,
    fxBlock: SuspendFxBlock<Eff, F, A>,
    invoke: suspend Eff.(F) -> A
  ) {
    Arb.bind(G, Arb.throwable(), ::Pair)
      .samples()
      .take(10)
      .forEach { (f, _) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = invoke(f.first)
            f.second.suspend()
            res
          }
          fail("It should never reach here. fx should've thrown ${f.second}")
        } shouldBe f.second
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
