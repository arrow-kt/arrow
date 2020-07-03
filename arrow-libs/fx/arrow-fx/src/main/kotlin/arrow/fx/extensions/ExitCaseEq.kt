package arrow.fx.extensions

import arrow.extension
import arrow.fx.typeclasses.ExitCase
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
