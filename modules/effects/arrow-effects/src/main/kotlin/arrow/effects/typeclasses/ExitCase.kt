package arrow.effects.typeclasses

import arrow.core.Either
import arrow.effects.typeclasses.ExitCase.Completed
import arrow.effects.typeclasses.ExitCase.Failing

sealed class ExitCase<E> {

  object Completed : ExitCase<Nothing>()

  object Cancelled : ExitCase<Nothing>()

  data class Failing<E>(val e: E) : ExitCase<E>()
}

fun <E> Either<E, *>.toExitCase() =
  fold(::Failing, { Completed })
