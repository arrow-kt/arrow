package arrow.fx.extensions

import arrow.extension
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.ExitCase2
import arrow.typeclasses.Eq

@extension
interface ExitCaseEq<E> : Eq<ExitCase<E>> {
  fun EQE(): Eq<E>

  override fun ExitCase<E>.eqv(b: ExitCase<E>): Boolean = when (this) {
    ExitCase.Completed -> when (b) {
      ExitCase.Completed -> true
      else -> false
    }
    ExitCase.Cancelled -> when (b) {
      ExitCase.Cancelled -> true
      else -> false
    }
    is ExitCase.Error -> when (b) {
      is ExitCase.Error -> EQE().run { e.eqv(b.e) }
      else -> false
    }
  }
}

@extension
interface ExitCase2Eq<E> : Eq<ExitCase2<E>> {
  fun EQE(): Eq<E>
  fun EQThrowable(): Eq<Throwable>

  override fun ExitCase2<E>.eqv(b: ExitCase2<E>): Boolean = when (this) {
    ExitCase2.Completed -> when (b) {
      ExitCase2.Completed -> true
      else -> false
    }
    ExitCase2.Cancelled -> when (b) {
      ExitCase2.Cancelled -> true
      else -> false
    }
    is ExitCase2.Error -> when (b) {
      is ExitCase2.Error -> EQE().run { error.eqv(b.error) }
      else -> false
    }
    is ExitCase2.Exception -> when (b) {
      is ExitCase2.Exception -> EQThrowable().run { exception.eqv(b.exception) }
      else -> false
    }
  }
}
