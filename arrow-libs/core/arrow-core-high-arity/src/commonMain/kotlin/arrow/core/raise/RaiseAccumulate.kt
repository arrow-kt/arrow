@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class, ExperimentalRaiseAccumulateApi::class)
@file:JvmMultifileClass
@file:JvmName("RaiseHighArityKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")

package arrow.core.raise

import arrow.core.NonEmptyList
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  @BuilderInference action15: RaiseAccumulate<Error>.() -> T15,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action15, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    val o = accumulating(action15)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  @BuilderInference action15: RaiseAccumulate<Error>.() -> T15,
  @BuilderInference action16: RaiseAccumulate<Error>.() -> T16,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action15, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action16, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    val o = accumulating(action15)
    val p = accumulating(action16)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  @BuilderInference action15: RaiseAccumulate<Error>.() -> T15,
  @BuilderInference action16: RaiseAccumulate<Error>.() -> T16,
  @BuilderInference action17: RaiseAccumulate<Error>.() -> T17,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action15, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action16, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action17, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    val o = accumulating(action15)
    val p = accumulating(action16)
    val q = accumulating(action17)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  @BuilderInference action15: RaiseAccumulate<Error>.() -> T15,
  @BuilderInference action16: RaiseAccumulate<Error>.() -> T16,
  @BuilderInference action17: RaiseAccumulate<Error>.() -> T17,
  @BuilderInference action18: RaiseAccumulate<Error>.() -> T18,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action15, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action16, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action17, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action18, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    val o = accumulating(action15)
    val p = accumulating(action16)
    val q = accumulating(action17)
    val r = accumulating(action18)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  @BuilderInference action15: RaiseAccumulate<Error>.() -> T15,
  @BuilderInference action16: RaiseAccumulate<Error>.() -> T16,
  @BuilderInference action17: RaiseAccumulate<Error>.() -> T17,
  @BuilderInference action18: RaiseAccumulate<Error>.() -> T18,
  @BuilderInference action19: RaiseAccumulate<Error>.() -> T19,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action15, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action16, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action17, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action18, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action19, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    val o = accumulating(action15)
    val p = accumulating(action16)
    val q = accumulating(action17)
    val r = accumulating(action18)
    val s = accumulating(action19)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value)
  }
}

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  @BuilderInference action10: RaiseAccumulate<Error>.() -> T10,
  @BuilderInference action11: RaiseAccumulate<Error>.() -> T11,
  @BuilderInference action12: RaiseAccumulate<Error>.() -> T12,
  @BuilderInference action13: RaiseAccumulate<Error>.() -> T13,
  @BuilderInference action14: RaiseAccumulate<Error>.() -> T14,
  @BuilderInference action15: RaiseAccumulate<Error>.() -> T15,
  @BuilderInference action16: RaiseAccumulate<Error>.() -> T16,
  @BuilderInference action17: RaiseAccumulate<Error>.() -> T17,
  @BuilderInference action18: RaiseAccumulate<Error>.() -> T18,
  @BuilderInference action19: RaiseAccumulate<Error>.() -> T19,
  @BuilderInference action20: RaiseAccumulate<Error>.() -> T20,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): R {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action10, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action11, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action12, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action13, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action14, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action15, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action16, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action17, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action18, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action19, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action20, InvocationKind.EXACTLY_ONCE)
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return accumulate {
    val a = accumulating(action1)
    val b = accumulating(action2)
    val c = accumulating(action3)
    val d = accumulating(action4)
    val e = accumulating(action5)
    val f = accumulating(action6)
    val g = accumulating(action7)
    val h = accumulating(action8)
    val i = accumulating(action9)
    val j = accumulating(action10)
    val k = accumulating(action11)
    val l = accumulating(action12)
    val m = accumulating(action13)
    val n = accumulating(action14)
    val o = accumulating(action15)
    val p = accumulating(action16)
    val q = accumulating(action17)
    val r = accumulating(action18)
    val s = accumulating(action19)
    val t = accumulating(action20)
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value)
  }
}

