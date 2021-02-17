package arrow.core.extensions.sequence.eqK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.extensions.SequenceKEqK
import arrow.typeclasses.Eq
import kotlin.sequences.Sequence

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  ReplaceWith(
    "this.toList() == arg1.toList()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.eqK(arg1: Sequence<A>, arg2: Eq<A>): Boolean =
  arrow.core.extensions.sequence.eqK.Sequence.eqK().run {
    arrow.core.SequenceK(this@eqK).eqK<A>(arrow.core.SequenceK(arg1), arg2) as kotlin.Boolean
  }

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  level = DeprecationLevel.WARNING
)
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForSequenceK, A>> = arrow.core.extensions.sequence.eqK.Sequence
  .eqK()
  .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForSequenceK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val eqK_singleton: SequenceKEqK = object : arrow.core.extensions.SequenceKEqK {}

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
    "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
    level = DeprecationLevel.WARNING
  )
  inline fun eqK(): SequenceKEqK = eqK_singleton
}
