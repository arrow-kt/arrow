package arrow.mtl.extensions

import arrow.Kind
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.extensions.KleisliMonadErrorInstance
import arrow.data.extensions.KleisliMonadInstance
import arrow.data.fix

import arrow.extension
import arrow.mtl.typeclasses.MonadReader
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError

@extension
interface KleisliMonadReaderInstance<F, D> : MonadReader<KleisliPartialOf<F, D>, D>, KleisliMonadInstance<F, D> {

  override fun MF(): Monad<F>

  override fun ask(): Kleisli<F, D, D> = Kleisli { MF().just(it) }

  override fun <A> Kind<KleisliPartialOf<F, D>, A>.local(f: (D) -> D): Kleisli<F, D, A> = fix().local(f)
}

class KleisliMtlContext<F, D, E>(val MF: MonadError<F, E>) : KleisliMonadReaderInstance<F, D>, KleisliMonadErrorInstance<F, D, E> {

  override fun MF(): Monad<F> = MF

  override fun ME(): MonadError<F, E> = MF
}
