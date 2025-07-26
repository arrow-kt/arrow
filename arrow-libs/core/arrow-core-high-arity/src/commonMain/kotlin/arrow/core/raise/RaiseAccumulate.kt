@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class, ExperimentalRaiseAccumulateApi::class)
@file:JvmMultifileClass
@file:JvmName("RaiseHighArityKt")

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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
      ) { _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
      ) { _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
      ) { _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
      ) { _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
      ) { _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  val a15: T15
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
        { a15 = action15() },
      ) { _, _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  val a15: T15
  val a16: T16
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
        { a15 = action15() },
        { a16 = action16() },
      ) { _, _, _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  val a15: T15
  val a16: T16
  val a17: T17
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
        { a15 = action15() },
        { a16 = action16() },
        { a17 = action17() },
      ) { _, _, _, _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  val a15: T15
  val a16: T16
  val a17: T17
  val a18: T18
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
        { a15 = action15() },
        { a16 = action16() },
        { a17 = action17() },
        { a18 = action18() },
      ) { _, _, _, _, _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  val a15: T15
  val a16: T16
  val a17: T17
  val a18: T18
  val a19: T19
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
        { a15 = action15() },
        { a16 = action16() },
        { a17 = action17() },
        { a18 = action18() },
        { a19 = action19() },
      ) { _, _, _, _, _, _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
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
  val a1: T1
  val a2: T2
  val a3: T3
  val a4: T4
  val a5: T5
  val a6: T6
  val a7: T7
  val a8: T8
  val a9: T9
  val a10: T10
  val a11: T11
  val a12: T12
  val a13: T13
  val a14: T14
  val a15: T15
  val a16: T16
  val a17: T17
  val a18: T18
  val a19: T19
  val a20: T20
  zipOrAccumulate(
    { a1 = action1() },
    { a2 = action2() },
    { a3 = action3() },
    { a4 = action4() },
    { a5 = action5() },
    { a6 = action6() },
    { a7 = action7() },
    { a8 = action8() },
    {
      zipOrAccumulate(
        { a9 = action9() },
        { a10 = action10() },
        { a11 = action11() },
        { a12 = action12() },
        { a13 = action13() },
        { a14 = action14() },
        { a15 = action15() },
        { a16 = action16() },
        { a17 = action17() },
        { a18 = action18() },
        { a19 = action19() },
        { a20 = action20() },
      ) { _, _, _, _, _, _, _, _, _, _, _, _ -> }
    }
  ) { _, _, _, _, _, _, _, _, _ -> }
  return block(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
}

