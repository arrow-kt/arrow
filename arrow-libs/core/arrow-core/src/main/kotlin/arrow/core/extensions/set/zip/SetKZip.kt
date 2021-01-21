package arrow.core.extensions.set.zip

import arrow.core.Tuple2
import arrow.core.toTuple2
import kotlin.Function2
import kotlin.Suppress
import kotlin.collections.zip as _zip
import kotlin.jvm.JvmName

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "this.zip(arg1, ::Tuple2)",
    "arrow.core.zip", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Set<A>.zip(arg1: Set<B>): Set<Tuple2<A, B>> =
  _zip(arg1).map { it.toTuple2() }.toSet()

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "this.zip(arg1, arg2)",
    "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Set<A>.zipWith(arg1: Set<B>, arg2: Function2<A, B, C>): Set<C> =
  _zip(arg1, arg2).toSet()
