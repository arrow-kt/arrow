package arrow.continuations.generic

/** Represents a unique identifier using object equality. */
internal class Token {
  @ExperimentalUnsignedTypes
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}
