package arrow.generic

public data class Tagged<Value, Tag>(val tag: Tag, val value: Value)
public infix fun <Value, Tag> Value.tag(tag: Tag): Tagged<Value, Tag> = Tagged(tag, this)
public fun <Value> Value.untagged(): Tagged<Value, *> = Tagged(null, this)

public val nil: AndGeneric.Nil = AndGeneric.Nil

public infix fun <Bound, Tag, Value : Bound, T : Tag, Rest : AndGeneric<Bound, Tag>> Tagged<Value, T>.and(rest: Rest): AndGeneric.Cons<Bound, Tag, Value, T, Rest> =
  AndGeneric.Cons(tag, value, rest)

public fun <Bound, Tag, V1 : Bound, T1 : Tag> and(
  x1: Tagged<V1, T1>
): AndGeneric.Cons<Bound, Tag, V1, T1, AndGeneric.Nil> =
  x1 and nil

public fun <Bound, Tag, V1 : Bound, T1 : Tag, V2 : Bound, T2 : Tag> and(
  x1: Tagged<V1, T1>, x2: Tagged<V2, T2>
): AndGeneric.Cons<Bound, Tag, V1, T1, AndGeneric.Cons<Bound, Tag, V2, T2, AndGeneric.Nil>> =
  x1 and (x2 and nil)

public fun <Bound, Tag, V1 : Bound, T1 : Tag, V2 : Bound, T2 : Tag, V3 : Bound, T3 : Tag> and(
  x1: Tagged<V1, T1>, x2: Tagged<V2, T2>, x3: Tagged<V3, T3>
): AndGeneric.Cons<Bound, Tag, V1, T1, AndGeneric.Cons<Bound, Tag, V2, T2, AndGeneric.Cons<Bound, Tag, V3, T3, AndGeneric.Nil>>> =
  x1 and (x2 and (x3 and nil))

public fun <Bound, Tag, Value : Bound, T : Tag> Tagged<Value, T>.`this`(): OrGeneric.Cons<Bound, Tag, Value, T, Nothing> =
  OrGeneric.Cons.This(tag, value)

public fun <Bound, Tag, Rest : OrGeneric<Bound, Tag>> Rest.that(): OrGeneric.Cons<Bound, Tag, Nothing, Nothing, Rest> =
  OrGeneric.Cons.That(this)
