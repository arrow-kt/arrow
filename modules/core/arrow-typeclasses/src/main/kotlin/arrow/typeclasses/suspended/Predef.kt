package arrow.typeclasses.suspended

import arrow.Kind

interface Predef {
  suspend fun <A> effectIdentity(a: A): A = a
}

interface BindSyntax<F> : Predef {

  suspend fun <A> Kind<F, A>.bind(): A

  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()

  fun <A> f(fa: suspend () -> A): Kind<F, A>

  fun <A> (suspend () -> A).liftM(): Kind<F, A> = f(this)

  fun <A, B> (suspend (A) -> B).liftM(): (Kind<F, A>) -> Kind<F, B> =
    { suspend { this(it.bind()) }.liftM() }

  fun <A, B, C> (suspend (A, B) -> C).liftM(): (Kind<F, A>, Kind<F, B>) -> Kind<F, C> =
    { ka, kb -> suspend { this(ka.bind(), kb.bind()) }.liftM() }

  fun <A, B, C, D> (suspend (A, B, C) -> D).liftM(): (Kind<F, A>, Kind<F, B>, Kind<F, C>) -> Kind<F, D> =
    { ka, kb, kc -> suspend { this(ka.bind(), kb.bind(), kc.bind()) }.liftM() }

  fun <A, B> (suspend (A) -> B).flatLiftM(unit: Unit = Unit): (A) -> Kind<F, B> =
    { suspend { this(it) }.liftM() }

  fun <A, B, C> (suspend (A, B) -> C).flatLiftM(): (A, B) -> Kind<F, C> =
    { a, b -> suspend { this(a, b) }.liftM() }

  fun <A, B, C, D> (suspend (A, B, C) -> D).flatLiftM(): (A, B, C) -> Kind<F, D> =
    { a, b, c -> suspend { this(a, b, c) }.liftM() }

}