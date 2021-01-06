package arrow.core.extensions.listk.alternative

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.extensions.ListKAlternative
import kotlin.Boolean
import kotlin.Function0
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val alternative_singleton: ListKAlternative = object :
    arrow.core.extensions.ListKAlternative {}

@JvmName("some")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This method results in StackOverflow")
fun <A> Kind<ForListK, A>.some(): ListK<SequenceK<A>> = arrow.core.ListK.alternative().run {
  this@some.some<A>() as arrow.core.ListK<arrow.core.SequenceK<A>>
}

@JvmName("many")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This method results in StackOverflow")
fun <A> Kind<ForListK, A>.many(): ListK<SequenceK<A>> = arrow.core.ListK.alternative().run {
  this@many.many<A>() as arrow.core.ListK<arrow.core.SequenceK<A>>
}

@JvmName("alt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1"))
infix fun <A> Kind<ForListK, A>.alt(arg1: Kind<ForListK, A>): ListK<A> =
    arrow.core.ListK.alternative().run {
  this@alt.alt<A>(arg1) as arrow.core.ListK<A>
}

@JvmName("orElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1"))
fun <A> Kind<ForListK, A>.orElse(arg1: Kind<ForListK, A>): ListK<A> =
    arrow.core.ListK.alternative().run {
  this@orElse.orElse<A>(arg1) as arrow.core.ListK<A>
}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1"))
fun <A> Kind<ForListK, A>.combineK(arg1: Kind<ForListK, A>): ListK<A> =
    arrow.core.ListK.alternative().run {
  this@combineK.combineK<A>(arg1) as arrow.core.ListK<A>
}

@JvmName("optional")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(::Some) + listOf(None)", "arrow.core.Some", "arrow.core.None"))
fun <A> Kind<ForListK, A>.optional(): ListK<Option<A>> = arrow.core.ListK.alternative().run {
  this@optional.optional<A>() as arrow.core.ListK<arrow.core.Option<A>>
}

@JvmName("guard")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("if (arg0) listOf(Unit) else emptyList()"))
fun guard(arg0: Boolean): ListK<Unit> = arrow.core.ListK
   .alternative()
   .guard(arg0) as arrow.core.ListK<kotlin.Unit>

@JvmName("lazyOrElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this + arg1()"))
fun <A> Kind<ForListK, A>.lazyOrElse(arg1: Function0<Kind<ForListK, A>>): ListK<A> =
    arrow.core.ListK.alternative().run {
  this@lazyOrElse.lazyOrElse<A>(arg1) as arrow.core.ListK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Alternative typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.alternative(): ListKAlternative = alternative_singleton
