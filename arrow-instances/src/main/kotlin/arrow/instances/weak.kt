package arrow.instances

import arrow.HK
import arrow.core.*
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(Weak::class)
interface WeakEqInstance<A> : Eq<Weak<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Weak<A>, b: Weak<A>): Boolean = a.option() == b.option()

}

@instance(Weak::class)
interface WeakFunctorInstance : Functor<WeakHK> {
    override fun <A, B> map(fa: WeakKind<A>, f: kotlin.Function1<A, B>): Weak<B> =
        fa.ev().map(f)
}

@instance(Weak::class)
interface WeakApplicativeInstance : Applicative<WeakHK> {

    override fun <A, B> ap(fa: HK<WeakHK, A>, ff: HK<WeakHK, (A) -> B>): HK<WeakHK, B> =
        fa.ev().ap(ff)

    override fun <A> pure(a: A): HK<WeakHK, A> =
        Weak(a)

    override fun <A, B> map(fa: HK<WeakHK, A>, f: (A) -> B): HK<WeakHK, B> =
        fa.ev().map(f)

}

@instance(Weak::class)
interface WeakMonadInstance : Monad<WeakHK> {

    override fun <A, B> ap(fa: HK<WeakHK, A>, ff: HK<WeakHK, (A) -> B>): HK<WeakHK, B> =
        fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: HK<WeakHK, A>, f: (A) -> HK<WeakHK, B>): HK<WeakHK, B> =
        fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WeakHK, Either<A, B>>): HK<WeakHK, B> =
        Weak.tailRectM(a, f)

    override fun <A> pure(a: A): HK<WeakHK, A> =
        Weak(a)

}

@instance(Weak::class)
interface WeakFoldableInstance : Foldable<WeakHK> {
    override fun <A> exists(fa: WeakKind<A>, p: Predicate<A>): Boolean =
        fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: WeakKind<A>, b: B, f: (B, A) -> B): B =
        fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: WeakKind<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
        fa.ev().foldRight(lb, f)

    override fun <A> forall(fa: WeakKind<A>, p: Predicate<A>): Boolean =
        fa.ev().forall(p)

    override fun <A> isEmpty(fa: WeakKind<A>): Boolean =
        fa.ev().isEmpty()

    override fun <A> nonEmpty(fa: WeakKind<A>): Boolean =
        fa.ev().nonEmpty()
}

fun <A, G, B> Weak<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Weak<B>> =
    ev().option().fold({ GA.pure(Weak.emptyWeak()) }, { GA.map(f(it), { Weak(it) }) })

@instance(Weak::class)
interface WeakTraverseInstance : Traverse<WeakHK> {
    override fun <A, B> map(fa: WeakKind<A>, f: (A) -> B): Weak<B> =
        fa.ev().map(f)

    override fun <G, A, B> traverse(fa: WeakKind<A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Weak<B>> =
        fa.ev().traverse(f, GA)

    override fun <A> exists(fa: WeakKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
        fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: WeakKind<A>, b: B, f: (B, A) -> B): B =
        fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: WeakKind<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
        fa.ev().foldRight(lb, f)

    override fun <A> forall(fa: WeakKind<A>, p: Predicate<A>): Boolean =
        fa.ev().forall(p)

    override fun <A> isEmpty(fa: WeakKind<A>): Boolean =
        fa.ev().isEmpty()

    override fun <A> nonEmpty(fa: WeakKind<A>): Boolean =
        fa.ev().nonEmpty()
}
