package arrow.core.extensions.function1.divide

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.Function1Divide
import arrow.typeclasses.Conested
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Function1<Z, Tuple2<A, B>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, Z>(arg0, arg1, arg2) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B> Kind<Conested<ForFunction1, O>, A>.product(
  MO: Monoid<O>,
  arg1: Kind<Conested<ForFunction1, O>, B>
): Kind<Conested<ForFunction1, O>, Tuple2<A, B>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B>(arg1) as arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1,
    O>, arrow.core.Tuple2<A, B>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C> Kind<Conested<ForFunction1, O>, Tuple2<A, B>>.product(
  MO: Monoid<O>,
  arg1: Kind<Conested<ForFunction1, O>, C>
): Kind<Conested<ForFunction1, O>, Tuple3<A, B, C>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple3<A, B, C>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D> Kind<Conested<ForFunction1, O>, Tuple3<A, B, C>>.product(
  MO: Monoid<O>,
  arg1: Kind<Conested<ForFunction1, O>, D>
): Kind<Conested<ForFunction1, O>, Tuple4<A, B, C, D>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple4<A, B, C,
    D>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E> Kind<Conested<ForFunction1, O>, Tuple4<A, B, C, D>>.product(
  MO: Monoid<O>,
  arg1: Kind<Conested<ForFunction1, O>, E>
): Kind<Conested<ForFunction1, O>, Tuple5<A, B, C, D,
  E>> = arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D, E>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple5<A, B, C, D,
    E>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF> Kind<Conested<ForFunction1, O>, Tuple5<A, B, C, D,
    E>>.product(MO: Monoid<O>, arg1: Kind<Conested<ForFunction1, O>, FF>):
    Kind<Conested<ForFunction1, O>, Tuple6<A, B, C, D, E, FF>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D, E, FF>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple6<A, B, C, D,
    E, FF>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G> Kind<Conested<ForFunction1, O>, Tuple6<A, B, C, D, E,
    FF>>.product(MO: Monoid<O>, arg1: Kind<Conested<ForFunction1, O>, G>):
    Kind<Conested<ForFunction1, O>, Tuple7<A, B, C, D, E, FF, G>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D, E, FF, G>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple7<A, B, C, D,
    E, FF, G>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H> Kind<Conested<ForFunction1, O>, Tuple7<A, B, C, D, E, FF,
    G>>.product(MO: Monoid<O>, arg1: Kind<Conested<ForFunction1, O>, H>):
    Kind<Conested<ForFunction1, O>, Tuple8<A, B, C, D, E, FF, G, H>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D, E, FF, G, H>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple8<A, B, C, D,
    E, FF, G, H>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, I> Kind<Conested<ForFunction1, O>, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(MO: Monoid<O>, arg1: Kind<Conested<ForFunction1, O>, I>):
    Kind<Conested<ForFunction1, O>, Tuple9<A, B, C, D, E, FF, G, H, I>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D, E, FF, G, H, I>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple9<A, B, C, D,
    E, FF, G, H, I>>
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
  "product(MO, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, I, J> Kind<Conested<ForFunction1, O>, Tuple9<A, B, C, D, E, FF, G,
    H, I>>.product(MO: Monoid<O>, arg1: Kind<Conested<ForFunction1, O>, J>):
    Kind<Conested<ForFunction1, O>, Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
    arrow.core.Function1.divide<O>(MO).run {
  this@product.product<A, B, C, D, E, FF, G, H, I, J>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, arrow.core.Tuple10<A, B, C,
    D, E, FF, G, H, I, J>>
}

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Function1<Z, Tuple3<A, B, C>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, Z>(arg0, arg1, arg2, arg3) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Function1<Z, Tuple4<A, B, C, D>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Function1<Z, Tuple5<A, B, C, D, E>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Function1<Z, Tuple6<A, B, C, D, E, FF>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Function1<Z, Tuple7<A, B, C, D, E, FF, G>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Kind<Conested<ForFunction1, O>, H>,
  arg8: Function1<Z, Tuple8<A, B, C, D, E, FF, G, H>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, I, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Kind<Conested<ForFunction1, O>, H>,
  arg8: Kind<Conested<ForFunction1, O>, I>,
  arg9: Function1<Z, Tuple9<A, B, C, D, E, FF, G, H, I>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, E, FF, G, H, I,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("divide")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "divide(MO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.Function1.divide"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, I, J, Z> divide(
  MO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Kind<Conested<ForFunction1, O>, H>,
  arg8: Kind<Conested<ForFunction1, O>, I>,
  arg9: Kind<Conested<ForFunction1, O>, J>,
  arg10: Function1<Z, Tuple10<A, B, C, D, E, FF, G, H, I, J>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .divide<O>(MO)
   .divide<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <O> Companion.divide(MO: Monoid<O>): Function1Divide<O> = object :
    arrow.core.extensions.Function1Divide<O> { override fun MO(): arrow.typeclasses.Monoid<O> = MO }
