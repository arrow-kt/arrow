package arrow.generic

/**
 * The base type of all potential generic types.
 * All values are children of [Bound], all tags are children of [Tag].
 */
public interface Generic<out Bound, out Tag> {
  /**
   * Generic product type.
   * All values are children of [Bound], all tags are children of [Tag].
   */
  public sealed interface Product<out Bound, out Tag> : Generic<Bound, Tag> {
    public fun <R> fold(cons: (Tag, Bound, R) -> R, nil: () -> R) : R
  }

  /**
   * Represents an element of type value [Value] followed by more elements [Rest].
   */
  public data class And<out Bound, out Tag, out Value : Bound, out T : Tag, out Rest : Product<Bound, Tag>>(
    val tag: T, val value: Value, val rest: Rest
  ) : Product<Bound, Tag> {
    override fun <R> fold(cons: (Tag, Bound, R) -> R, nil: () -> R) : R = cons(tag, value, rest.fold(cons, nil))
  }

  /**
   * Represents the end of the elements in a product. It is equivalent to [Unit].
   */
  public data object Nil : Product<Nothing, Nothing> {
    override fun <R> fold(cons: (Nothing, Nothing, R) -> R, nil: () -> R) : R = nil()
  }

  /**
   * Generic sum type.
   * All values are children of [Bound], all tags are children of [Tag].
   */
  public sealed interface Sum<out Bound, out Tag> : Generic<Bound, Tag> {
    public fun <R> merge(transform: (Tag, Bound) -> R): R
  }

  /**
   * Represents a potential choice [Value] and more choices afterward [That].
   */
  public sealed interface Or<out Bound, out Tag, out Value : Bound, out T : Tag, out That : Sum<Bound, Tag>> : Sum<Bound, Tag> {
    public data class This<out Bound, out Tag, out Value : Bound, out T : Tag>(val tag: T, val value: Value) : Or<Bound, Tag, Value, T, Nothing> {
      override fun <R> merge(transform: (Tag, Bound) -> R): R = transform(tag, value)
    }
    public data class That<out Bound, out Tag, out That : Sum<Bound, Tag>>(val that: That) : Or<Bound, Tag, Nothing, Nothing, That> {
      override fun <R> merge(transform: (Tag, Bound) -> R): R = that.merge(transform)
    }
  }

  /**
   * Represents no more choices. It is equivalent to [Nothing].
   */
  public class Void private constructor() : Sum<Nothing, Nothing> {
    override fun <R> merge(transform: (Nothing, Nothing) -> R): R =
      throw IllegalStateException("Void has no value")
  }
}
