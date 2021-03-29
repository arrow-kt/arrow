package arrow.core.extensions.andthen.contravariant

import arrow.Kind
import arrow.core.AndThen.Companion
import arrow.core.AndThenDeprecation
import arrow.core.ForAndThen
import arrow.core.extensions.AndThenContravariant
import arrow.typeclasses.Conested
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val contravariant_singleton: AndThenContravariant<Any?> = object :
  AndThenContravariant<Any?> {}

@JvmName("contramap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <O, A, B> Kind<Conested<ForAndThen, O>, A>.contramap(arg1: Function1<B, A>):
  Kind<Conested<ForAndThen, O>, B> = arrow.core.AndThen.contravariant<O>().run {
    this@contramap.contramap<A, B>(arg1) as
      arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForAndThen, O>, B>
  }

@JvmName("lift1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <O, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Conested<ForAndThen, O>, B>,
  Kind<Conested<ForAndThen, O>, A>> = arrow.core.AndThen
  .contravariant<O>()
  .lift<A, B>(arg0) as
  kotlin.Function1<arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForAndThen, O>, B>,
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForAndThen, O>, A>>

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <O, A, B> Kind<Conested<ForAndThen, O>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
  Kind<Conested<ForAndThen, O>, B> = arrow.core.AndThen.contravariant<O>().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForAndThen,
        O>, B>
  }

@JvmName("narrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <O, A, B : A> Kind<Conested<ForAndThen, O>, A>.narrow(): Kind<Conested<ForAndThen, O>, B> =
  arrow.core.AndThen.contravariant<O>().run {
    this@narrow.narrow<A, B>() as arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForAndThen, O>, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(AndThenDeprecation)
inline fun <O> Companion.contravariant(): AndThenContravariant<O> = contravariant_singleton as
  arrow.core.extensions.AndThenContravariant<O>
