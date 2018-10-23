package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.effects.typeclasses.MonadDefer
import arrow.extension

@extension
interface KleisliMonadDeferInstance<F, D> : MonadDefer<KleisliPartialOf<F, D>> {

    fun DF(): MonadDefer<F>

    override fun <A> defer(fa: () -> Kind<KleisliPartialOf<F, D>, A>): Kind<KleisliPartialOf<F, D>, A> = Kleisli { d: D ->
        DF().defer { fa().fix().run(d) }
    }

    override fun <A> just(a: A): Kind<KleisliPartialOf<F, D>, A> = Kleisli.just(DF(), a)

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<KleisliPartialOf<F, D>, Either<A, B>>): Kind<KleisliPartialOf<F, D>, B> = Kleisli.tailRecM(DF(), a, f)

    override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.flatMap(f: (A) -> Kind<KleisliPartialOf<F, D>, B>): Kind<KleisliPartialOf<F, D>, B> = this.fix().flatMap(DF()) { a -> f(a).fix() }

    override fun <A> Kind<KleisliPartialOf<F, D>, A>.handleErrorWith(f: (Throwable) -> Kind<KleisliPartialOf<F, D>, A>): Kind<KleisliPartialOf<F, D>, A> = this.fix().handleErrorWith(DF(), f)

    override fun <A> raiseError(e: Throwable): Kind<KleisliPartialOf<F, D>, A> = Kleisli.raiseError(DF(), e)
}