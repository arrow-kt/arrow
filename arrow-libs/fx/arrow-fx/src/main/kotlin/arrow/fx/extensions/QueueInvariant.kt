package arrow.fx.extensions

import arrow.Kind
import arrow.core.Option
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
        override fun tryOfferAll(a: Iterable<B>): Kind<F, Boolean> = fixed.tryOfferAll(a.map(g))
        override fun offerAll(a: Iterable<B>): Kind<F, Unit> = fixed.offerAll(a.map(g))
        override fun peek(): Kind<F, B> = fixed.peek().map(f)
        override fun take(): Kind<F, B> = fixed.take().map(f)
        override fun takeAll(): Kind<F, List<B>> = fixed.takeAll().map { it.map(f) }
        override fun offer(a: B): Kind<F, Unit> = fixed.offer(g(a))
        override fun size(): Kind<F, Int> = fixed.size()
        override fun tryTake(): Kind<F, Option<B>> = fixed.tryTake().map { it.map(f) }
        override fun tryPeek(): Kind<F, Option<B>> = fixed.tryPeek().map { it.map(f) }
        override fun peekAll(): Kind<F, List<B>> = fixed.peekAll().map { it.map(f) }
        override fun tryOffer(a: B): Kind<F, Boolean> = fixed.tryOffer(g(a))
      }
    }
}
