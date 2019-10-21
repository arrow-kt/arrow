package arrow.syntax.function

import arrow.core.Tuple10
import arrow.core.Tuple11
import arrow.core.Tuple12
import arrow.core.Tuple13
import arrow.core.Tuple14
import arrow.core.Tuple15
import arrow.core.Tuple16
import arrow.core.Tuple17
import arrow.core.Tuple18
import arrow.core.Tuple19
import arrow.core.Tuple2
import arrow.core.Tuple20
import arrow.core.Tuple21
import arrow.core.Tuple22
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9

fun <T1, T2, R> ((T1, T2) -> R).tupled(): (Tuple2<T1, T2>) -> R = { (t1, t2) -> this(t1, t2) }

fun <T1, T2, T3, R> ((T1, T2, T3) -> R).tupled(): (Tuple3<T1, T2, T3>) -> R = { (t1, t2, t3) -> this(t1, t2, t3) }

fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).tupled(): (Tuple4<T1, T2, T3, T4>) -> R = { (t1, t2, t3, t4) -> this(t1, t2, t3, t4) }

fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).tupled(): (Tuple5<T1, T2, T3, T4, T5>) -> R = { (t1, t2, t3, t4, t5) -> this(t1, t2, t3, t4, t5) }

fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).tupled(): (Tuple6<T1, T2, T3, T4, T5, T6>) -> R = { (t1, t2, t3, t4, t5, t6) -> this(t1, t2, t3, t4, t5, t6) }

fun <T1, T2, T3, T4, T5, T6, T7, R> ((T1, T2, T3, T4, T5, T6, T7) -> R).tupled(): (Tuple7<T1, T2, T3, T4, T5, T6, T7>) -> R = { (t1, t2, t3, t4, t5, t6, t7) -> this(t1, t2, t3, t4, t5, t6, t7) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> ((T1, T2, T3, T4, T5, T6, T7, T8) -> R).tupled(): (Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8) -> this(t1, t2, t3, t4, t5, t6, t7, t8) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R).tupled(): (Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R).tupled(): (Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R).tupled(): (Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R).tupled(): (Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R).tupled(): (Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R).tupled(): (Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R).tupled(): (Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R).tupled(): (Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R).tupled(): (Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R).tupled(): (Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R).tupled(): (Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R).tupled(): (Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R).tupled(): (Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R).tupled(): (Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) -> R = { (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22) -> this(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22) }

fun <T1, T2, R> ((Tuple2<T1, T2>) -> R).untupled(): (T1, T2) -> R = { t1, t2 -> this(Tuple2(t1, t2)) }

fun <T1, T2, T3, R> ((Tuple3<T1, T2, T3>) -> R).untupled(): (T1, T2, T3) -> R = { t1, t2, t3 -> this(Tuple3(t1, t2, t3)) }

fun <T1, T2, T3, T4, R> ((Tuple4<T1, T2, T3, T4>) -> R).untupled(): (T1, T2, T3, T4) -> R = { t1, t2, t3, t4 -> this(Tuple4(t1, t2, t3, t4)) }

fun <T1, T2, T3, T4, T5, R> ((Tuple5<T1, T2, T3, T4, T5>) -> R).untupled(): (T1, T2, T3, T4, T5) -> R = { t1, t2, t3, t4, t5 -> this(Tuple5(t1, t2, t3, t4, t5)) }

fun <T1, T2, T3, T4, T5, T6, R> ((Tuple6<T1, T2, T3, T4, T5, T6>) -> R).untupled(): (T1, T2, T3, T4, T5, T6) -> R = { t1, t2, t3, t4, t5, t6 -> this(Tuple6(t1, t2, t3, t4, t5, t6)) }

fun <T1, T2, T3, T4, T5, T6, T7, R> ((Tuple7<T1, T2, T3, T4, T5, T6, T7>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7) -> R = { t1, t2, t3, t4, t5, t6, t7 -> this(Tuple7(t1, t2, t3, t4, t5, t6, t7)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> ((Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8) -> R = { t1, t2, t3, t4, t5, t6, t7, t8 -> this(Tuple8(t1, t2, t3, t4, t5, t6, t7, t8)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ((Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9 -> this(Tuple9(t1, t2, t3, t4, t5, t6, t7, t8, t9)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ((Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10 -> this(Tuple10(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ((Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11 -> this(Tuple11(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> ((Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12 -> this(Tuple12(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> ((Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13 -> this(Tuple13(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ((Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14 -> this(Tuple14(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ((Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15 -> this(Tuple15(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ((Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16 -> this(Tuple16(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> ((Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17 -> this(Tuple17(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> ((Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18 -> this(Tuple18(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> ((Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19 -> this(Tuple19(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> ((Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20 -> this(Tuple20(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> ((Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21 -> this(Tuple21(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21)) }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> ((Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) -> R).untupled(): (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22 -> this(Tuple22(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22)) }
