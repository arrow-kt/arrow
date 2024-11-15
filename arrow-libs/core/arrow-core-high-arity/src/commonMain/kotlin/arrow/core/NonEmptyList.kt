@file:JvmName("NonEmptyListHighArityKt")
@file:OptIn(ExperimentalContracts::class)
@file:Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")

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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head, t15.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, t15.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head, t15.head, t16.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, t15.tail, t16.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head, t15.head, t16.head, t17.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, t15.tail, t16.tail, t17.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head, t15.head, t16.head, t17.head, t18.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, t15.tail, t16.tail, t17.tail, t18.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head, t15.head, t16.head, t17.head, t18.head, t19.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, t15.tail, t16.tail, t17.tail, t18.tail, t19.tail, map)
  )
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
  return NonEmptyList(
    map(head, t1.head, t2.head, t3.head, t4.head, t5.head, t6.head, t7.head, t8.head, t9.head, t10.head, t11.head, t12.head, t13.head, t14.head, t15.head, t16.head, t17.head, t18.head, t19.head, t20.head),
    tail.zip(t1.tail, t2.tail, t3.tail, t4.tail, t5.tail, t6.tail, t7.tail, t8.tail, t9.tail, t10.tail, t11.tail, t12.tail, t13.tail, t14.tail, t15.tail, t16.tail, t17.tail, t18.tail, t19.tail, t20.tail, map)
  )
}
