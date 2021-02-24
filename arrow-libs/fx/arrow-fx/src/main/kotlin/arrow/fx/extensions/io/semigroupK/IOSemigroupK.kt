package arrow.fx.extensions.io.semigroupK

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: IOSemigroupK = object : arrow.fx.extensions.IOSemigroupK {}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.combineK(arg1: Kind<ForIO, A>): IO<A> = arrow.fx.IO.semigroupK().run {
  this@combineK.combineK<A>(arg1) as arrow.fx.IO<A>
}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> algebra(): Semigroup<Kind<ForIO, A>> = arrow.fx.IO
  .semigroupK()
  .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.fx.ForIO, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.semigroupK(): IOSemigroupK = semigroupK_singleton
