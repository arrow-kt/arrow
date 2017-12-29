package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*

@instance(Try::class)
interface TryMonadErrorInstance : TryMonadInstance, MonadError<TryHK, Throwable> {

    override fun <A> raiseError(e: Throwable): Try<A> = Failure(e)

    override fun <A> handleErrorWith(fa: TryKind<A>, f: (Throwable) -> TryKind<A>): Try<A> = fa.ev().recoverWith { f(it).ev() }

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
interface TryFunctorInstance : Functor<TryHK> {
    override fun <A, B> map(fa: TryKind<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.ev().map(f)
}

@instance(Try::class)
interface TryApplicativeInstance : Applicative<TryHK> {
    override fun <A, B> ap(fa: TryKind<A>, ff: TryKind<kotlin.Function1<A, B>>): Try<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: TryKind<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Try<A> =
            Try.pure(a)
}

@instance(Try::class)
interface TryMonadInstance : Monad<TryHK> {
    override fun <A, B> ap(fa: TryKind<A>, ff: TryKind<kotlin.Function1<A, B>>): Try<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: TryKind<A>, f: kotlin.Function1<A, TryKind<B>>): Try<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, TryKind<Either<A, B>>>): Try<B> =
            Try.tailRecM(a, f)

    override fun <A, B> map(fa: TryKind<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Try<A> =
            Try.pure(a)
}

@instance(Try::class)
interface TryFoldableInstance : Foldable<TryHK> {
    override fun <A> exists(fa: TryKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: TryKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: TryKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)
}

@instance(Try::class)
interface TryTraverseInstance : Traverse<TryHK> {
    override fun <A, B> map(fa: TryKind<A>, f: kotlin.Function1<A, B>): Try<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: TryKind<A>, f: kotlin.Function1<A, HK<G, B>>, GA: Applicative<G>): HK<G, Try<B>> =
            fa.ev().traverse(f, GA)

    override fun <A> exists(fa: TryKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: TryKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: TryKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)
}