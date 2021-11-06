package arrow.continuations.generic

/** Represents a unique identifier using object equality. */
@Deprecated("Deprecated together with SuspendingComputation.")
internal class Token {
  @ExperimentalUnsignedTypes
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}
