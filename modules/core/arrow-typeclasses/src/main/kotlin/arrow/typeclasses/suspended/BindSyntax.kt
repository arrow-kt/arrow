package arrow.typeclasses.suspended

import arrow.Kind

interface BindSyntax<F> : Predef {

  suspend fun <A> Kind<F, A>.bind(): A

  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()

  suspend operator fun <A> Kind<F, A>.not(): A =
    bind()

  fun <A> effect(fa: suspend () -> A): Kind<F, A>

  fun <A> (suspend () -> A).effect(unit: Unit = Unit): Kind<F, A> = effect(this)

  fun <A, B> (suspend (A) -> B).effect(): (Kind<F, A>) -> Kind<F, B> =
    { suspend { this(it.bind()) }.effect() }

  fun <A, B, C> (suspend (A, B) -> C).effect(): (Kind<F, A>, Kind<F, B>) -> Kind<F, C> =
    { ka, kb -> suspend { this(ka.bind(), kb.bind()) }.effect() }

  fun <A, B, C, D> (suspend (A, B, C) -> D).effect(): (Kind<F, A>, Kind<F, B>, Kind<F, C>) -> Kind<F, D> =
    { ka, kb, kc -> suspend { this(ka.bind(), kb.bind(), kc.bind()) }.effect() }

  fun <A, B> (suspend (A) -> B).flatLiftM(unit: Unit = Unit): (A) -> Kind<F, B> =
    { suspend { this(it) }.effect() }

  fun <A, B, C> (suspend (A, B) -> C).flatLiftM(): (A, B) -> Kind<F, C> =
    { a, b -> suspend { this(a, b) }.effect() }

  fun <A, B, C, D> (suspend (A, B, C) -> D).flatLiftM(): (A, B, C) -> Kind<F, D> =
    { a, b, c -> suspend { this(a, b, c) }.effect() }

}