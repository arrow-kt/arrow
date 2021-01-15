package arrow.core.extensions.function1.decidable

import arrow.Kind
import arrow.core.Either
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Decidable
import arrow.typeclasses.Conested
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Function1<Z, Either<A, B>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, Z>(arg0, arg1, arg2) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Function1<Z, Either<A, Either<B, C>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, Z>(arg0, arg1, arg2, arg3) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Function1<Z, Either<A, Either<B, Either<C, D>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Function1<Z, Either<A, Either<B, Either<C, Either<D, E>>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Function1<Z, Either<A, Either<B, Either<C, Either<D, Either<E, FF>>>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Function1<Z, Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, G>>>>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Kind<Conested<ForFunction1, O>, H>,
  arg8: Function1<Z, Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, Either<G,
      H>>>>>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, I, Z> choose(
  MOOO: Monoid<O>,
  arg0: Kind<Conested<ForFunction1, O>, A>,
  arg1: Kind<Conested<ForFunction1, O>, B>,
  arg2: Kind<Conested<ForFunction1, O>, C>,
  arg3: Kind<Conested<ForFunction1, O>, D>,
  arg4: Kind<Conested<ForFunction1, O>, E>,
  arg5: Kind<Conested<ForFunction1, O>, FF>,
  arg6: Kind<Conested<ForFunction1, O>, G>,
  arg7: Kind<Conested<ForFunction1, O>, H>,
  arg8: Kind<Conested<ForFunction1, O>, I>,
  arg9: Function1<Z, Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, Either<G,
      Either<H, I>>>>>>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, E, FF, G, H, I,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@JvmName("choose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "choose(MOOO, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.Function1.choose"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B, C, D, E, FF, G, H, I, J, Z> choose(
  MOOO: Monoid<O>,
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
  arg10: Function1<Z, Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, Either<G,
      Either<H, Either<I, J>>>>>>>>>>
): Kind<Conested<ForFunction1, O>, Z> = arrow.core.Function1
   .decidable<O>(MOOO)
   .choose<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, Z>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <O> Companion.decidable(MOOO: Monoid<O>): Function1Decidable<O> = object :
    arrow.core.extensions.Function1Decidable<O> { override fun MOOO(): arrow.typeclasses.Monoid<O> =
    MOOO }
