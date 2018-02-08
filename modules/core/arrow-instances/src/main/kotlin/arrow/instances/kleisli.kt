package arrow.instances

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.data.Kleisli
import arrow.data.KleisliOf
import arrow.data.KleisliPartialOf
import arrow.instance
import arrow.typeclasses.*

@instance(Kleisli::class)
interface KleisliFunctorInstance<F, D> : Functor<KleisliPartialOf<F, D>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: KleisliOf<F, D, A>, f: (A) -> B): Kleisli<F, D, B> = fa.extract().map(f, FF())
}

@instance(Kleisli::class)
interface KleisliApplicativeInstance<F, D> : KleisliFunctorInstance<F, D>, Applicative<KleisliPartialOf<F, D>> {

    override fun FF(): Applicative<F>

    override fun <A> pure(a: A): Kleisli<F, D, A> = Kleisli({ FF().pure(a) })

    override fun <A, B> map(fa: KleisliOf<F, D, A>, f: (A) -> B): Kleisli<F, D, B> =
            fa.extract().map(f, FF())

    override fun <A, B> ap(fa: KleisliOf<F, D, A>, ff: KleisliOf<F, D, (A) -> B>): Kleisli<F, D, B> =
            fa.extract().ap(ff, FF())

    override fun <A, B> product(fa: KleisliOf<F, D, A>, fb: KleisliOf<F, D, B>): Kleisli<F, D, Tuple2<A, B>> =
            Kleisli({ FF().product(fa.extract().run(it), fb.extract().run(it)) })
}

@instance(Kleisli::class)
interface KleisliMonadInstance<F, D> : KleisliApplicativeInstance<F, D>, Monad<KleisliPartialOf<F, D>> {

    override fun FF(): Monad<F>

    override fun <A, B> map(fa: KleisliOf<F, D, A>, f: (A) -> B): Kleisli<F, D, B> =
            fa.extract().map(f, FF())

    override fun <A, B> flatMap(fa: KleisliOf<F, D, A>, f: (A) -> KleisliOf<F, D, B>): Kleisli<F, D, B> =
            fa.extract().flatMap(f.andThen { it.extract() }, FF())

    override fun <A, B> ap(fa: KleisliOf<F, D, A>, ff: KleisliOf<F, D, (A) -> B>): Kleisli<F, D, B> =
            fa.extract().ap(ff, FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> KleisliOf<F, D, Either<A, B>>): Kleisli<F, D, B> =
            Kleisli.tailRecM(a, f, FF())

}

@instance(Kleisli::class)
interface KleisliApplicativeErrorInstance<F, D, E> : ApplicativeError<KleisliPartialOf<F, D>, E>, KleisliApplicativeInstance<F, D> {

    override fun FF(): MonadError<F, E>

    override fun <A> handleErrorWith(fa: KleisliOf<F, D, A>, f: (E) -> KleisliOf<F, D, A>): Kleisli<F, D, A> =
            fa.extract().handleErrorWith(f, FF())

    override fun <A> raiseError(e: E): Kleisli<F, D, A> =
            Kleisli.raiseError(e, FF())

}

@instance(Kleisli::class)
interface KleisliMonadErrorInstance<F, D, E> : KleisliApplicativeErrorInstance<F, D, E>, MonadError<KleisliPartialOf<F, D>, E>, KleisliMonadInstance<F, D>
