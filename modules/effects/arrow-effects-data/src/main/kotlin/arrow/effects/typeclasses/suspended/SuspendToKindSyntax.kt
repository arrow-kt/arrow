package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.typeclasses.suspended.BindSyntax

interface SuspendToKindSyntax<F> : BindSyntax<F> {

  fun <A> (suspend () -> A).k(): Kind<F, A>

  fun <A, B> (suspend (A) -> B).k(): (Kind<F, A>) -> Kind<F, B> =
    { suspend { this(it.bind()) }.k() }

  fun <A, B, C> (suspend (A, B) -> C).k(): (Kind<F, A>, Kind<F, B>) -> Kind<F, C> =
    { ka, kb -> suspend { this(ka.bind(), kb.bind()) }.k() }

  fun <A, B, C, D> (suspend (A, B, C) -> D).k(): (Kind<F, A>, Kind<F, B>, Kind<F, C>) -> Kind<F, D> =
    { ka, kb, kc -> suspend { this(ka.bind(), kb.bind(), kc.bind()) }.k() }

  fun <A, B> (suspend (A) -> B).kr(unit: Unit = Unit): (A) -> Kind<F, B> =
    { suspend { this(it) }.k() }

  fun <A, B, C> (suspend (A, B) -> C).kr(): (A, B) -> Kind<F, C> =
    { a, b -> suspend { this(a, b) }.k() }

  fun <A, B, C, D> (suspend (A, B, C) -> D).kr(): (A, B, C) -> Kind<F, D> =
    { a, b, c -> suspend { this(a, b, c) }.k() }
}