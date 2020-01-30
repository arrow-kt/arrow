package arrow.fx.extensions

import arrow.Kind
import arrow.extension
import arrow.fx.Queue
import arrow.fx.QueueOf
import arrow.fx.QueuePartialOf
import arrow.fx.fix
import arrow.typeclasses.Functor
import arrow.typeclasses.Invariant
import arrow.undocumented

@extension
@undocumented
interface QueueInvariant<F> : Invariant<QueuePartialOf<F>> {
  fun FR(): Functor<F>

  override fun <A, B> QueueOf<F, A>.imap(f: (A) -> B, g: (B) -> A): Queue<F, B> =
    FR().run {
      val fixed = this@imap.fix()
      object : Queue<F, B> {
        override fun take(): Kind<F, B> = fixed.take().map(f)
        override fun offer(a: B): Kind<F, Unit> = fixed.offer(g(a))
        override fun size(): Kind<F, Int> = fixed.size()
        override fun awaitShutdown(): Kind<F, Unit> = fixed.awaitShutdown()
        override fun shutdown(): Kind<F, Unit> = fixed.shutdown()
      }
    }
}
