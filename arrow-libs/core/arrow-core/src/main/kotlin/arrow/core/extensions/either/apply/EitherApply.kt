package arrow.core.extensions.either.apply

import arrow.Kind
import arrow.core.Either
import arrow.core.ap as _ap
import arrow.core.apEval as _apEval
import arrow.core.map2 as _map2
import arrow.core.product as _product
import arrow.core.flatMap as _flatMap
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.EitherApply
import arrow.core.fix
import kotlin.Any
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: EitherApply<Any?> = object : EitherApply<Any?> {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ap(arg1)", "arrow.core.ap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.ap(arg1: Kind<Kind<ForEither, L>, Function1<A, B>>):
  Either<L, B> = _ap(arg1)

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("apEval(arg1)", "arrow.core.apEval"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.apEval(
  arg1: Eval<Kind<Kind<ForEither, L>, Function1<A,
    B>>>
): Eval<Kind<Kind<ForEither, L>, B>> =
  fix()._apEval(arg1.map { it.fix() })

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map2Eval(arg1, arg2)", "arrow.core.map2Eval"))
fun <L, A, B, Z> Kind<Kind<ForEither, L>, A>.map2Eval(
  arg1: Eval<Kind<Kind<ForEither, L>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForEither, L>, Z>> =
  arrow.core.Either.apply<L>().run {
    this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
      arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, Z>>
  }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1) { a, b -> arg2(Tuple2(a, b)) }", "arrow.core.mapN", "arrow.core.Tuple2"))
fun <L, A, B, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix()) { a, b -> arg2(Tuple2(a, b)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1) { a, b -> arg2(Tuple2(a, b)) }", "arrow.core.mapN", "arrow.core.Tuple2"))
fun <L, A, B, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix()) { a, b -> arg2(Tuple2(a, b)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c)) }", "arrow.core.mapN", "arrow.core.Tuple3"))
fun <L, A, B, C, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix()) { a, b, c -> arg3(Tuple3(a, b, c)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c)) }", "arrow.core.mapN", "arrow.core.Tuple3"))
fun <L, A, B, C, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix()) { a, b, c -> arg3(Tuple3(a, b, c)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }", "arrow.core.mapN", "arrow.core.Tuple4"))
fun <L, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix()) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }", "arrow.core.mapN", "arrow.core.Tuple4"))
fun <L, A, B, C, D, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix()) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }", "arrow.core.mapN", "arrow.core.Tuple5"))
fun <L, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix()) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }", "arrow.core.mapN", "arrow.core.Tuple5"))
fun <L, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix()) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }", "arrow.core.mapN", "arrow.core.Tuple6"))
fun <L, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix()) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }", "arrow.core.mapN", "arrow.core.Tuple6"))
fun <L, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix()) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }", "arrow.core.mapN", "arrow.core.Tuple7"))
fun <L, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix()) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }", "arrow.core.mapN", "arrow.core.Tuple7"))
fun <L, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix()) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }", "arrow.core.mapN", "arrow.core.Tuple8"))
fun <L, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix()) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }", "arrow.core.mapN", "arrow.core.Tuple8"))
fun <L, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix()) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }", "arrow.core.mapN", "arrow.core.Tuple9"))
fun <L, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix()) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }", "arrow.core.mapN", "arrow.core.Tuple9"))
fun <L, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix()) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }", "arrow.core.mapN", "arrow.core.Tuple10"))
fun <L, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix()) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }", "arrow.core.mapN", "arrow.core.Tuple10"))
fun <L, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Either<L, Z> = Either.mapN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix()) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map2(arg1, arg2)", "arrow.core.map2"))
fun <L, A, B, Z> Kind<Kind<ForEither, L>, A>.map2(
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Either<L, Z> = fix()._map2(arg1.fix(), arg2)

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.product(arg1: Kind<Kind<ForEither, L>, B>): Either<L,
  Tuple2<A, B>> = fix()._product(arg1.fix())

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, Z> Kind<Kind<ForEither, L>, Tuple2<A, B>>.product(arg1: Kind<Kind<ForEither, L>, Z>):
  Either<L, Tuple3<A, B, Z>> = fix()._product(arg1.fix())

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, Z> Kind<Kind<ForEither, L>, Tuple3<A, B, C>>.product(
  arg1: Kind<Kind<ForEither, L>,
    Z>
): Either<L, Tuple4<A, B, C, Z>> = fix()._product(arg1.fix())

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, D, Z> Kind<Kind<ForEither, L>, Tuple4<A, B, C,
  D>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple5<A, B, C, D, Z>> =
  fix()._product(arg1.fix())

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, D, E, Z> Kind<Kind<ForEither, L>, Tuple5<A, B, C, D,
  E>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple6<A, B, C, D, E, Z>> =
  fix()._product(arg1.fix())

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, D, E, FF, Z> Kind<Kind<ForEither, L>, Tuple6<A, B, C, D, E,
  FF>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple7<A, B, C, D, E, FF, Z>> =
  fix()._product(arg1.fix())

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, D, E, FF, G, Z> Kind<Kind<ForEither, L>, Tuple7<A, B, C, D, E, FF,
  G>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple8<A, B, C, D, E, FF, G, Z>> =
  fix()._product(arg1.fix())

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForEither, L>, Tuple8<A, B, C, D, E, FF, G,
  H>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  fix()._product(arg1.fix())

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <L, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForEither, L>, Tuple9<A, B, C, D, E, FF, G, H,
  I>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple10<A, B, C, D, E, FF, G, H, I,
  Z>> = fix()._product(arg1.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1)", "arrow.core.tupledN"))
fun <L, A, B> tupled(arg0: Kind<Kind<ForEither, L>, A>, arg1: Kind<Kind<ForEither, L>, B>):
  Either<L, Tuple2<A, B>> = Either.tupledN(arg0.fix(), arg1.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1)", "arrow.core.tupledN"))
fun <L, A, B> tupledN(arg0: Kind<Kind<ForEither, L>, A>, arg1: Kind<Kind<ForEither, L>, B>):
  Either<L, Tuple2<A, B>> = Either.tupledN(arg0.fix(), arg1.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2)", "arrow.core.tupledN"))
fun <L, A, B, C> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>
): Either<L, Tuple3<A, B, C>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2)", "arrow.core.tupledN"))
fun <L, A, B, C> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>
): Either<L, Tuple3<A, B, C>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3)", "arrow.core.tupledN"))
fun <L, A, B, C, D> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>
): Either<L, Tuple4<A, B, C, D>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3)", "arrow.core.tupledN"))
fun <L, A, B, C, D> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>
): Either<L, Tuple4<A, B, C, D>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg4)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>
): Either<L, Tuple5<A, B, C, D, E>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg4)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>
): Either<L, Tuple5<A, B, C, D, E>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>
): Either<L, Tuple6<A, B, C, D, E, FF>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>
): Either<L, Tuple6<A, B, C, D, E, FF>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>
): Either<L, Tuple7<A, B, C, D, E, FF, G>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>
): Either<L, Tuple7<A, B, C, D, E, FF, G>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6, arg7)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>
): Either<L, Tuple8<A, B, C, D, E, FF, G, H>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6, arg7)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>
): Either<L, Tuple8<A, B, C, D, E, FF, G, H>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6, arg7, arg8)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>
): Either<L, Tuple9<A, B, C, D, E, FF, G, H, I>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6, arg7, arg8)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>
): Either<L, Tuple9<A, B, C, D, E, FF, G, H, I>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix())

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6, arg7, arg8, arg9)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>
): Either<L, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix())

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tupledN(arg0, arg1, arg2, arg3, arg5, arg6, arg7, arg8, arg9)", "arrow.core.tupledN"))
fun <L, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>
): Either<L, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = Either.tupledN(arg0.fix(), arg1.fix(), arg2.fix(), arg3.fix(), arg4.fix(), arg5.fix(), arg6.fix(), arg7.fix(), arg8.fix(), arg9.fix())

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { arg1 }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.followedBy(arg1: Kind<Kind<ForEither, L>, B>): Either<L, B> =
  _flatMap { arg1.fix() }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(this, fb) { left, _ -> left }", "arrow.core.mapN"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.apTap(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  Either.mapN(fix(), arg1.fix()) { left, _ -> left }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Apply typeclasses is deprecated. Use concrete methods on Either")
inline fun <L> Companion.apply(): EitherApply<L> = apply_singleton as
  arrow.core.extensions.EitherApply<L>
