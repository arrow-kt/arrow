@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")

package arrow.core.raise

import arrow.core.NonEmptyList
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    block(a, b, c, d, e, f, g, h, i, j)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    block(a, b, c, d, e, f, g, h, i, j, k)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    block(a, b, c, d, e, f, g, h, i, j, k, l)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    val o by accumulating(action15)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    val o by accumulating(action15)
    val p by accumulating(action16)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    val o by accumulating(action15)
    val p by accumulating(action16)
    val q by accumulating(action17)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    val o by accumulating(action15)
    val p by accumulating(action16)
    val q by accumulating(action17)
    val r by accumulating(action18)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    val o by accumulating(action15)
    val p by accumulating(action16)
    val q by accumulating(action17)
    val r by accumulating(action18)
    val s by accumulating(action19)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate {
    val a by accumulating(action1)
    val b by accumulating(action2)
    val c by accumulating(action3)
    val d by accumulating(action4)
    val e by accumulating(action5)
    val f by accumulating(action6)
    val g by accumulating(action7)
    val h by accumulating(action8)
    val i by accumulating(action9)
    val j by accumulating(action10)
    val k by accumulating(action11)
    val l by accumulating(action12)
    val m by accumulating(action13)
    val n by accumulating(action14)
    val o by accumulating(action15)
    val p by accumulating(action16)
    val q by accumulating(action17)
    val r by accumulating(action18)
    val s by accumulating(action19)
    val t by accumulating(action20)
    block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
  }
}

