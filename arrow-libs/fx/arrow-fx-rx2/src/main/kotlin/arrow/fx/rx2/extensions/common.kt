package arrow.fx.rx2.extensions

import arrow.core.Tuple2
import arrow.core.Tuple3
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3

internal fun <A, B> tupled2(): BiFunction<A, B, Tuple2<A, B>> =
  BiFunction { a: A, b: B -> Tuple2(a, b) }

internal fun <A, B, C> tupled3(): Function3<A, B, C, Tuple3<A, B, C>> =
  Function3 { a: A, b: B, c: C -> Tuple3(a, b, c) }
