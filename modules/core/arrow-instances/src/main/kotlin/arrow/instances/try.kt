package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(Try::class)
interface TryApplicativeErrorInstance : TryApplicativeInstance, ApplicativeError<ForTry, Throwable> {

    override fun <A> raiseError(e: Throwable): Try<A> = Failure(e)

    override fun <A> handleErrorWith(fa: TryOf<A>, f: (Throwable) -> TryOf<A>): Try<A> = fa.extract().recoverWith { f(it).extract() }

}

@instance(Try::class)
interface TryMonadErrorInstance : TryApplicativeErrorInstance, TryMonadInstance, MonadError<ForTry, Throwable> {
    override fun <A, B> ap(fa: TryOf<A>, ff: TryOf<(A) -> B>): Try<B> =
            super<TryMonadInstance>.ap(fa, ff).extract()

    override fun <A, B> map(fa: TryOf<A>, f: (A) -> B): Try<B> =
            super<TryMonadInstance>.map(fa, f).extract()

    override fun <A> pure(a: A): Try<A> =
            super<TryMonadInstance>.pure(a).extract()
}

@instance(Try::class)
interface TryEqInstance<A> : Eq<Try<A>> {

    fun EQA(): Eq<A>

    override fun eqv(a: Try<A>, b: Try<A>): Boolean = when (a) {
        is Success -> when (b) {
            is Failure -> false
            is Success -> EQA().eqv(a.value, b.value)
        }
        is Failure -> when (b) {
        //currently not supported by implicit resolution to have implicit that does not occur in type params
            is Failure -> a.exception == b.exception
            is Success -> false
        }
    }

}

@instance(Try::class)
interface TryShowInstance<A> : Show<Try<A>> {
    override fun show(a: Try<A>): String =
            a.toString()
}

@instance(Try::class)
interface TryFunctorInstance : Functor<ForTry> {
    override fun <A, B> map(fa: TryOf<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.extract().map(f)
}

@instance(Try::class)
interface TryApplicativeInstance : Applicative<ForTry> {
    override fun <A, B> ap(fa: TryOf<A>, ff: TryOf<kotlin.Function1<A, B>>): Try<B> =
            fa.extract().ap(ff)

    override fun <A, B> map(fa: TryOf<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Try<A> =
            Try.pure(a)
}

@instance(Try::class)
interface TryMonadInstance : Monad<ForTry> {
    override fun <A, B> ap(fa: TryOf<A>, ff: TryOf<kotlin.Function1<A, B>>): Try<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: TryOf<A>, f: kotlin.Function1<A, TryOf<B>>): Try<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, TryOf<Either<A, B>>>): Try<B> =
            Try.tailRecM(a, f)

    override fun <A, B> map(fa: TryOf<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Try<A> =
            Try.pure(a)
}

@instance(Try::class)
interface TryFoldableInstance : Foldable<ForTry> {
    override fun <A> exists(fa: TryOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.extract().exists(p)

    override fun <A, B> foldLeft(fa: TryOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.extract().foldLeft(b, f)

    override fun <A, B> foldRight(fa: TryOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.extract().foldRight(lb, f)
}

@instance(Try::class)
interface TryTraverseInstance : Traverse<ForTry> {
    override fun <A, B> map(fa: TryOf<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.extract().map(f)

    override fun <G, A, B> traverse(fa: TryOf<A>, f: kotlin.Function1<A, Kind<G, B>>, GA: Applicative<G>): Kind<G, Try<B>> =
            fa.extract().traverse(f, GA)

    override fun <A> exists(fa: TryOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.extract().exists(p)

    override fun <A, B> foldLeft(fa: TryOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.extract().foldLeft(b, f)

    override fun <A, B> foldRight(fa: TryOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.extract().foldRight(lb, f)
}