package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.typeclasses.Monad

interface MonadTrans<T> {
  fun <F, A> lift(MF: Monad<F>, fa: Kind<F, A>): Kind2<T, F, A>
}
