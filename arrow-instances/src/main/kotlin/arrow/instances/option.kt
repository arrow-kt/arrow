package arrow.instances

import arrow.HK
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Option::class)
interface OptionSemigroupInstance<A> : Semigroup<Option<A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: Option<A>, b: Option<A>): Option<A> =
            when (a) {
                is Some<A> -> when (b) {
                    is Some<A> -> Some(SG().combine(a.t, b.t))
                    None -> b
                }
                None -> a
            }
}

@instance(Option::class)
interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
    override fun empty(): Option<A> = None
}

@instance(Option::class)
interface OptionApplicativeErrorInstance : OptionApplicativeInstance, ApplicativeError<OptionHK, Unit> {
    override fun <A> raiseError(e: Unit): Option<A> = None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (Unit) -> OptionKind<A>): Option<A> = fa.ev().orElse({ f(Unit).ev() })
}

@instance(Option::class)
interface OptionMonadErrorInstance : OptionApplicativeErrorInstance, OptionMonadInstance, MonadError<OptionHK, Unit> {
    override fun <A, B> ap(fa: OptionKind<A>, ff: OptionKind<(A) -> B>): Option<B> =
            super<OptionMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: OptionKind<A>, f: (A) -> B): Option<B> =
            super<OptionMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): Option<A> =
            super<OptionMonadInstance>.pure(a)
}

@instance(Option::class)
interface OptionEqInstance<A> : Eq<Option<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Option<A>, b: Option<A>): Boolean = when (a) {
        is Some -> when (b) {
            None -> false
            is Some -> EQ().eqv(a.t, b.t)
        }
        None -> when (b) {
            None -> true
            is Some -> false
        }
    }

}

@instance(Option::class)
interface OptionShowInstance<A> : Show<Option<A>> {
    override fun show(a: Option<A>): String =
            a.toString()
}

@instance(Option::class)
interface OptionFunctorInstance : Functor<OptionHK> {
    override fun <A, B> map(fa: OptionKind<A>, f: kotlin.Function1<A, B>): Option<B> =
            fa.ev().map(f)
}

@instance(Option::class)
interface OptionApplicativeInstance : Applicative<OptionHK> {
    override fun <A, B> ap(fa: OptionKind<A>, ff: OptionKind<kotlin.Function1<A, B>>): Option<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: OptionKind<A>, f: kotlin.Function1<A, B>): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> =
            Option.pure(a)
}

@instance(Option::class)
interface OptionMonadInstance : Monad<OptionHK> {
    override fun <A, B> ap(fa: OptionKind<A>, ff: OptionKind<kotlin.Function1<A, B>>): Option<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: OptionKind<A>, f: kotlin.Function1<A, OptionKind<B>>): Option<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionKind<Either<A, B>>>): Option<B> =
            Option.tailRecM(a, f)

    override fun <A, B> map(fa: OptionKind<A>, f: kotlin.Function1<A, B>): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> =
            Option.pure(a)
}

@instance(Option::class)
interface OptionFoldableInstance : Foldable<OptionHK> {
    override fun <A> exists(fa: OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: OptionKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: OptionKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> forall(fa: OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().forall(p)

    override fun <A> isEmpty(fa: OptionKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()

    override fun <A> nonEmpty(fa: OptionKind<A>): kotlin.Boolean =
            fa.ev().nonEmpty()
}

fun <A, G, B> Option<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Option<B>> =
        this.ev().let { option ->
            when (option) {
                is Some -> GA.map(f(option.t), { Some(it) })
                is None -> GA.pure(None)
            }
        }

fun <A, G, B> Option<A>.traverseFilter(f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, Option<B>> =
        this.ev().let { option ->
            when (option) {
                is Some -> f(option.t)
                None -> GA.pure(None)
            }
        }

@instance(Option::class)
interface OptionTraverseInstance : Traverse<OptionHK> {
    override fun <A, B> map(fa: OptionKind<A>, f: kotlin.Function1<A, B>): Option<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: OptionKind<A>, f: kotlin.Function1<A, HK<G, B>>, GA: Applicative<G>): HK<G, Option<B>> =
            fa.ev().traverse(f, GA)

    override fun <A> exists(fa: OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: OptionKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: OptionKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> forall(fa: OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().forall(p)

    override fun <A> isEmpty(fa: OptionKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()

    override fun <A> nonEmpty(fa: OptionKind<A>): kotlin.Boolean =
            fa.ev().nonEmpty()
}