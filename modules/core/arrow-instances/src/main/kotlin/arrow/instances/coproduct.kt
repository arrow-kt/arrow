package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(Coproduct::class)
interface CoproductFunctorInstance<F, G> : Functor<CoproductKindPartial<F, G>> {

    fun FF(): Functor<F>

    fun FG(): Functor<G>

    override fun <A, B> map(fa: CoproductKind<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.reify().map(FF(), FG(), f)
}

@instance(Coproduct::class)
interface CoproductComonadInstance<F, G> : Comonad<CoproductKindPartial<F, G>> {

    fun CF(): Comonad<F>

    fun CG(): Comonad<G>

    override fun <A, B> coflatMap(fa: CoproductKind<F, G, A>, f: (CoproductKind<F, G, A>) -> B): Coproduct<F, G, B> = fa.reify().coflatMap(CF(), CG(), f)

    override fun <A> extract(fa: CoproductKind<F, G, A>): A = fa.reify().extract(CF(), CG())

    override fun <A, B> map(fa: CoproductKind<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.reify().map(CF(), CG(), f)

}

@instance(Coproduct::class)
interface CoproductFoldableInstance<F, G> : Foldable<CoproductKindPartial<F, G>> {

    fun FF(): Foldable<F>

    fun FG(): Foldable<G>

    override fun <A, B> foldLeft(fa: CoproductKind<F, G, A>, b: B, f: (B, A) -> B): B = fa.reify().foldLeft(b, f, FF(), FG())

    override fun <A, B> foldRight(fa: CoproductKind<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.reify().foldRight(lb, f, FF(), FG())

}

@instance(Coproduct::class)
interface CoproductTraverseInstance<F, G> : Traverse<CoproductKindPartial<F, G>> {

    fun TF(): Traverse<F>

    fun TG(): Traverse<G>

    override fun <H, A, B> traverse(fa: CoproductKind<F, G, A>, f: (A) -> Kind<H, B>, GA: Applicative<H>): Kind<H, Coproduct<F, G, B>> =
            fa.reify().traverse(f, GA, TF(), TG())

    override fun <A, B> foldLeft(fa: CoproductKind<F, G, A>, b: B, f: (B, A) -> B): B = fa.reify().foldLeft(b, f, TF(), TG())

    override fun <A, B> foldRight(fa: CoproductKind<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.reify().foldRight(lb, f, TF(), TG())

}