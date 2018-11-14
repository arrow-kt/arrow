package arrow.mtl.instances

import arrow.Kind
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.KleisliMonadErrorInstance
import arrow.instances.KleisliMonadInstance
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

class KleisliMtlContextPartiallyApplied<F, D, E>(val MF: MonadError<F, E>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: KleisliMtlContext<F, D, E>.() -> A): A =
    f(KleisliMtlContext(MF))
}

fun <F, D, E> ForKleisli(MF: MonadError<F, E>): KleisliMtlContextPartiallyApplied<F, D, E> =
  KleisliMtlContextPartiallyApplied(MF)