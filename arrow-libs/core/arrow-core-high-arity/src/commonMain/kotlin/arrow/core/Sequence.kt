package arrow.core

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  s17: Sequence<T17>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()
    val iterator17 = s17.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
        iterator17.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
        && iterator17.hasNext()

  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  s17: Sequence<T17>,
  s18: Sequence<T18>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()
    val iterator17 = s17.iterator()
    val iterator18 = s18.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
        iterator17.next(),
        iterator18.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
        && iterator17.hasNext()
        && iterator18.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  s17: Sequence<T17>,
  s18: Sequence<T18>,
  s19: Sequence<T19>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()
    val iterator17 = s17.iterator()
    val iterator18 = s18.iterator()
    val iterator19 = s19.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
        iterator17.next(),
        iterator18.next(),
        iterator19.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
        && iterator17.hasNext()
        && iterator18.hasNext()
        && iterator19.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  s17: Sequence<T17>,
  s18: Sequence<T18>,
  s19: Sequence<T19>,
  s20: Sequence<T20>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()
    val iterator17 = s17.iterator()
    val iterator18 = s18.iterator()
    val iterator19 = s19.iterator()
    val iterator20 = s20.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
        iterator17.next(),
        iterator18.next(),
        iterator19.next(),
        iterator20.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
        && iterator17.hasNext()
        && iterator18.hasNext()
        && iterator19.hasNext()
        && iterator20.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  s17: Sequence<T17>,
  s18: Sequence<T18>,
  s19: Sequence<T19>,
  s20: Sequence<T20>,
  s21: Sequence<T21>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()
    val iterator17 = s17.iterator()
    val iterator18 = s18.iterator()
    val iterator19 = s19.iterator()
    val iterator20 = s20.iterator()
    val iterator21 = s21.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
        iterator17.next(),
        iterator18.next(),
        iterator19.next(),
        iterator20.next(),
        iterator21.next(),
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
        && iterator17.hasNext()
        && iterator18.hasNext()
        && iterator19.hasNext()
        && iterator20.hasNext()
        && iterator21.hasNext()
  }
}

public fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> Sequence<T1>.zip(
  s2: Sequence<T2>,
  s3: Sequence<T3>,
  s4: Sequence<T4>,
  s5: Sequence<T5>,
  s6: Sequence<T6>,
  s7: Sequence<T7>,
  s8: Sequence<T8>,
  s9: Sequence<T9>,
  s10: Sequence<T10>,
  s11: Sequence<T11>,
  s12: Sequence<T12>,
  s13: Sequence<T13>,
  s14: Sequence<T14>,
  s15: Sequence<T15>,
  s16: Sequence<T16>,
  s17: Sequence<T17>,
  s18: Sequence<T18>,
  s19: Sequence<T19>,
  s20: Sequence<T20>,
  s21: Sequence<T21>,
  s22: Sequence<T22>,
  map: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R,
): Sequence<R> = Sequence {
  object : Iterator<R> {
    val iterator1 = this@zip.iterator()
    val iterator2 = s2.iterator()
    val iterator3 = s3.iterator()
    val iterator4 = s4.iterator()
    val iterator5 = s5.iterator()
    val iterator6 = s6.iterator()
    val iterator7 = s7.iterator()
    val iterator8 = s8.iterator()
    val iterator9 = s9.iterator()
    val iterator10 = s10.iterator()
    val iterator11 = s11.iterator()
    val iterator12 = s12.iterator()
    val iterator13 = s13.iterator()
    val iterator14 = s14.iterator()
    val iterator15 = s15.iterator()
    val iterator16 = s16.iterator()
    val iterator17 = s17.iterator()
    val iterator18 = s18.iterator()
    val iterator19 = s19.iterator()
    val iterator20 = s20.iterator()
    val iterator21 = s21.iterator()
    val iterator22 = s22.iterator()

    override fun next(): R =
      map(
        iterator1.next(),
        iterator2.next(),
        iterator3.next(),
        iterator4.next(),
        iterator5.next(),
        iterator6.next(),
        iterator7.next(),
        iterator8.next(),
        iterator9.next(),
        iterator10.next(),
        iterator11.next(),
        iterator12.next(),
        iterator13.next(),
        iterator14.next(),
        iterator15.next(),
        iterator16.next(),
        iterator17.next(),
        iterator18.next(),
        iterator19.next(),
        iterator20.next(),
        iterator21.next(),
        iterator22.next()
      )

    override fun hasNext(): Boolean =
      iterator1.hasNext()
        && iterator2.hasNext()
        && iterator3.hasNext()
        && iterator4.hasNext()
        && iterator5.hasNext()
        && iterator6.hasNext()
        && iterator7.hasNext()
        && iterator8.hasNext()
        && iterator9.hasNext()
        && iterator10.hasNext()
        && iterator11.hasNext()
        && iterator12.hasNext()
        && iterator13.hasNext()
        && iterator14.hasNext()
        && iterator15.hasNext()
        && iterator16.hasNext()
        && iterator17.hasNext()
        && iterator18.hasNext()
        && iterator19.hasNext()
        && iterator20.hasNext()
        && iterator21.hasNext()
        && iterator22.hasNext()
  }
}
