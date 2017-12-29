package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupInstance<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}

@instance(NonEmptyList::class)
interface NonEmptyListEqInstance<A> : Eq<NonEmptyList<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: NonEmptyList<A>, b: NonEmptyList<A>): Boolean =
            a.all.zip(b.all) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }
}

interface NonEmptyListFunctorInstance : Functor<NonEmptyListHK> {
    override fun <A, B> map(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.ev().map(f)
}

object NonEmptyListFunctorInstanceImplicits {
    fun instance(): NonEmptyListFunctorInstance = NonEmptyList.Companion.functor()
}

fun NonEmptyList.Companion.functor(): NonEmptyListFunctorInstance =
        object : NonEmptyListFunctorInstance, Functor<NonEmptyListHK> {}

interface NonEmptyListApplicativeInstance : Applicative<NonEmptyListHK> {
    override fun <A, B> ap(fa: NonEmptyListKind<A>, ff: NonEmptyListKind<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

object NonEmptyListApplicativeInstanceImplicits {
    fun instance(): NonEmptyListApplicativeInstance = NonEmptyList.Companion.applicative()
}

fun NonEmptyList.Companion.applicative(): NonEmptyListApplicativeInstance =
        object : NonEmptyListApplicativeInstance, Applicative<NonEmptyListHK> {}

interface NonEmptyListMonadInstance : Monad<NonEmptyListHK> {
    override fun <A, B> ap(fa: NonEmptyListKind<A>, ff: NonEmptyListKind<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, NonEmptyListKind<B>>): NonEmptyList<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListKind<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

object NonEmptyListMonadInstanceImplicits {
    fun instance(): NonEmptyListMonadInstance = NonEmptyList.Companion.monad()
}

fun NonEmptyList.Companion.monad(): NonEmptyListMonadInstance =
        object : NonEmptyListMonadInstance, Monad<NonEmptyListHK> {}

interface NonEmptyListComonadInstance : Comonad<NonEmptyListHK> {
    override fun <A, B> coflatMap(fa: NonEmptyListKind<A>, f: kotlin.Function1<NonEmptyListKind<A>, B>): NonEmptyList<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: NonEmptyListKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.ev().map(f)
}

object NonEmptyListComonadInstanceImplicits {
    fun instance(): NonEmptyListComonadInstance = NonEmptyList.Companion.comonad()
}

fun NonEmptyList.Companion.comonad(): NonEmptyListComonadInstance =
        object : NonEmptyListComonadInstance, Comonad<NonEmptyListHK> {}

interface NonEmptyListBimonadInstance : Bimonad<NonEmptyListHK> {
    override fun <A, B> ap(fa: NonEmptyListKind<A>, ff: NonEmptyListKind<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, NonEmptyListKind<B>>): NonEmptyList<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListKind<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)

    override fun <A, B> coflatMap(fa: NonEmptyListKind<A>, f: kotlin.Function1<NonEmptyListKind<A>, B>): NonEmptyList<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: NonEmptyListKind<A>): A =
            fa.ev().extract()
}

object NonEmptyListBimonadInstanceImplicits {
    fun instance(): NonEmptyListBimonadInstance = NonEmptyList.Companion.bimonad()
}

fun NonEmptyList.Companion.bimonad(): NonEmptyListBimonadInstance =
        object : NonEmptyListBimonadInstance, Bimonad<NonEmptyListHK> {}

interface NonEmptyListFoldableInstance : Foldable<NonEmptyListHK> {
    override fun <A, B> foldLeft(fa: NonEmptyListKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: NonEmptyListKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

object NonEmptyListFoldableInstanceImplicits {
    fun instance(): NonEmptyListFoldableInstance = NonEmptyList.Companion.foldable()
}

fun NonEmptyList.Companion.foldable(): NonEmptyListFoldableInstance =
        object : NonEmptyListFoldableInstance, Foldable<NonEmptyListHK> {}

interface NonEmptyListTraverseInstance : Traverse<NonEmptyListHK> {
    override fun <A, B> map(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: NonEmptyListKind<A>, f: kotlin.Function1<A, HK<G, B>>, GA: Applicative<G>): HK<G, NonEmptyList<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: NonEmptyListKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: NonEmptyListKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

object NonEmptyListTraverseInstanceImplicits {
    fun instance(): NonEmptyListTraverseInstance = NonEmptyList.Companion.traverse()
}

fun NonEmptyList.Companion.traverse(): NonEmptyListTraverseInstance =
        object : NonEmptyListTraverseInstance, Traverse<NonEmptyListHK> {}

interface NonEmptyListSemigroupKInstance : SemigroupK<NonEmptyListHK> {
    override fun <A> combineK(x: NonEmptyListKind<A>, y: NonEmptyListKind<A>): NonEmptyList<A> =
            x.ev().combineK(y)
}

object NonEmptyListSemigroupKInstanceImplicits {
    fun instance(): NonEmptyListSemigroupKInstance = NonEmptyList.Companion.semigroupK()
}

fun NonEmptyList.Companion.semigroupK(): NonEmptyListSemigroupKInstance =
        object : NonEmptyListSemigroupKInstance, SemigroupK<NonEmptyListHK> {}
