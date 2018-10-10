package arrow.instances

import arrow.Kind
import arrow.data.Moore
import arrow.data.MoorePartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance
interface MooreComonadInstance<V> : Comonad<MoorePartialOf<V>> {
  override fun <A, B> Kind<MoorePartialOf<V>, A>.coflatMap(f: (Kind<MoorePartialOf<V>, A>) -> B): Moore<V, B> =
      fix().coflatMap(f)

  override fun <A> Kind<MoorePartialOf<V>, A>.extract(): A =
      fix().extract()

  override fun <A, B> Kind<MoorePartialOf<V>, A>.map(f: (A) -> B): Moore<V, B> =
      fix().map(f)
}

@instance
interface MooreFunctorInstance<V> : Functor<MoorePartialOf<V>> {
  override fun <A, B> Kind<MoorePartialOf<V>, A>.map(f: (A) -> B): Moore<V, B> =
      fix().map(f)
}

class MooreContext<S> : MooreComonadInstance<S>

class MooreContextPartiallyApplied<S> {
  infix fun <A> extensions(f: MooreContext<S>.() -> A): A =
      f(MooreContext())
}

fun <S> ForMoore(): MooreContextPartiallyApplied<S> =
    MooreContextPartiallyApplied()