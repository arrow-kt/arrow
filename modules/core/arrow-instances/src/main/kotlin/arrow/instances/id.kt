package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Id::class)
interface IdEqInstance<A> : Eq<Id<A>> {

    fun EQ(): Eq<A>

    override fun Id<A>.eqv(b: Id<A>): Boolean =
            EQ().run { value.eqv(b.value) }
}

@instance(Id::class)
interface IdShowInstance<A> : Show<Id<A>> {
    override fun Id<A>.show(): String =
            toString()
}

@instance(Id::class)
interface IdFunctorInstance : Functor<ForId> {
    override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
            fix().map(f)
}

@instance(Id::class)
interface IdApplicativeInstance : Applicative<ForId> {
    override fun <A, B> Kind<ForId, A>.ap(ff: Kind<ForId, (A) -> B>): Id<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
            fix().map(f)

    override fun <A> pure(a: A): Id<A> =
            Id.pure(a)
}

@instance(Id::class)
interface IdMonadInstance : Monad<ForId> {
    override fun <A, B> Kind<ForId, A>.ap(ff: Kind<ForId, (A) -> B>): Id<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForId, A>.flatMap(f: (A) -> Kind<ForId, B>): Id<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IdOf<Either<A, B>>>): Id<B> =
            Id.tailRecM(a, f)

    override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
            fix().map(f)

    override fun <A> pure(a: A): Id<A> =
            Id.pure(a)
}

@instance(Id::class)
interface IdComonadInstance : Comonad<ForId> {
    override fun <A, B> Kind<ForId, A>.coflatMap(f: (Kind<ForId, A>) -> B): Id<B> =
            fix().coflatMap(f)

    override fun <A> Kind<ForId, A>.extract(): A =
            fix().extract()

    override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
            fix().map(f)
}

@instance(Id::class)
interface IdBimonadInstance : Bimonad<ForId> {
    override fun <A, B> Kind<ForId, A>.ap(ff: Kind<ForId, (A) -> B>): Id<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForId, A>.flatMap(f: (A) -> Kind<ForId, B>): Id<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IdOf<Either<A, B>>>): Id<B> =
            Id.tailRecM(a, f)

    override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
            fix().map(f)

    override fun <A> pure(a: A): Id<A> =
            Id.pure(a)

    override fun <A, B> Kind<ForId, A>.coflatMap(f: (Kind<ForId, A>) -> B): Id<B> =
            fix().coflatMap(f)

    override fun <A> Kind<ForId, A>.extract(): A =
            fix().extract()
}

@instance(Id::class)
interface IdFoldableInstance : Foldable<ForId> {
    override fun <A, B> foldLeft(fa: IdOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: IdOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)
}

fun <A, G, B> Id<A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Id<B>> = GA.run {
    f(fix().value).map({ Id(it) })
}

@instance(Id::class)
interface IdTraverseInstance : Traverse<ForId> {
    override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
            fix().map(f)

    override fun <G, A, B> traverse(AP: Applicative<G>, fa: Kind<ForId, A>, f: (A) -> Kind<G, B>): Kind<G, Id<B>> =
            fa.fix().traverse(f, AP)

    override fun <A, B> foldLeft(fa: IdOf<A>, b: B, f: Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: IdOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)
}
