package arrow.generic

public data class Tagged<out Value, out Tag>(val tag: Tag, val value: Value)
public infix fun <Value, Tag> Value.tag(tag: Tag): Tagged<Value, Tag> = Tagged(tag, this)
public fun <Value> Value.untagged(): Tagged<Value, Nothing?> = Tagged(null, this)

public val nil: Generic.Nil = Generic.Nil

public infix fun <Bound, Tag, Value : Bound, T : Tag, Rest : Generic.Product<Bound, Tag>> Tagged<Value, T>.and(rest: Rest): Generic.And<Bound, Tag, Value, T, Rest> =
  Generic.And(tag, value, rest)

public fun <Bound, Tag, V1 : Bound, T1 : Tag> and(
  x1: Tagged<V1, T1>
): Generic.And<Bound, Tag, V1, T1, Generic.Nil> =
  x1 and nil

public fun <Bound, Tag, V1 : Bound, T1 : Tag, V2 : Bound, T2 : Tag> and(
  x1: Tagged<V1, T1>, x2: Tagged<V2, T2>
): Generic.And<Bound, Tag, V1, T1, Generic.And<Bound, Tag, V2, T2, Generic.Nil>> =
  x1 and (x2 and nil)

public fun <Bound, Tag, V1 : Bound, T1 : Tag, V2 : Bound, T2 : Tag, V3 : Bound, T3 : Tag> and(
  x1: Tagged<V1, T1>, x2: Tagged<V2, T2>, x3: Tagged<V3, T3>
): Generic.And<Bound, Tag, V1, T1, Generic.And<Bound, Tag, V2, T2, Generic.And<Bound, Tag, V3, T3, Generic.Nil>>> =
  x1 and (x2 and (x3 and nil))

public fun <Bound, Tag, Value : Bound, T : Tag> Tagged<Value, T>.`this`(): Generic.Or.This<Bound, Tag, Value, T> =
  Generic.Or.This(tag, value)

public fun <Bound, Tag, Rest : Generic.Sum<Bound, Tag>> Rest.that(): Generic.Or.That<Bound, Tag, Rest> =
  Generic.Or.That(this)

/**
 * Used to ensure that a match casts to [Void], to keep exhaustivity.
 */
public fun Generic.Void.absurd(): Nothing =
  throw IllegalStateException("Void has no value")
