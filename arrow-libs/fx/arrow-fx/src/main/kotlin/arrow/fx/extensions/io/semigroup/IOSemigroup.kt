package arrow.fx.extensions.io.semigroup

import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOSemigroup
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
fun <A> IO<A>.plus(SG: Semigroup<A>, arg1: IO<A>): IO<A> = arrow.fx.IO.semigroup<A>(SG).run {
  this@plus.plus(arg1) as arrow.fx.IO<A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> IO<A>.maybeCombine(SG: Semigroup<A>, arg1: IO<A>): IO<A> =
  arrow.fx.IO.semigroup<A>(SG).run {
    this@maybeCombine.maybeCombine(arg1) as arrow.fx.IO<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <A> Companion.semigroup(SG: Semigroup<A>): IOSemigroup<A> = object :
  arrow.fx.extensions.IOSemigroup<A> { override fun SG(): arrow.typeclasses.Semigroup<A> = SG }
