package arrow.core.extensions.list.functorFilter

import arrow.core.Option
import arrow.core.extensions.ListKFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.collections.filter as _filter
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapNotNull { arg1(it).orNull() }"))
fun <A, B> List<A>.filterMap(arg1: Function1<A, Option<B>>): List<B> =
  mapNotNull { arg1(it).orNull() }

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapNotNull { it.orNull() }"))
fun <A> List<Option<A>>.flattenOption(): List<A> =
  mapNotNull { it.orNull() }

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("filter(arg1)"))
fun <A> List<A>.filter(arg1: Function1<A, Boolean>): List<A> =
  _filter(arg1)

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("filter(arg1::isInstance).map { arg1.cast(it) }"))
fun <A, B> List<A>.filterIsInstance(arg1: Class<B>): List<B> =
  _filter(arg1::isInstance).map { arg1.cast(it) }

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: ListKFunctorFilter = object :
    arrow.core.extensions.ListKFunctorFilter {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Functor typeclasses is deprecated. Use concrete methods on List")
  inline fun functorFilter(): ListKFunctorFilter = functorFilter_singleton}
