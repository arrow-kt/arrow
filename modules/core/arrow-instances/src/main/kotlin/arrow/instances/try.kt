package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Try::class)
interface TryApplicativeErrorInstance : TryApplicativeInstance, ApplicativeError<ForTry, Throwable> {

    override fun <A> raiseError(e: Throwable): Try<A> =
            Failure(e)

    override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
            fix().recoverWith { f(it).fix() }

}

@instance(Try::class)
interface TryMonadErrorInstance : TryMonadInstance, MonadError<ForTry, Throwable> {
    override fun <A> raiseError(e: Throwable): Try<A> =
            Failure(e)

    override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
            fix().recoverWith { f(it).fix() }
}

@instance(Try::class)
interface TryEqInstance<A> : Eq<Try<A>> {

    fun EQA(): Eq<A>

    fun EQT(): Eq<Throwable>

    override fun Try<A>.eqv(b: Try<A>): Boolean = when (this) {
        is Success -> when (b) {
            is Failure -> false
            is Success -> EQA().run { value.eqv(b.value) }
        }
        is Failure -> when (b) {
            is Failure -> EQT().run { exception.eqv(b.exception) }
            is Success -> false
        }
    }

}

@instance(Try::class)
interface TryShowInstance<A> : Show<Try<A>> {
    override fun Try<A>.show(): String =
            toString()
}

@instance(Try::class)
interface TryFunctorInstance : Functor<ForTry> {
    override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
            fix().map(f)
}

@instance(Try::class)
interface TryApplicativeInstance : Applicative<ForTry> {
    override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
            fix().map(f)

    override fun <A> pure(a: A): Try<A> =
            Try.pure(a)
}

@instance(Try::class)
interface TryMonadInstance : Monad<ForTry> {
    override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForTry, A>.flatMap(f: (A) -> Kind<ForTry, B>): Try<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, TryOf<Either<A, B>>>): Try<B> =
            Try.tailRecM(a, f)

    override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
            fix().map(f)

    override fun <A> pure(a: A): Try<A> =
            Try.pure(a)
}

@instance(Try::class)
interface TryFoldableInstance : Foldable<ForTry> {
    override fun <A> exists(fa: TryOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.fix().exists(p)

    override fun <A, B> foldLeft(fa: TryOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: TryOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)
}

fun <A, B, G> Try<A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Try<B>> = GA.run {
    fix().fold({ pure(Try.raise(it)) }, { f(it).map({ Try { it } }) })
}

@instance(Try::class)
interface TryTraverseInstance : Traverse<ForTry> {
    override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
            fix().map(f)

    override fun <G, A, B> traverse(AP: Applicative<G>, fa: Kind<ForTry, A>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> =
            fa.fix().traverse(f, AP)

    override fun <A> exists(fa: TryOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.fix().exists(p)

    override fun <A, B> foldLeft(fa: TryOf<A>, b: B, f: Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: TryOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)
}
