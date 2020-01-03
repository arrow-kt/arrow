package arrow.fx.extensions

import arrow.Kind
import arrow.extension
import arrow.fx.Queue
import arrow.fx.QueueOf
import arrow.fx.QueuePartialOf
import arrow.fx.fix
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface QueueFunctor<F, E> : Functor<QueuePartialOf<F, E>> {
  fun functorF(): Functor<F>
  override fun <B, C> QueueOf<F, E, B>.map(f: (B) -> C): Queue<F, E, C> =
    object : Queue<F, E, C> {
      override fun offer(a: E): Kind<F, Unit> = this@map.fix().offer(a)
      override fun take(): Kind<F, C> = with(functorF()) {
        this@map.fix().take().map(f)
      }

      override fun size(): Kind<F, Int> = this@map.fix().size()
      override fun awaitShutdown(): Kind<F, Unit> = this@map.fix().awaitShutdown()
      override fun shutdown(): Kind<F, Unit> = this@map.fix().shutdown()
    }
}
