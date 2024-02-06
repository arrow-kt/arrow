package arrow.core

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  m16: Map<Key, T16>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
      && m16.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15
      val mm16 = m16[key] as T16

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
          mm16,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  m16: Map<Key, T16>,
  m17: Map<Key, T17>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
      && m16.containsKey(key)
      && m17.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15
      val mm16 = m16[key] as T16
      val mm17 = m17[key] as T17

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
          mm16,
          mm17,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  m16: Map<Key, T16>,
  m17: Map<Key, T17>,
  m18: Map<Key, T18>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
      && m16.containsKey(key)
      && m17.containsKey(key)
      && m18.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15
      val mm16 = m16[key] as T16
      val mm17 = m17[key] as T17
      val mm18 = m18[key] as T18

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
          mm16,
          mm17,
          mm18,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  m16: Map<Key, T16>,
  m17: Map<Key, T17>,
  m18: Map<Key, T18>,
  m19: Map<Key, T19>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
      && m16.containsKey(key)
      && m17.containsKey(key)
      && m18.containsKey(key)
      && m19.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15
      val mm16 = m16[key] as T16
      val mm17 = m17[key] as T17
      val mm18 = m18[key] as T18
      val mm19 = m19[key] as T19

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
          mm16,
          mm17,
          mm18,
          mm19
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  m16: Map<Key, T16>,
  m17: Map<Key, T17>,
  m18: Map<Key, T18>,
  m19: Map<Key, T19>,
  m20: Map<Key, T20>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
      && m16.containsKey(key)
      && m17.containsKey(key)
      && m18.containsKey(key)
      && m19.containsKey(key)
      && m20.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15
      val mm16 = m16[key] as T16
      val mm17 = m17[key] as T17
      val mm18 = m18[key] as T18
      val mm19 = m19[key] as T19
      val mm20 = m20[key] as T20
      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
          mm16,
          mm17,
          mm18,
          mm19,
          mm20,
        )
      )
    }
  }
}

@Suppress("UNCHECKED_CAST")
public inline fun <Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> Map<Key, A>.zip(
  m1: Map<Key, T1>,
  m2: Map<Key, T2>,
  m3: Map<Key, T3>,
  m4: Map<Key, T4>,
  m5: Map<Key, T5>,
  m6: Map<Key, T6>,
  m7: Map<Key, T7>,
  m8: Map<Key, T8>,
  m9: Map<Key, T9>,
  m10: Map<Key, T10>,
  m11: Map<Key, T11>,
  m12: Map<Key, T12>,
  m13: Map<Key, T13>,
  m14: Map<Key, T14>,
  m15: Map<Key, T15>,
  m16: Map<Key, T16>,
  m17: Map<Key, T17>,
  m18: Map<Key, T18>,
  m19: Map<Key, T19>,
  m20: Map<Key, T20>,
  m21: Map<Key, T21>,
  map: (Key, A, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
): Map<Key, R> = buildMap(size) {
  this@zip.forEach { (key, bb) ->
    if (m1.containsKey(key)
      && m2.containsKey(key)
      && m3.containsKey(key)
      && m4.containsKey(key)
      && m5.containsKey(key)
      && m6.containsKey(key)
      && m7.containsKey(key)
      && m8.containsKey(key)
      && m9.containsKey(key)
      && m10.containsKey(key)
      && m11.containsKey(key)
      && m12.containsKey(key)
      && m13.containsKey(key)
      && m14.containsKey(key)
      && m15.containsKey(key)
      && m16.containsKey(key)
      && m17.containsKey(key)
      && m18.containsKey(key)
      && m19.containsKey(key)
      && m20.containsKey(key)
      && m21.containsKey(key)
    ) {
      val mm1 = m1[key] as T1
      val mm2 = m2[key] as T2
      val mm3 = m3[key] as T3
      val mm4 = m4[key] as T4
      val mm5 = m5[key] as T5
      val mm6 = m6[key] as T6
      val mm7 = m7[key] as T7
      val mm8 = m8[key] as T8
      val mm9 = m9[key] as T9
      val mm10 = m10[key] as T10
      val mm11 = m11[key] as T11
      val mm12 = m12[key] as T12
      val mm13 = m13[key] as T13
      val mm14 = m14[key] as T14
      val mm15 = m15[key] as T15
      val mm16 = m16[key] as T16
      val mm17 = m17[key] as T17
      val mm18 = m18[key] as T18
      val mm19 = m19[key] as T19
      val mm20 = m20[key] as T20
      val mm21 = m21[key] as T21

      put(
        key, map(
          key,
          bb,
          mm1,
          mm2,
          mm3,
          mm4,
          mm5,
          mm6,
          mm7,
          mm8,
          mm9,
          mm10,
          mm11,
          mm12,
          mm13,
          mm14,
          mm15,
          mm16,
          mm17,
          mm18,
          mm19,
          mm20,
          mm21
        )
      )
    }
  }
}
