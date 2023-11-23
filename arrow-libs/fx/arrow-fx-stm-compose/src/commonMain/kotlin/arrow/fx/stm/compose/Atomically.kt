package arrow.fx.stm.compose

import androidx.compose.runtime.snapshots.MutableSnapshot
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import kotlinx.coroutines.sync.Mutex
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public tailrec suspend fun <A> atomically(f: STM.() -> A): A {
  val readVariables = mutableSetOf<Any?>()
  val snapshot = Snapshot.takeMutableSnapshot(
    readObserver = readVariables::add
  )
  fourCases(
    runSnapshotEither(snapshot, f),
    snapshot,
    onException = { e -> throw e },
    onRetry = {
      snapshot.dispose()
      if (readVariables.isEmpty()) throw BlockedIndefinitely()
      // set up a listener for changes
      val mutex = Mutex(locked = true)
      val observer = Snapshot.registerApplyObserver { changes, _ ->
        if (readVariables.any { it in changes }) {
          mutex.unlock()
        }
      }
      // wait until one of the read variables changes
      mutex.lock()
      observer.dispose()
      return@atomically atomically(f)
    },
    onApplySuccess = { return@atomically it },
    onApplyFailure = { return@atomically atomically(f)}
  )
  throw IllegalStateException("atomically: you should never reach this")
}

private fun <A> runSnapshotEither(
  snapshot: MutableSnapshot,
  f: STM.() -> A
): Either<STMProblem, A> = snapshot.enter {
  either {
    catch(
      block = { f(ComposeRaiseSTM(snapshot, this)) },
      catch = { raise(STMOtherThrowable(it)) }
    )
  }
}

@OptIn(ExperimentalContracts::class)
private inline fun <A> fourCases(
  result: Either<STMProblem, A>,
  snapshot: MutableSnapshot,
  onException: (Throwable) -> Unit,
  onRetry: () -> Unit,
  onApplySuccess: (A) -> Unit,
  onApplyFailure: (A) -> Unit
) {
  contract {
    callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onRetry, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onApplySuccess, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onApplyFailure, InvocationKind.AT_MOST_ONCE)
  }
  when (result) {
    is Either.Left -> when (val r = result.value) {
      is STMOtherThrowable -> {
        snapshot.dispose()
        onException(r.error)
      }

      is STMRetry -> {
        snapshot.dispose()
        onRetry()
      }
    }

    is Either.Right -> {
      when (snapshot.apply()) {
        SnapshotApplyResult.Success ->
          onApplySuccess(result.value)

        is SnapshotApplyResult.Failure -> {
          snapshot.dispose()
          onApplyFailure(result.value)
        }
      }
    }
  }
}

private sealed interface STMProblem
private data object STMRetry: STMProblem
private data class STMOtherThrowable(val error: Throwable): STMProblem

private class ComposeRaiseSTM(
  val snapshot: MutableSnapshot,
  val raise: Raise<STMProblem>
): STM, Raise<STMProblem> by raise {
  override fun retry(): Nothing = raise.raise(STMRetry)
  override fun <A> (STM.() -> A).orElse(other: STM.() -> A): A {
    val nestedSnapshot = snapshot.takeNestedMutableSnapshot()
    fourCases(
      runSnapshotEither(nestedSnapshot, this),
      nestedSnapshot,
      onException = { e -> raise.raise(STMOtherThrowable(e)) },
      onRetry = { return@orElse other(this@ComposeRaiseSTM) },
      onApplySuccess = { return@orElse it },
      onApplyFailure = { throw IllegalStateException("nested orElse failed") }
    )
    throw IllegalStateException("orElse: you should never reach this")
  }

  override fun <A> catch(f: STM.() -> A, onError: STM.(Throwable) -> A): A {
    val nestedSnapshot = snapshot.takeNestedMutableSnapshot()
    fourCases(
      runSnapshotEither(nestedSnapshot, f),
      nestedSnapshot,
      onException = { e -> return@catch onError(this@ComposeRaiseSTM, e) },
      onRetry = { raise.raise(STMRetry) },
      onApplySuccess = { return@catch it },
      onApplyFailure = { throw IllegalStateException("nested orElse failed") }
    )
    throw IllegalStateException("catch: you should never reach this")
  }
}
