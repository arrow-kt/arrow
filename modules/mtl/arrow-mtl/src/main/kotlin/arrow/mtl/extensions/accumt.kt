package arrow.mtl.extensions

import arrow.Kind
import arrow.extension
import arrow.mtl.AccumTPartialOf
import arrow.mtl.fix
import arrow.typeclasses.Functor
import arrow.undocumented


@extension
@undocumented
interface AccumtTFunctor<W, M> : Functor<AccumTPartialOf<W, M>> {

  fun MF(): Functor<M>

  override fun <A, B> Kind<AccumTPartialOf<W, M>, A>.map(f: (A) -> B): Kind<AccumTPartialOf<W, M>, B> {
    this.fix().
  }
}
