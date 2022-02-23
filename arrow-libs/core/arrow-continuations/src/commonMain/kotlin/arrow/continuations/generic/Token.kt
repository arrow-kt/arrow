package arrow.continuations.generic

/** Represents a unique identifier using object equality. */
@Deprecated(deprecateArrowContinuation)
internal class Token {
  override fun toString(): String = "Token(${hashCode().toString(16)})"
}
