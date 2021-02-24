package arrow.fx.extensions.resource.semigroup

import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceSemigroup
import arrow.fx.typeclasses.Bracket
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Resource<F, E, A>.plus(
  SR: Semigroup<A>,
  BR: Bracket<F, E>,
  arg1: Resource<F, E, A>
): Resource<F, E, A> = arrow.fx.Resource.semigroup<F, E, A>(SR, BR).run {
  this@plus.plus(arg1) as arrow.fx.Resource<F, E, A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Resource<F, E, A>.maybeCombine(
  SR: Semigroup<A>,
  BR: Bracket<F, E>,
  arg1: Resource<F, E, A>
): Resource<F, E, A> = arrow.fx.Resource.semigroup<F, E, A>(SR, BR).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.fx.Resource<F, E, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E, A> Companion.semigroup(SR: Semigroup<A>, BR: Bracket<F, E>): ResourceSemigroup<F,
  E, A> = object : arrow.fx.extensions.ResourceSemigroup<F, E, A> {
  override fun SR():
    arrow.typeclasses.Semigroup<A> = SR

  override fun BR(): arrow.fx.typeclasses.Bracket<F, E> = BR
}
