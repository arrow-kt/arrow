package arrow.core.extensions.id.selective

import arrow.Kind
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdSelective
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
internal val selective_singleton: IdSelective = object : arrow.core.extensions.IdSelective {}

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
fun <A, B> Kind<ForId, Either<A, B>>.select(arg1: Kind<ForId, Function1<A, B>>): Id<B> =
    arrow.core.Id.selective().run {
  this@select.select<A, B>(arg1) as arrow.core.Id<B>
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
fun <A, B, C> Kind<ForId, Either<A, B>>.branch(
  arg1: Kind<ForId, Function1<A, C>>,
  arg2: Kind<ForId, Function1<B, C>>
): Id<C> = arrow.core.Id.selective().run {
  this@branch.branch<A, B, C>(arg1, arg2) as arrow.core.Id<C>
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
fun <A> Kind<ForId, Boolean>.whenS(arg1: Kind<ForId, Function0<Unit>>): Id<Unit> =
    arrow.core.Id.selective().run {
  this@whenS.whenS<A>(arg1) as arrow.core.Id<kotlin.Unit>
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
fun <A> Kind<ForId, Boolean>.ifS(arg1: Kind<ForId, A>, arg2: Kind<ForId, A>): Id<A> =
    arrow.core.Id.selective().run {
  this@ifS.ifS<A>(arg1, arg2) as arrow.core.Id<A>
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
fun <A> Kind<ForId, Boolean>.orS(arg1: Kind<ForId, Boolean>): Id<Boolean> =
    arrow.core.Id.selective().run {
  this@orS.orS<A>(arg1) as arrow.core.Id<kotlin.Boolean>
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
fun <A> Kind<ForId, Boolean>.andS(arg1: Kind<ForId, Boolean>): Id<Boolean> =
    arrow.core.Id.selective().run {
  this@andS.andS<A>(arg1) as arrow.core.Id<kotlin.Boolean>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.selective(): IdSelective = selective_singleton
