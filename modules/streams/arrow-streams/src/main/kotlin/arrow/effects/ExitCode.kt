package arrow.effects

//Temp copy until https://github.com/arrow-kt/arrow/pull/1043 is merged
sealed class ExitCase<out E> {
  object Completed : ExitCase<Nothing>()
  object Cancelled : ExitCase<Nothing>()
  data class Error<out E>(val e: E) : ExitCase<E>()
}