package arrow.instances

import arrow.*
import arrow.core.*

@instance(Id::class)
interface IdEqInstance<A> : Eq<Id<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Id<A>, b: Id<A>): Boolean =
            EQ().eqv(a.value, b.value)
}

@instance(Id::class)
interface IdFunctorInstance : Functor<IdHK> {
    override fun <A, B> map(fa: IdKind<A>, f: kotlin.Function1<A, B>): Id<B> =
            fa.ev().map(f)
}

@instance(Id::class)
interface IdApplicativeInstance : Applicative<IdHK> {
    override fun <A, B> ap(fa: IdKind<A>, ff: IdKind<kotlin.Function1<A, B>>): Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: IdKind<A>, f: kotlin.Function1<A, B>): Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Id<A> =
            Id.pure(a)
}

@instance(Id::class)
interface IdMonadInstance : Monad<IdHK> {
    override fun <A, B> ap(fa: IdKind<A>, ff: IdKind<kotlin.Function1<A, B>>): Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: IdKind<A>, f: kotlin.Function1<A, IdKind<B>>): Id<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IdKind<Either<A, B>>>): Id<B> =
            Id.tailRecM(a, f)

    override fun <A, B> map(fa: IdKind<A>, f: kotlin.Function1<A, B>): Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Id<A> =
            Id.pure(a)
}

@instance(Id::class)
interface IdComonadInstance : Comonad<IdHK> {
    override fun <A, B> coflatMap(fa: IdKind<A>, f: kotlin.Function1<IdKind<A>, B>): Id<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: IdKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: IdKind<A>, f: kotlin.Function1<A, B>): Id<B> =
            fa.ev().map(f)
}

@instance(Id::class)
interface IdBimonadInstance : Bimonad<IdHK> {
    override fun <A, B> ap(fa: IdKind<A>, ff: IdKind<kotlin.Function1<A, B>>): Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: IdKind<A>, f: kotlin.Function1<A, IdKind<B>>): Id<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IdKind<Either<A, B>>>): Id<B> =
            Id.tailRecM(a, f)

    override fun <A, B> map(fa: IdKind<A>, f: kotlin.Function1<A, B>): Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Id<A> =
            Id.pure(a)

    override fun <A, B> coflatMap(fa: IdKind<A>, f: kotlin.Function1<IdKind<A>, B>): Id<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: IdKind<A>): A =
            fa.ev().extract()
}

@instance(Id::class)
interface IdFoldableInstance : Foldable<IdHK> {
    override fun <A, B> foldLeft(fa: IdKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: IdKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)
}

fun <A, G, B> Id<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Id<B>> = GA.map(f(this.ev().value), { Id(it) })

@instance(Id::class)
interface IdTraverseInstance : Traverse<IdHK> {
    override fun <A, B> map(fa: IdKind<A>, f: kotlin.Function1<A, B>): Id<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: IdKind<A>, f: kotlin.Function1<A, HK<G, B>>, GA: Applicative<G>): HK<G, Id<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: IdKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: IdKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)
}
