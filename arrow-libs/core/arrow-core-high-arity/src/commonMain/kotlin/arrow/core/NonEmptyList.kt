@file:JvmName("NonEmptyListHighArityKt")
@file:OptIn(ExperimentalContracts::class, PotentiallyUnsafeNonEmptyOperation::class)
@file:Suppress("WRONG_INVOCATION_KIND", "LEAKED_IN_PLACE_LAMBDA")

package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  t15: NonEmptyList<T15>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, t15.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  t15: NonEmptyList<T15>,
  t16: NonEmptyList<T16>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, t15.all, t16.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  t15: NonEmptyList<T15>,
  t16: NonEmptyList<T16>,
  t17: NonEmptyList<T17>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, t15.all, t16.all, t17.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  t15: NonEmptyList<T15>,
  t16: NonEmptyList<T16>,
  t17: NonEmptyList<T17>,
  t18: NonEmptyList<T18>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, t15.all, t16.all, t17.all, t18.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  t15: NonEmptyList<T15>,
  t16: NonEmptyList<T16>,
  t17: NonEmptyList<T17>,
  t18: NonEmptyList<T18>,
  t19: NonEmptyList<T19>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, t15.all, t16.all, t17.all, t18.all, t19.all, map).wrapAsNonEmptyListOrThrow()
}

public inline fun <A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> NonEmptyList<A>.zip(
  t1: NonEmptyList<T1>,
  t2: NonEmptyList<T2>,
  t3: NonEmptyList<T3>,
  t4: NonEmptyList<T4>,
  t5: NonEmptyList<T5>,
  t6: NonEmptyList<T6>,
  t7: NonEmptyList<T7>,
  t8: NonEmptyList<T8>,
  t9: NonEmptyList<T9>,
  t10: NonEmptyList<T10>,
  t11: NonEmptyList<T11>,
  t12: NonEmptyList<T12>,
  t13: NonEmptyList<T13>,
  t14: NonEmptyList<T14>,
  t15: NonEmptyList<T15>,
  t16: NonEmptyList<T16>,
  t17: NonEmptyList<T17>,
  t18: NonEmptyList<T18>,
  t19: NonEmptyList<T19>,
  t20: NonEmptyList<T20>,
  map: (A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): NonEmptyList<R> {
  contract { callsInPlace(map, InvocationKind.AT_LEAST_ONCE) }
  return all.zip(t1.all, t2.all, t3.all, t4.all, t5.all, t6.all, t7.all, t8.all, t9.all, t10.all, t11.all, t12.all, t13.all, t14.all, t15.all, t16.all, t17.all, t18.all, t19.all, t20.all, map).wrapAsNonEmptyListOrThrow()
}
