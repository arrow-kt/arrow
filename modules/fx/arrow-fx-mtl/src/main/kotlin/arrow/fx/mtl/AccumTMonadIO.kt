package arrow.fx.mtl

import arrow.Kind
import arrow.extension
import arrow.fx.IO
import arrow.fx.typeclasses.MonadIO
import arrow.mtl.AccumTPartialOf
import arrow.mtl.extensions.AccumTMonad
import arrow.mtl.extensions.accumt.monadTrans.liftT
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

@extension
interface AccumTMonadIO<S, F> : MonadIO<AccumTPartialOf<S, F>>, AccumTMonad<S, F> {
  fun FIO(): MonadIO<F>

  override fun MS(): Monoid<S>
  override fun MF(): Monad<F> = FIO()

  override fun <A> IO<A>.liftIO(): Kind<AccumTPartialOf<S, F>, A> = FIO().run {
    liftIO().liftT(MS(), MF())
  }
}
