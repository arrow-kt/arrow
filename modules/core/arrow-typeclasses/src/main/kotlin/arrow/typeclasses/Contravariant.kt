package arrow.typeclasses

import arrow.Kind

interface Contravariant<F> : Invariant<F> {

    //    def contramap[A, B](fa: F[A])(f: B => A): F[B] TODO("remove scala comment)
    fun <A, B> Kind<F, A>.contramap(f: (B) -> A): Kind<F, B>

    //    override def imap[A, B](fa: F[A])(f: A => B)(fi: B => A): F[B] = contramap(fa)(fi) TODO("remove scala comment)
    override fun <A, B> Kind<F, A>.imap(f: (A) -> B, fi: (B) -> A): Kind<F, B> = contramap(fi) // TODO("what about f")

    //    def narrow[A, B <: A](fa: F[A]): F[B] = fa.asInstanceOf[F[B]] TODO("remove scala comment)
    fun <A, B : A> Kind<F, A>.narrow(): Kind<F, B> = this as Kind<F, B>

    //    def liftContravariant[A, B](f: A => B): F[B] => F[A] = contramap(_: F[B])(f) TODO("remove scala comment)
    // TODO("what is _:")
    fun <A, B> liftContravariant(f: (A) -> B): (Kind<F, B>) -> Kind<F, A> = { /*fb: Kind<F, B> ->*/ it.contramap(f) }
}