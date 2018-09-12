package arrow.typeclasses

import arrow.Kind

interface Contravariant<F> : Invariant<F> {
    fun <A, B> Kind<F, A>.contramap(f: (B) -> A): Kind<F, B>

    fun <A, B> lift(f: (A) -> B): (Kind<F, B>) -> Kind<F, A> =
        { fb: Kind<F, B> ->
            fb.contramap(f)
        }

    override fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B> =
        contramap(g)

    fun <A, B : A> Kind<F, A>.narrow(): Kind<F, B> = this as Kind<F, B>
}