package arrow.core.extensions.nonemptylist.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.NonEmptyListApply
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: NonEmptyListApply = object : arrow.core.extensions.NonEmptyListApply
{}

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
    "fix().ap<B>(arg1)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.ap(arg1: Kind<ForNonEmptyList, Function1<A, B>>):
  NonEmptyList<B> = arrow.core.NonEmptyList.apply().run {
    this@ap.ap<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
    "arg1.map<NonEmptyList<B>> { this.ap<A, B>(it.fix<(A) -> B>()) }",
    "arrow.core.fix",
    "arrow.core.k"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.apEval(arg1: Eval<Kind<ForNonEmptyList, Function1<A, B>>>):
  Eval<Kind<ForNonEmptyList, B>> = arrow.core.NonEmptyList.apply().run {
    this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.core.ForNonEmptyList, B>>
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
    "apEval(arg1.map<NonEmptyList<(A) -> B>> { it.fix().map { b -> { a: A -> arg2(Tuple2(a, b)) } } })",
    "arrow.core.map2Eval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Kind<ForNonEmptyList, A>.map2Eval(
  arg1: Eval<Kind<ForNonEmptyList, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<ForNonEmptyList, Z>> =
  arrow.core.NonEmptyList.apply().run {
    this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
      arrow.core.Eval<arrow.Kind<arrow.core.ForNonEmptyList, Z>>
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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix()) { a, b -> arg2(Tuple2(a, b)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix()) { a, b -> arg2(Tuple2(a, b)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix()) { a, b, c -> arg3(Tuple3(a, b, c)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix()) { a, b, c -> arg3(Tuple3(a, b, c)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix()) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix()) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix()) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix()) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix()) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix()) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix()) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix()) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix()) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix()) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix()) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix()) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix()) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as
  arrow.core.NonEmptyList<Z>

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
    "NonEmptyList.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix()) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }",
    "arrow.core.NonEmptyList",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
  .apply()
  .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as
  arrow.core.NonEmptyList<Z>

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
    "flatMap<A> { a -> arg1.map<B> { b -> arg2(Tuple2(a, b)) }}",
    "arrow.core.NonEmptyList"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Kind<ForNonEmptyList, A>.map2(
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList.apply().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.NonEmptyList<Z>
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
    "ap(arg1.map<(A) -> Tuple2<A, B>> { b -> { a -> Tuple2<A, B>(a, b) } })",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.product(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<Tuple2<A,
    B>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>
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
    "flatMap<Tuple3<A, B, Z>> { a -> arg1.map<Tuple3<A, B, Z>> { z -> Tuple3<A, B, Z>(a.a, a.b, z) }}",
    "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Kind<ForNonEmptyList, Tuple2<A, B>>.product(arg1: Kind<ForNonEmptyList, Z>):
  NonEmptyList<Tuple3<A, B, Z>> = arrow.core.NonEmptyList.apply().run {
    this@product.product<A, B, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple3<A, B, Z>>
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
    "flatMap<Tuple4<A, B, C, Z>> { a -> arg1.map<Tuple4<A, B, C, Z>> { z -> Tuple4<A, B, C, Z>(a.a, a.b, a.c, z) }}",
    "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> Kind<ForNonEmptyList, Tuple3<A, B, C>>.product(arg1: Kind<ForNonEmptyList, Z>):
  NonEmptyList<Tuple4<A, B, C, Z>> = arrow.core.NonEmptyList.apply().run {
    this@product.product<A, B, C, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple4<A, B, C, Z>>
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
    "flatMap<Tuple5<A, B, C, D, Z>> { a -> arg1.map<Tuple5<A, B, C, D, Z>> { z -> Tuple5<A, B, C, D, Z>(a.a, a.b, a.c, a.d, z) }}",
    "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> Kind<ForNonEmptyList, Tuple4<A, B, C, D>>.product(
  arg1: Kind<ForNonEmptyList, Z>
): NonEmptyList<Tuple5<A, B, C, D, Z>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple5<A, B, C, D,
      Z>>
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
    "flatMap<Tuple6<A, B, C, D, E, Z>> { a -> arg1.map<Tuple6<A, B, C, D, E, Z>> { z -> Tuple6<A, B, C, D, E, Z>(a.a, a.b, a.c, a.d, a.e, z) }}",
    "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> Kind<ForNonEmptyList, Tuple5<A, B, C, D,
    E>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple6<A, B, C, D, E, Z>> =
  arrow.core.NonEmptyList.apply().run {
    this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple6<A, B, C,
        D, E, Z>>
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
    "flatMap<Tuple7<A, B, C, D, E, FF, Z>> { a -> arg1.map<Tuple7<A, B, C, D, E, FF, Z>> { z -> Tuple7<A, B, C, D, E, FF, Z>(a.a, a.b, a.c, a.d, a.e, a.f, z) }}",
    "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> Kind<ForNonEmptyList, Tuple6<A, B, C, D, E,
    FF>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple7<A, B, C, D, E, FF, Z>> =
  arrow.core.NonEmptyList.apply().run {
    this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple7<A,
        B, C, D, E, FF, Z>>
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
    "flatMap<Tuple8<A, B, C, D, E, FF, G, Z>> { a -> arg1.map<Tuple8<A, B, C, D, E, FF, G, Z>> { z -> Tuple8<A, B, C, D, E, FF, G, Z>(a.a, a.b, a.c, a.d, a.e, a.f, a.g, z) }}",
    "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> Kind<ForNonEmptyList, Tuple7<A, B, C, D, E, FF,
    G>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple8<A, B, C, D, E, FF, G, Z>> =
  arrow.core.NonEmptyList.apply().run {
    this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as
      arrow.core.NonEmptyList<arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
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
    "flatMap<Tuple9<A, B, C, D, E, FF, G, H, Z>> { a -> arg1.map<Tuple9<A, B, C, D, E, FF, G, H, Z>> { z -> Tuple9<A, B, C, D, E, FF, G, H, Z>(a.a, a.b, a.c, a.d, a.e, a.f, a.g, a.h, z) }}",
    "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> Kind<ForNonEmptyList, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.NonEmptyList.apply().run {
    this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as
      arrow.core.NonEmptyList<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
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
    "flatMap<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> { a -> arg1.map<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> { z -> Tuple10<A, B, C, D, E, FF, G, H, I, Z>(a.a, a.b, a.c, a.d, a.e, a.f, a.g, a.h, a.i, z) }}",
    "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<ForNonEmptyList, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple10<A, B, C, D, E, FF, G, H, I,
    Z>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as
    arrow.core.NonEmptyList<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
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
    "NonEmptyList.mapN<A, B, Tuple2<A, B>>(arg0.fix(), arg1.fix()) { a, b -> Tuple2(a, b) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple2",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupled(arg0: Kind<ForNonEmptyList, A>, arg1: Kind<ForNonEmptyList, B>):
  NonEmptyList<Tuple2<A, B>> = arrow.core.NonEmptyList
    .apply()
    .tupled<A, B>(arg0, arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>

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
    "NonEmptyList.mapN<A, B, Tuple2<A, B>>(arg0.fix(), arg1.fix()) { a, b -> Tuple2(a, b) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple2",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupledN(arg0: Kind<ForNonEmptyList, A>, arg1: Kind<ForNonEmptyList, B>):
  NonEmptyList<Tuple2<A, B>> = arrow.core.NonEmptyList
    .apply()
    .tupledN<A, B>(arg0, arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>

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
    "NonEmptyList.mapN<A, B, C, Tuple3<A, B, C>>(arg0.fix(), arg1.fix(), arg2.fix()) { a, b, c -> Tuple3(a, b, c) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple3",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>
): NonEmptyList<Tuple3<A, B, C>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.NonEmptyList<arrow.core.Tuple3<A, B, C>>

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
    "NonEmptyList.mapN<A, B, C, Tuple3<A, B, C>>(arg0.fix(), arg1.fix(), arg2.fix()) { a, b, c -> Tuple3(a, b, c) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple3",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>
): NonEmptyList<Tuple3<A, B, C>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.NonEmptyList<arrow.core.Tuple3<A, B, C>>

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
    "NonEmptyList.mapN<A, B, C, D, Tuple4<A, B, C, D>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix()) { a, b, c, d -> Tuple4(a, b, c, d) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple4",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>
): NonEmptyList<Tuple4<A, B, C, D>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<arrow.core.Tuple4<A, B, C,
    D>>

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
    "NonEmptyList.mapN<A, B, C, D, Tuple4<A, B, C, D>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix()) { a, b, c, d -> Tuple4(a, b, c, d) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple4",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>
): NonEmptyList<Tuple4<A, B, C, D>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<arrow.core.Tuple4<A, B,
    C, D>>

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
    "NonEmptyList.mapN<A, B, C, D, E, Tuple5<A, B, C, D, E>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix()) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple5",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>
): NonEmptyList<Tuple5<A, B, C, D, E>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as
  arrow.core.NonEmptyList<arrow.core.Tuple5<A, B, C, D, E>>

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
    "NonEmptyList.mapN<A, B, C, D, E, Tuple5<A, B, C, D, E>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix()) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple5",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>
): NonEmptyList<Tuple5<A, B, C, D, E>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as
  arrow.core.NonEmptyList<arrow.core.Tuple5<A, B, C, D, E>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, Tuple6<A, B, C, D, E, FF>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix()) { a, b, c, d, e, f -> Tuple6(a, b, c, d, e, f) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple6",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>
): NonEmptyList<Tuple6<A, B, C, D, E, FF>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
  arrow.core.NonEmptyList<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, Tuple6<A, B, C, D, E, FF>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix()) { a, b, c, d, e, f -> Tuple6(a, b, c, d, e, f) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple6",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>
): NonEmptyList<Tuple6<A, B, C, D, E, FF>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
  arrow.core.NonEmptyList<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, Tuple7<A, B, C, D, E, FF, G>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix()) { a, b, c, d, e, f, g -> Tuple7(a, b, c, d, e, f, g) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple7",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>
): NonEmptyList<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
  arrow.core.NonEmptyList<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, Tuple7<A, B, C, D, E, FF, G>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix()) { a, b, c, d, e, f, g -> Tuple7(a, b, c, d, e, f, g) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple7",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>
): NonEmptyList<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
  arrow.core.NonEmptyList<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, H, Tuple8<A, B, C, D, E, FF, G, H>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix()) { a, b, c, d, e, f, g, h -> Tuple8(a, b, c, d, e, f, g, h) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple8",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>
): NonEmptyList<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.NonEmptyList<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, H, Tuple8<A, B, C, D, E, FF, G, H>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix()) { a, b, c, d, e, f, g, h -> Tuple8(a, b, c, d, e, f, g, h) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple8",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>
): NonEmptyList<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.NonEmptyList<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, H, I, Tuple9<A, B, C, D, E, FF, G, H, I>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix()) { a, b, c, d, e, f, g, h, i -> Tuple9(a, b, c, d, e, f, g, h, i) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple9",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>
): NonEmptyList<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.NonEmptyList<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, H, I, Tuple9<A, B, C, D, E, FF, G, H, I>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix()) { a, b, c, d, e, f, g, h, i -> Tuple9(a, b, c, d, e, f, g, h, i) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple9",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>
): NonEmptyList<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.NonEmptyList<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, H, I, J, Tuple10<A, B, C, D, E, FF, G, H, I, J>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix()) { a, b, c, d, e, f, g, h, i, j -> Tuple10(a, b, c, d, e, f, g, h, i, j) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple10",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>
): NonEmptyList<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.NonEmptyList
  .apply()
  .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
  arrow.core.NonEmptyList<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
    "NonEmptyList.mapN<A, B, C, D, E, FF, G, H, I, J, Tuple10<A, B, C, D, E, FF, G, H, I, J>>(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix()) { a, b, c, d, e, f, g, h, i, j -> Tuple10(a, b, c, d, e, f, g, h, i, j) }",
    "arrow.core.NonEmptyList",
    "arrow.core.Tuple10",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>
): NonEmptyList<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.NonEmptyList
  .apply()
  .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
  arrow.core.NonEmptyList<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
    "flatMap { arg1 }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.followedBy(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<B> =
  arrow.core.NonEmptyList.apply().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
    "NonEmptyList.mapN(this.fix(), arg1.fix()) { left, _ -> left }",
    "arrow.core.fix",
    "arrow.core.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.apTap(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<A> =
  arrow.core.NonEmptyList.apply().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.NonEmptyList<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Apply typeclass is deprecated. Use concrete methods on NonEmptyList",
  level = DeprecationLevel.WARNING
)
inline fun Companion.apply(): NonEmptyListApply = apply_singleton
