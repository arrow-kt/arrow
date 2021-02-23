package arrow.fx.extensions.resource.monoid

import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceMonoid
import arrow.fx.typeclasses.Bracket
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
@Deprecated(IODeprecation)
fun <F, E, A> Collection<Resource<F, E, A>>.combineAll(MR: Monoid<A>, BR: Bracket<F, E>):
  Resource<F, E, A> = arrow.fx.Resource.monoid<F, E, A>(MR, BR).run {
    this@combineAll.combineAll() as arrow.fx.Resource<F, E, A>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> combineAll(
  MR: Monoid<A>,
  BR: Bracket<F, E>,
  arg0: List<Resource<F, E, A>>
): Resource<F, E, A> = arrow.fx.Resource
  .monoid<F, E, A>(MR, BR)
  .combineAll(arg0) as arrow.fx.Resource<F, E, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E, A> Companion.monoid(MR: Monoid<A>, BR: Bracket<F, E>): ResourceMonoid<F, E, A> =
  object : arrow.fx.extensions.ResourceMonoid<F, E, A> {
    override fun MR():
      arrow.typeclasses.Monoid<A> = MR

    override fun BR(): arrow.fx.typeclasses.Bracket<F, E> = BR
  }
