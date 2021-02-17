package arrow.core.extensions.option.selective

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionSelective
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val selective_singleton: OptionSelective = object : arrow.core.extensions.OptionSelective
{}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "select(arg1)",
    "arrow.core.select"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, Either<A, B>>.select(arg1: Kind<ForOption, Function1<A, B>>): Option<B> =
  arrow.core.Option.selective().run {
    this@select.select<A, B>(arg1) as arrow.core.Option<B>
  }

@JvmName("branch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "branch(arg1, arg2)",
    "arrow.core.branch"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForOption, Either<A, B>>.branch(
  arg1: Kind<ForOption, Function1<A, C>>,
  arg2: Kind<ForOption, Function1<B, C>>
): Option<C> = arrow.core.Option.selective().run {
  this@branch.branch<A, B, C>(arg1, arg2) as arrow.core.Option<C>
}

@JvmName("whenS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "whenS(arg1)",
    "arrow.core.whenS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, Boolean>.whenS(arg1: Kind<ForOption, Function0<Unit>>): Option<Unit> =
  arrow.core.Option.selective().run {
    this@whenS.whenS<A>(arg1) as arrow.core.Option<kotlin.Unit>
  }

@JvmName("ifS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "ifS(arg1, arg2)",
    "arrow.core.ifS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, Boolean>.ifS(arg1: Kind<ForOption, A>, arg2: Kind<ForOption, A>): Option<A> =
  arrow.core.Option.selective().run {
    this@ifS.ifS<A>(arg1, arg2) as arrow.core.Option<A>
  }

@JvmName("orS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "orS(arg1)",
    "arrow.core.orS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, Boolean>.orS(arg1: Kind<ForOption, Boolean>): Option<Boolean> =
  arrow.core.Option.selective().run {
    this@orS.orS<A>(arg1) as arrow.core.Option<kotlin.Boolean>
  }

@JvmName("andS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "andS(arg1)",
    "arrow.core.andS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, Boolean>.andS(arg1: Kind<ForOption, Boolean>): Option<Boolean> =
  arrow.core.Option.selective().run {
    this@andS.andS<A>(arg1) as arrow.core.Option<kotlin.Boolean>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Selective typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.selective(): OptionSelective = selective_singleton
