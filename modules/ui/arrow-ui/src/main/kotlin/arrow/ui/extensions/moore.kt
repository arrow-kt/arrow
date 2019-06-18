package arrow.ui.extensions

import arrow.Kind
import arrow.ui.Moore
import arrow.ui.MoorePartialOf
import arrow.ui.fix
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface MooreComonad<V> : Comonad<MoorePartialOf<V>> {
  override fun <A, B> Kind<MoorePartialOf<V>, A>.coflatMap(f: (Kind<MoorePartialOf<V>, A>) -> B): Moore<V, B> =
      fix().coflatMap(f)

  override fun <A> Kind<MoorePartialOf<V>, A>.extract(): A =
      fix().extract()

  override fun <A, B> Kind<MoorePartialOf<V>, A>.map(f: (A) -> B): Moore<V, B> =
      fix().map(f)
}

@extension
@undocumented
interface MooreFunctor<V> : Functor<MoorePartialOf<V>> {
  override fun <A, B> Kind<MoorePartialOf<V>, A>.map(f: (A) -> B): Moore<V, B> =
      fix().map(f)
}
