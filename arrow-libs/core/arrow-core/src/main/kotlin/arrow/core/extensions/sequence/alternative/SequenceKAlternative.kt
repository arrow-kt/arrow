package arrow.core.extensions.sequence.alternative

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.extensions.SequenceKAlternative
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
    "this.some()",
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
    "this.many()",
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
    "this + arg1"
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
    "this + arg1"
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
    "this + arg1"
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
    "this.map(::Some) + sequenceOf(None)",
    "arrow.core.None",
    "arrow.core.Some"
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
    "if (arg0) sequenceOf(Unit) else emptySequence()"
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
    "this + arg1()"
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

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Alternative typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun alternative(): SequenceKAlternative = alternative_singleton
}
