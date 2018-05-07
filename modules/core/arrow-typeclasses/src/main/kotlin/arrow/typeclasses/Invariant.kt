package arrow.typeclasses

import arrow.Kind

interface Invariant<F> {

    //    def imap[A, B](fa: F[A])(f: A => B)(g: B => A): F[B]
    fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B>
}