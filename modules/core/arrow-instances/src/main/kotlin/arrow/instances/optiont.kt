package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(OptionT::class)
interface OptionTFunctorInstance<F> : Functor<OptionTPartialOf<F>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: OptionTOf<F, A>, f: (A) -> B): OptionT<F, B> = fa.extract().map(f, FF())

}

@instance(OptionT::class)
interface OptionTApplicativeInstance<F> : OptionTFunctorInstance<F>, Applicative<OptionTPartialOf<F>> {

    override fun FF(): Monad<F>

    override fun <A> pure(a: A): OptionT<F, A> = OptionT(FF().pure(Option(a)))

    override fun <A, B> map(fa: OptionTOf<F, A>, f: (A) -> B): OptionT<F, B> = fa.extract().map(f, FF())

    override fun <A, B> ap(fa: OptionTOf<F, A>, ff: OptionTOf<F, (A) -> B>): OptionT<F, B> =
            fa.extract().ap(ff, FF())
}

@instance(OptionT::class)
interface OptionTMonadInstance<F> : OptionTApplicativeInstance<F>, Monad<OptionTPartialOf<F>> {

    override fun <A, B> map(fa: OptionTOf<F, A>, f: (A) -> B): OptionT<F, B> = fa.extract().map(f, FF())

    override fun <A, B> flatMap(fa: OptionTOf<F, A>, f: (A) -> OptionTOf<F, B>): OptionT<F, B> = fa.extract().flatMap({ f(it).extract() }, FF())

    override fun <A, B> ap(fa: OptionTOf<F, A>, ff: OptionTOf<F, (A) -> B>): OptionT<F, B> =
            fa.extract().ap(ff, FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> OptionTOf<F, Either<A, B>>): OptionT<F, B> =
            OptionT.tailRecM(a, f, FF())

}

fun <F, A, B> OptionT<F, A>.foldLeft(b: B, f: (B, A) -> B, FF: Foldable<F>): B = FF.compose(Option.foldable()).foldLC(value, b, f)

fun <F, A, B> OptionT<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>): Eval<B> = FF.compose(Option.foldable()).foldRC(value, lb, f)

fun <F, G, A, B> OptionT<F, A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>, FF: Traverse<F>): Kind<G, OptionT<F, B>> {
    val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).traverseC(value, f, GA)
    return GA.map(fa, { OptionT(FF.map(it.unnest(), { it.extract() })) })
}

@instance(OptionT::class)
interface OptionTFoldableInstance<F> : Foldable<OptionTPartialOf<F>> {

    fun FFF(): Foldable<F>

    override fun <A, B> foldLeft(fa: OptionTOf<F, A>, b: B, f: (B, A) -> B): B = fa.extract().foldLeft(b, f, FFF())

    override fun <A, B> foldRight(fa: OptionTOf<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.extract().foldRight(lb, f, FFF())

}

@instance(OptionT::class)
interface OptionTTraverseInstance<F> : OptionTFoldableInstance<F>, Traverse<OptionTPartialOf<F>> {

    override fun FFF(): Traverse<F>

    override fun <G, A, B> traverse(fa: OptionTOf<F, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, OptionT<F, B>> =
            fa.extract().traverse(f, GA, FFF())

}

@instance(OptionT::class)
interface OptionTSemigroupKInstance<F> : SemigroupK<OptionTPartialOf<F>> {

    fun FF(): Monad<F>

    override fun <A> combineK(x: OptionTOf<F, A>, y: OptionTOf<F, A>): OptionT<F, A> = x.extract().orElse({ y.extract() }, FF())
}

@instance(OptionT::class)
interface OptionTMonoidKInstance<F> : MonoidK<OptionTPartialOf<F>>, OptionTSemigroupKInstance<F> {
    override fun <A> empty(): OptionT<F, A> = OptionT(FF().pure(None))
}
