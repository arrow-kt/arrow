package arrow.core.extensions.function1.monoid

import arrow.core.Function1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Monoid
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(MB)",
  "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Collection<Function1<A, B>>.combineAll(MB: Monoid<B>): Function1<A, B> =
    arrow.core.Function1.monoid<A, B>(MB).run {
  this@combineAll.combineAll() as arrow.core.Function1<A, B>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(MB, arg0)",
  "arrow.core.Function1.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> combineAll(MB: Monoid<B>, arg0: List<Function1<A, B>>): Function1<A, B> =
    arrow.core.Function1
   .monoid<A, B>(MB)
   .combineAll(arg0) as arrow.core.Function1<A, B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B> Companion.monoid(MB: Monoid<B>): Function1Monoid<A, B> = object :
    arrow.core.extensions.Function1Monoid<A, B> { override fun MB(): arrow.typeclasses.Monoid<B> =
    MB }
