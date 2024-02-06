@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")

package arrow.core.raise

import arrow.core.EmptyValue
import arrow.core.EmptyValue.unbox
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@RaiseDSL
public inline fun <Error, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> T1,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> T2,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> T3,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> T4,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> T5,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> T6,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> T7,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> T8,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> T9,
  block: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): R {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9))
}

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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t15 = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14), unbox(t15))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t15 = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t16 = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14), unbox(t15), unbox(t16))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t15 = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t16 = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t17 = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14), unbox(t15), unbox(t16), unbox(t17))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t15 = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t16 = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t17 = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t18 = recover({ action18(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14), unbox(t15), unbox(t16), unbox(t17), unbox(t18))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t15 = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t16 = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t17 = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t18 = recover({ action18(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t19 = recover({ action19(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14), unbox(t15), unbox(t16), unbox(t17), unbox(t18), unbox(t19))
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
  val error: MutableList<Error> = mutableListOf()
  val t1 = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t2 = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t3 = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t4 = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t5 = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t6 = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t7 = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t8 = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t9 = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t10 = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t11 = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t12 = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t13 = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t14 = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t15 = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t16 = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t17 = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t18 = recover({ action18(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t19 = recover({ action19(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val t20 = recover({ action20(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(t1), unbox(t2), unbox(t3), unbox(t4), unbox(t5), unbox(t6), unbox(t7), unbox(t8), unbox(t9), unbox(t10), unbox(t11), unbox(t12), unbox(t13), unbox(t14), unbox(t15), unbox(t16), unbox(t17), unbox(t18), unbox(t19), unbox(t20))
}

