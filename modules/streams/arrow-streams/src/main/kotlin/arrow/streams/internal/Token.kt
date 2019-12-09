package arrow.streams.internal

/** Represents a unique identifier (using object equality). */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}
