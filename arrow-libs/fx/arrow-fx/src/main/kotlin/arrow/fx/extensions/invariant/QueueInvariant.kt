package arrow.fx.extensions.invariant

import arrow.Kind
import arrow.fx.ForQueue
import arrow.fx.IODeprecation
import arrow.fx.Queue
import arrow.fx.Queue.Companion
import arrow.fx.extensions.QueueInvariant
import arrow.typeclasses.Functor
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B> Kind<Kind<ForQueue, F>, A>.imap(
  FR: Functor<F>,
  arg1: Function1<A, B>,
  arg2: Function1<B, A>
): Queue<F, B> = arrow.fx.Queue.invariant<F>(FR).run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.Queue<F, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F> Companion.invariant(FR: Functor<F>): QueueInvariant<F> = object :
  arrow.fx.extensions.QueueInvariant<F> { override fun FR(): arrow.typeclasses.Functor<F> = FR }
