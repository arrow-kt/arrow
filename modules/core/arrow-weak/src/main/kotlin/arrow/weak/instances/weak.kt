package arrow.weak.instances

import arrow.Kind
import arrow.core.*
import arrow.weak.*
import arrow.instance
import arrow.typeclasses.*

@instance(Weak::class)
interface WeakEqInstance<A> : Eq<Weak<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Weak<A>, b: Weak<A>): Boolean = a.option() == b.option()

}

@instance(Weak::class)
interface WeakFunctorInstance : Functor<ForWeak> {
    override fun <A, B> map(fa: WeakOf<A>, f: kotlin.Function1<A, B>): Weak<B> =
        fa.fix().map(f)
}

@instance(Weak::class)
interface WeakApplicativeInstance : Applicative<ForWeak> {

    override fun <A, B> ap(fa: WeakOf<A>, ff: WeakOf<(A) -> B>): Weak<B> =
        fa.fix().ap(ff)

    override fun <A> pure(a: A): WeakOf<A> =
        Weak(a)

    override fun <A, B> map(fa: WeakOf<A>, f: (A) -> B): Weak<B> =
        fa.fix().map(f)

}

@instance(Weak::class)
interface WeakMonadInstance : Monad<ForWeak> {

    override fun <A, B> ap(fa: WeakOf<A>, ff: WeakOf<(A) -> B>): Weak<B> =
        fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: Kind<ForWeak, A>, f: (A) -> Kind<ForWeak, B>): Kind<ForWeak, B> =
        fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForWeak, Either<A, B>>): Kind<ForWeak, B> =
        Weak.tailRectM(a, f)

    override fun <A> pure(a: A): WeakOf<A> =
        Weak(a)

}

@instance(Weak::class)
interface WeakFoldableInstance : Foldable<ForWeak> {
    override fun <A> exists(fa: WeakOf<A>, p: Predicate<A>): Boolean =
        fa.fix().exists(p)

    override fun <A, B> foldLeft(fa: WeakOf<A>, b: B, f: (B, A) -> B): B =
        fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: WeakOf<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
        fa.fix().foldRight(lb, f)

    override fun <A> forall(fa: WeakOf<A>, p: Predicate<A>): Boolean =
        fa.fix().forall(p)

    override fun <A> isEmpty(fa: WeakOf<A>): Boolean =
        fa.fix().isEmpty()

    override fun <A> nonEmpty(fa: WeakOf<A>): Boolean =
        fa.fix().nonEmpty()
}

fun <A, G, B> Weak<A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Weak<B>> =
    fix().option().fold({ GA.pure(Weak.emptyWeak()) }, { GA.map(f(it), { Weak(it) }) })

@instance(Weak::class)
interface WeakTraverseInstance : Traverse<ForWeak> {
    override fun <A, B> map(fa: WeakOf<A>, f: (A) -> B): Weak<B> =
        fa.fix().map(f)

    override fun <G, A, B> traverse(fa: WeakOf<A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Weak<B>> =
        fa.fix().traverse(f, GA)

    override fun <A> exists(fa: WeakOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
        fa.fix().exists(p)

    override fun <A, B> foldLeft(fa: WeakOf<A>, b: B, f: (B, A) -> B): B =
        fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: WeakOf<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
        fa.fix().foldRight(lb, f)

    override fun <A> forall(fa: WeakOf<A>, p: Predicate<A>): Boolean =
        fa.fix().forall(p)

    override fun <A> isEmpty(fa: WeakOf<A>): Boolean =
        fa.fix().isEmpty()

    override fun <A> nonEmpty(fa: WeakOf<A>): Boolean =
        fa.fix().nonEmpty()
}
