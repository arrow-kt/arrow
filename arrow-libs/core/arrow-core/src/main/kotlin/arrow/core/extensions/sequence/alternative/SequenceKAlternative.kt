package arrow.core.extensions.sequence.alternative

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.extensions.SequenceKAlternative
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

@JvmName("some")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "some()",
  "arrow.core.some"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.some(): Sequence<Sequence<A>> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@some).some<A>() as
    kotlin.sequences.Sequence<kotlin.sequences.Sequence<A>>
}

@JvmName("many")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "many()",
  "arrow.core.many"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.many(): Sequence<Sequence<A>> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@many).many<A>() as
    kotlin.sequences.Sequence<kotlin.sequences.Sequence<A>>
}

@JvmName("alt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "alt(arg1)",
  "arrow.core.alt"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> Sequence<A>.alt(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@alt).alt<A>(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<A>
}

@JvmName("orElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "orElse(arg1)",
  "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.orElse(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@orElse).orElse<A>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineK(arg1)",
  "arrow.core.combineK"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.combineK(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@combineK).combineK<A>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
}

@JvmName("optional")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "optional()",
  "arrow.core.optional"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.optional(): Sequence<Option<A>> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@optional).optional<A>() as
    kotlin.sequences.Sequence<arrow.core.Option<A>>
}

@JvmName("guard")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "guard(arg0)",
  "arrow.core.extensions.sequence.alternative.Sequence.guard"
  ),
  DeprecationLevel.WARNING
)
fun guard(arg0: Boolean): Sequence<Unit> = arrow.core.extensions.sequence.alternative.Sequence
   .alternative()
   .guard(arg0) as kotlin.sequences.Sequence<kotlin.Unit>

@JvmName("lazyOrElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lazyOrElse(arg1)",
  "arrow.core.lazyOrElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.lazyOrElse(arg1: Function0<Kind<ForSequenceK, A>>): Sequence<A> =
    arrow.core.extensions.sequence.alternative.Sequence.alternative().run {
  arrow.core.SequenceK(this@lazyOrElse).lazyOrElse<A>(arg1) as kotlin.sequences.Sequence<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val alternative_singleton: SequenceKAlternative = object :
    arrow.core.extensions.SequenceKAlternative {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun alternative(): SequenceKAlternative = alternative_singleton}
