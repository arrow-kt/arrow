package arrow.generic

/**
 * The base type of all potential generic types.
 * All values are children of [Bound], all tags are children of [Tag].
 */
public interface Generic<out Bound, out Tag>

public typealias And = AndTagged<*>
public typealias AndTagged<Tag> = AndGeneric<*, Tag>

/**
 * Generic product type.
 * All values are children of [Bound], all tags are children of [Tag].
 */
public sealed interface AndGeneric<out Bound, out Tag> : Generic<Bound, Tag> {
  public fun <R> fold(cons: (Tag, Bound, R) -> R, nil: () -> R) : R

  /**
   * Represents a product of one value [Value] and another product [Rest].
   */
  public data class Cons<out Bound, out Tag, out Value : Bound, out T : Tag, out Rest : AndGeneric<Bound, Tag>>(
    val tag: T, val value: Value, val rest: Rest
  ) : AndGeneric<Bound, Tag> {
    override fun <R> fold(cons: (Tag, Bound, R) -> R, nil: () -> R) : R = cons(tag, value, rest.fold(cons, nil))
  }

  /**
   * Represents the end of the elements in a product. It is equivalent to [Unit].
   */
  public data object Nil : AndGeneric<Nothing, Nothing> {
    override fun <R> fold(cons: (Nothing, Nothing, R) -> R, nil: () -> R) : R = nil()
  }
}

public typealias Or = OrTagged<*>
public typealias OrTagged<Tag> = OrGeneric<*, Tag>

/**
 * Generic sum type.
 * All values are children of [Bound], all tags are children of [Tag].
 */
public sealed interface OrGeneric<out Bound, out Tag> : Generic<Bound, Tag> {
  public fun <R> merge(transform: (Tag, Bound) -> R): R

  /**
   * Represents a potential choice [Value] and more choices afterward [That].
   */
  public sealed interface Cons<out Bound, out Tag, out Value : Bound, out T : Tag, out That : OrGeneric<Bound, Tag>> : OrGeneric<Bound, Tag> {
    public data class This<out Bound, out Tag, out Value : Bound, out T : Tag>(val tag: T, val value: Value) : Cons<Bound, Tag, Value, T, Nothing> {
      override fun <R> merge(transform: (Tag, Bound) -> R): R = transform(tag, value)
    }
    public data class That<out Bound, out Tag, out That : OrGeneric<Bound, Tag>>(val that: That) : Cons<Bound, Tag, Nothing, Nothing, That> {
      override fun <R> merge(transform: (Tag, Bound) -> R): R = that.merge(transform)
    }
  }

  /**
   * Represents no more choices. It is equivalent to [Nothing].
   */
  public class Nil private constructor() : OrGeneric<Nothing, Nothing> {
    override fun <R> merge(transform: (Nothing, Nothing) -> R): R = throw IllegalArgumentException("Or Nil should never be constructed")

    override fun equals(other: Any?): Boolean = other is Nil
    override fun hashCode(): Int = 1
  }
}
