package arrow.core

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  t16: Iterable<T16>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val tt16 = t16.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
    t16.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
    && tt16.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
        tt16.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  t16: Iterable<T16>,
  t17: Iterable<T17>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val tt16 = t16.iterator()
  val tt17 = t17.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
    t16.collectionSizeOrDefault(10),
    t17.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
    && tt16.hasNext()
    && tt17.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
        tt16.next(),
        tt17.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  t16: Iterable<T16>,
  t17: Iterable<T17>,
  t18: Iterable<T18>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val tt16 = t16.iterator()
  val tt17 = t17.iterator()
  val tt18 = t18.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
    t16.collectionSizeOrDefault(10),
    t17.collectionSizeOrDefault(10),
    t18.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
    && tt16.hasNext()
    && tt17.hasNext()
    && tt18.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
        tt16.next(),
        tt17.next(),
        tt18.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  t16: Iterable<T16>,
  t17: Iterable<T17>,
  t18: Iterable<T18>,
  t19: Iterable<T19>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val tt16 = t16.iterator()
  val tt17 = t17.iterator()
  val tt18 = t18.iterator()
  val tt19 = t19.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
    t16.collectionSizeOrDefault(10),
    t17.collectionSizeOrDefault(10),
    t18.collectionSizeOrDefault(10),
    t19.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
    && tt16.hasNext()
    && tt17.hasNext()
    && tt18.hasNext()
    && tt19.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
        tt16.next(),
        tt17.next(),
        tt18.next(),
        tt19.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  t16: Iterable<T16>,
  t17: Iterable<T17>,
  t18: Iterable<T18>,
  t19: Iterable<T19>,
  t20: Iterable<T20>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val tt16 = t16.iterator()
  val tt17 = t17.iterator()
  val tt18 = t18.iterator()
  val tt19 = t19.iterator()
  val tt20 = t20.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
    t16.collectionSizeOrDefault(10),
    t17.collectionSizeOrDefault(10),
    t18.collectionSizeOrDefault(10),
    t19.collectionSizeOrDefault(10),
    t20.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
    && tt16.hasNext()
    && tt17.hasNext()
    && tt18.hasNext()
    && tt19.hasNext()
    && tt20.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
        tt16.next(),
        tt17.next(),
        tt18.next(),
        tt19.next(),
        tt20.next(),
      )
    )
  }
  return list
}

public inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> Iterable<T1>.zip(
  t2: Iterable<T2>,
  t3: Iterable<T3>,
  t4: Iterable<T4>,
  t5: Iterable<T5>,
  t6: Iterable<T6>,
  t7: Iterable<T7>,
  t8: Iterable<T8>,
  t9: Iterable<T9>,
  t10: Iterable<T10>,
  t11: Iterable<T11>,
  t12: Iterable<T12>,
  t13: Iterable<T13>,
  t14: Iterable<T14>,
  t15: Iterable<T15>,
  t16: Iterable<T16>,
  t17: Iterable<T17>,
  t18: Iterable<T18>,
  t19: Iterable<T19>,
  t20: Iterable<T20>,
  t21: Iterable<T21>,
  transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
): List<R> {
  val tt1 = iterator()
  val tt2 = t2.iterator()
  val tt3 = t3.iterator()
  val tt4 = t4.iterator()
  val tt5 = t5.iterator()
  val tt6 = t6.iterator()
  val tt7 = t7.iterator()
  val tt8 = t8.iterator()
  val tt9 = t9.iterator()
  val tt10 = t10.iterator()
  val tt11 = t11.iterator()
  val tt12 = t12.iterator()
  val tt13 = t13.iterator()
  val tt14 = t14.iterator()
  val tt15 = t15.iterator()
  val tt16 = t16.iterator()
  val tt17 = t17.iterator()
  val tt18 = t18.iterator()
  val tt19 = t19.iterator()
  val tt20 = t20.iterator()
  val tt21 = t21.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    t2.collectionSizeOrDefault(10),
    t3.collectionSizeOrDefault(10),
    t4.collectionSizeOrDefault(10),
    t5.collectionSizeOrDefault(10),
    t6.collectionSizeOrDefault(10),
    t7.collectionSizeOrDefault(10),
    t8.collectionSizeOrDefault(10),
    t9.collectionSizeOrDefault(10),
    t10.collectionSizeOrDefault(10),
    t11.collectionSizeOrDefault(10),
    t12.collectionSizeOrDefault(10),
    t13.collectionSizeOrDefault(10),
    t14.collectionSizeOrDefault(10),
    t15.collectionSizeOrDefault(10),
    t16.collectionSizeOrDefault(10),
    t17.collectionSizeOrDefault(10),
    t18.collectionSizeOrDefault(10),
    t19.collectionSizeOrDefault(10),
    t20.collectionSizeOrDefault(10),
    t21.collectionSizeOrDefault(10),
  )
  val list = ArrayList<R>(size)
  while (
    tt1.hasNext()
    && tt2.hasNext()
    && tt3.hasNext()
    && tt4.hasNext()
    && tt5.hasNext()
    && tt6.hasNext()
    && tt7.hasNext()
    && tt8.hasNext()
    && tt9.hasNext()
    && tt10.hasNext()
    && tt11.hasNext()
    && tt12.hasNext()
    && tt13.hasNext()
    && tt14.hasNext()
    && tt15.hasNext()
    && tt16.hasNext()
    && tt17.hasNext()
    && tt18.hasNext()
    && tt19.hasNext()
    && tt20.hasNext()
    && tt21.hasNext()
  ) {
    list.add(
      transform(
        tt1.next(),
        tt2.next(),
        tt3.next(),
        tt4.next(),
        tt5.next(),
        tt6.next(),
        tt7.next(),
        tt8.next(),
        tt9.next(),
        tt10.next(),
        tt11.next(),
        tt12.next(),
        tt13.next(),
        tt14.next(),
        tt15.next(),
        tt16.next(),
        tt17.next(),
        tt18.next(),
        tt19.next(),
        tt20.next(),
        tt21.next(),
      )
    )
  }
  return list
}
