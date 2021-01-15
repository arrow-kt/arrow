package arrow.core.extensions.sequence.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.SequenceKApply
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ap(arg1)",
  "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.ap(arg1: Sequence<Function1<A, B>>): Sequence<B> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@ap).ap<A, B>(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apEval(arg1)",
  "arrow.core.apEval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.apEval(arg1: Eval<Kind<ForSequenceK, Function1<A, B>>>):
    Eval<Kind<ForSequenceK, B>> = arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@apEval).apEval<A, B>(arg1) as
    arrow.core.Eval<arrow.Kind<arrow.core.ForSequenceK, B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map2Eval(arg1, arg2)",
  "arrow.core.map2Eval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Sequence<A>.map2Eval(
  arg1: Eval<Kind<ForSequenceK, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<ForSequenceK, Z>> = arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@map2Eval).map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.core.ForSequenceK, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arg2) as
    kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arg2) as
    kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arg3) as
    kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arg3) as
    kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arg4)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arg4)
    as kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D, E,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arg5)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D, E,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arg5)
    as kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D, E, FF,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arg6)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D, E, FF,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arg6)
    as kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D, E, FF, G,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arg7)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D, E, FF, G,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arg7)
    as kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D, E, FF, G, H,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arg8)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D, E, FF, G, H,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arg8)
    as kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8), arg9)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8), arg9)
    as kotlin.sequences.Sequence<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.extensions.sequence.apply.Sequence.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>,
  arg9: Sequence<J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8), arrow.core.SequenceK(arg9), arg10)
    as kotlin.sequences.Sequence<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.extensions.sequence.apply.Sequence.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>,
  arg9: Sequence<J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Sequence<Z> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8), arrow.core.SequenceK(arg9), arg10)
    as kotlin.sequences.Sequence<Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map2(arg1, arg2)",
  "arrow.core.map2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Sequence<A>.map2(arg1: Sequence<B>, arg2: Function1<Tuple2<A, B>, Z>): Sequence<Z> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@map2).map2<A, B, Z>(arrow.core.SequenceK(arg1), arg2) as
    kotlin.sequences.Sequence<Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.product(arg1: Sequence<B>): Sequence<Tuple2<A, B>> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple2<A, B>>
}

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Sequence<Tuple2<A, B>>.product(arg1: Sequence<Z>): Sequence<Tuple3<A, B, Z>> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, Z>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> Sequence<Tuple3<A, B, C>>.product(arg1: Sequence<Z>): Sequence<Tuple4<A, B, C, Z>> =
  arrow.core.extensions.sequence.apply.Sequence.apply().run {
    arrow.core.SequenceK(this@product).product<A, B, C, Z>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<arrow.core.Tuple4<A, B, C, Z>>
  }

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> Sequence<Tuple4<A, B, C, D>>.product(arg1: Sequence<Z>): Sequence<Tuple5<A, B,
    C, D, Z>> = arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, C, D, Z>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple5<A, B, C, D, Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> Sequence<Tuple5<A, B, C, D, E>>.product(arg1: Sequence<Z>):
    Sequence<Tuple6<A, B, C, D, E, Z>> = arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, C, D, E, Z>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple6<A, B, C, D, E, Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> Sequence<Tuple6<A, B, C, D, E, FF>>.product(arg1: Sequence<Z>):
    Sequence<Tuple7<A, B, C, D, E, FF, Z>> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, C, D, E, FF, Z>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple7<A, B, C, D, E, FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> Sequence<Tuple7<A, B, C, D, E, FF, G>>.product(arg1: Sequence<Z>):
    Sequence<Tuple8<A, B, C, D, E, FF, G, Z>> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, C, D, E, FF, G, Z>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
}

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> Sequence<Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Sequence<Z>): Sequence<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, C, D, E, FF, G, H, Z>(arrow.core.SequenceK(arg1))
    as kotlin.sequences.Sequence<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
}

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> Sequence<Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Sequence<Z>): Sequence<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@product).product<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<arrow.core.Tuple10<A, B, C, D, E,
    FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupled(arg0: Sequence<A>, arg1: Sequence<B>): Sequence<Tuple2<A, B>> =
    arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupledN(arg0: Sequence<A>, arg1: Sequence<B>): Sequence<Tuple2<A, B>> =
    arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>
): Sequence<Tuple3<A, B, C>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B,
    C>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2)) as
    kotlin.sequences.Sequence<arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>
): Sequence<Tuple3<A, B, C>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B,
    C>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2)) as
    kotlin.sequences.Sequence<arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>
): Sequence<Tuple4<A, B, C, D>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C,
    D>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3))
    as kotlin.sequences.Sequence<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>
): Sequence<Tuple4<A, B, C, D>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C,
    D>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3))
    as kotlin.sequences.Sequence<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>
): Sequence<Tuple5<A, B, C, D, E>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C, D,
    E>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4))
    as kotlin.sequences.Sequence<arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>
): Sequence<Tuple5<A, B, C, D, E>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C, D,
    E>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4))
    as kotlin.sequences.Sequence<arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>
): Sequence<Tuple6<A, B, C, D, E, FF>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C, D, E,
    FF>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5))
    as kotlin.sequences.Sequence<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>
): Sequence<Tuple6<A, B, C, D, E, FF>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C, D, E,
    FF>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5))
    as kotlin.sequences.Sequence<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>
): Sequence<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C, D, E, FF,
    G>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6))
    as kotlin.sequences.Sequence<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>
): Sequence<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C, D, E, FF,
    G>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6))
    as kotlin.sequences.Sequence<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>
): Sequence<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C, D, E, FF, G,
    H>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7))
    as kotlin.sequences.Sequence<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>
): Sequence<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C, D, E, FF, G,
    H>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7))
    as kotlin.sequences.Sequence<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>
): Sequence<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C, D, E, FF, G, H,
    I>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8))
    as kotlin.sequences.Sequence<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>
): Sequence<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H,
    I>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8))
    as kotlin.sequences.Sequence<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.extensions.sequence.apply.Sequence.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>,
  arg9: Sequence<J>
): Sequence<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8), arrow.core.SequenceK(arg9))
    as kotlin.sequences.Sequence<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.extensions.sequence.apply.Sequence.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Sequence<C>,
  arg3: Sequence<D>,
  arg4: Sequence<E>,
  arg5: Sequence<FF>,
  arg6: Sequence<G>,
  arg7: Sequence<H>,
  arg8: Sequence<I>,
  arg9: Sequence<J>
): Sequence<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.sequence.apply.Sequence
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arrow.core.SequenceK(arg2), arrow.core.SequenceK(arg3), arrow.core.SequenceK(arg4), arrow.core.SequenceK(arg5), arrow.core.SequenceK(arg6), arrow.core.SequenceK(arg7), arrow.core.SequenceK(arg8), arrow.core.SequenceK(arg9))
    as kotlin.sequences.Sequence<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "followedBy(arg1)",
  "arrow.core.followedBy"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.followedBy(arg1: Sequence<B>): Sequence<B> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@followedBy).followedBy<A, B>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apTap(arg1)",
  "arrow.core.apTap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.apTap(arg1: Sequence<B>): Sequence<A> =
    arrow.core.extensions.sequence.apply.Sequence.apply().run {
  arrow.core.SequenceK(this@apTap).apTap<A, B>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: SequenceKApply = object : arrow.core.extensions.SequenceKApply {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun apply(): SequenceKApply = apply_singleton}
