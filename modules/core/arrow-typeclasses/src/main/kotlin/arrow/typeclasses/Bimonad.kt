package arrow.typeclasses

inline operator fun <F, A> Bimonad<F>.invoke(ff: Bimonad<F>.() -> A) =
  run(ff)

interface Bimonad<F> : Monad<F>, Comonad<F>