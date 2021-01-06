package arrow.core.extensions.list.alternative

import arrow.Kind
import arrow.core.ForListK
import arrow.core.None
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.Some
import arrow.core.extensions.ListKAlternative
import arrow.core.fix
import kotlin.Boolean
import kotlin.Function0
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("some")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This method results in StackOverflow")
fun <A> List<A>.some(): List<SequenceK<A>> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@some).some<A>() as kotlin.collections.List<arrow.core.SequenceK<A>>
}

@JvmName("many")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This method results in StackOverflow")
fun <A> List<A>.many(): List<SequenceK<A>> =
  arrow.core.extensions.list.alternative.List.alternative().run {
    arrow.core.ListK(this@many).many<A>() as kotlin.collections.List<arrow.core.SequenceK<A>>
  }

@JvmName("alt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1"))
infix fun <A> List<A>.alt(arg1: List<A>): List<A> =
  this + arg1

@JvmName("orElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1"))
fun <A> List<A>.orElse(arg1: List<A>): List<A> =
  this + arg1

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1"))
fun <A> List<A>.combineK(arg1: List<A>): List<A> =
  this + arg1

@JvmName("optional")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(::Some) + listOf(None)", "arrow.core.Some", "arrow.core.None"))
fun <A> List<A>.optional(): List<Option<A>> =
  map(::Some) + listOf(None)

@JvmName("guard")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("if (arg0) listOf(Unit) else emptyList()"))
fun guard(arg0: Boolean): List<Unit> =
  if (arg0) listOf(Unit) else emptyList()

@JvmName("lazyOrElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1()"))
fun <A> List<A>.lazyOrElse(arg1: Function0<Kind<ForListK, A>>): List<A> =
  this + arg1().fix()

/**
 * cached extension
 */
@PublishedApi()
internal val alternative_singleton: ListKAlternative = object :
  arrow.core.extensions.ListKAlternative {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Alternative typeclasses is deprecated. Use concrete methods on List")
  inline fun alternative(): ListKAlternative = alternative_singleton
}
