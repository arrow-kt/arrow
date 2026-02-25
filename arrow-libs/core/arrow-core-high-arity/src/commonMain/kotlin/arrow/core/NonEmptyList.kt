@file:JvmName("NonEmptyListHighArityKt")
@file:OptIn(ExperimentalContracts::class)

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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  val i15 = t15.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size, t15.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next(), i15.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext() && i15.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  val i15 = t15.iterator()
  val i16 = t16.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size, t15.size, t16.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next(), i15.next(), i16.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext() && i15.hasNext() && i16.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  val i15 = t15.iterator()
  val i16 = t16.iterator()
  val i17 = t17.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size, t15.size, t16.size, t17.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next(), i15.next(), i16.next(), i17.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext() && i15.hasNext() && i16.hasNext() && i17.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  val i15 = t15.iterator()
  val i16 = t16.iterator()
  val i17 = t17.iterator()
  val i18 = t18.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size, t15.size, t16.size, t17.size, t18.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next(), i15.next(), i16.next(), i17.next(), i18.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext() && i15.hasNext() && i16.hasNext() && i17.hasNext() && i18.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  val i15 = t15.iterator()
  val i16 = t16.iterator()
  val i17 = t17.iterator()
  val i18 = t18.iterator()
  val i19 = t19.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size, t15.size, t16.size, t17.size, t18.size, t19.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next(), i15.next(), i16.next(), i17.next(), i18.next(), i19.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext() && i15.hasNext() && i16.hasNext() && i17.hasNext() && i18.hasNext() && i19.hasNext())
    this
  }
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
  val i0 = iterator()
  val i1 = t1.iterator()
  val i2 = t2.iterator()
  val i3 = t3.iterator()
  val i4 = t4.iterator()
  val i5 = t5.iterator()
  val i6 = t6.iterator()
  val i7 = t7.iterator()
  val i8 = t8.iterator()
  val i9 = t9.iterator()
  val i10 = t10.iterator()
  val i11 = t11.iterator()
  val i12 = t12.iterator()
  val i13 = t13.iterator()
  val i14 = t14.iterator()
  val i15 = t15.iterator()
  val i16 = t16.iterator()
  val i17 = t17.iterator()
  val i18 = t18.iterator()
  val i19 = t19.iterator()
  val i20 = t20.iterator()
  return buildNonEmptyList(minOf(size, t1.size, t2.size, t3.size, t4.size, t5.size, t6.size, t7.size, t8.size, t9.size, t10.size, t11.size, t12.size, t13.size, t14.size, t15.size, t16.size, t17.size, t18.size, t19.size, t20.size)) {
    do add(map(i0.next(), i1.next(), i2.next(), i3.next(), i4.next(), i5.next(), i6.next(), i7.next(), i8.next(), i9.next(), i10.next(), i11.next(), i12.next(), i13.next(), i14.next(), i15.next(), i16.next(), i17.next(), i18.next(), i19.next(), i20.next()))
    while (i0.hasNext() && i1.hasNext() && i2.hasNext() && i3.hasNext() && i4.hasNext() && i5.hasNext() && i6.hasNext() && i7.hasNext() && i8.hasNext() && i9.hasNext() && i10.hasNext() && i11.hasNext() && i12.hasNext() && i13.hasNext() && i14.hasNext() && i15.hasNext() && i16.hasNext() && i17.hasNext() && i18.hasNext() && i19.hasNext() && i20.hasNext())
    this
  }
}
