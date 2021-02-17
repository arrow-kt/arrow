package arrow.core.extensions.listk.show

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.String
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("show")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ShowDeprecation, ReplaceWith("toString()"))
fun <A> Kind<ForListK, A>.show(SA: Show<A>): String = arrow.core.ListK.show<A>(SA).run {
  this@show.show() as kotlin.String
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <A> Companion.show(SA: Show<A>): ListKShow<A> = object :
  arrow.core.extensions.ListKShow<A> { override fun SA(): arrow.typeclasses.Show<A> = SA }
