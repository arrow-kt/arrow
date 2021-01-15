package arrow.core.extensions.function1.contravariant

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Contravariant
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
internal val contravariant_singleton: Function1Contravariant<Any?> = object :
    Function1Contravariant<Any?> {}

@JvmName("contramap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "contramap(arg1)",
  "arrow.core.contramap"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B> Kind<Conested<ForFunction1, O>, A>.contramap(arg1: Function1<B, A>):
    Kind<Conested<ForFunction1, O>, B> = arrow.core.Function1.contravariant<O>().run {
  this@contramap.contramap<A, B>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, B>
}

@JvmName("lift1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lift(arg0)",
  "arrow.core.Function1.lift"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Conested<ForFunction1, O>, B>,
    Kind<Conested<ForFunction1, O>, A>> = arrow.core.Function1
   .contravariant<O>()
   .lift<A, B>(arg0) as
    kotlin.Function1<arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, B>,
    arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, A>>

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "imap(arg1, arg2)",
  "arrow.core.imap"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B> Kind<Conested<ForFunction1, O>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
    Kind<Conested<ForFunction1, O>, B> = arrow.core.Function1.contravariant<O>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1,
    O>, B>
}

@JvmName("narrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "narrow()",
  "arrow.core.narrow"
  ),
  DeprecationLevel.WARNING
)
fun <O, A, B : A> Kind<Conested<ForFunction1, O>, A>.narrow(): Kind<Conested<ForFunction1, O>, B> =
    arrow.core.Function1.contravariant<O>().run {
  this@narrow.narrow<A, B>() as arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>,
    B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <O> Companion.contravariant(): Function1Contravariant<O> = contravariant_singleton as
    arrow.core.extensions.Function1Contravariant<O>
