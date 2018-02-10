package arrow.instances

import arrow.Kind
import arrow.core.Eval
import arrow.data.Coproduct
import arrow.data.CoproductOf
import arrow.data.CoproductPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.*

@instance(Coproduct::class)
interface CoproductFunctorInstance<F, G> : Functor<CoproductPartialOf<F, G>> {

    fun FF(): Functor<F>

    fun FG(): Functor<G>

    override fun <A, B> map(fa: CoproductOf<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.fix().map(FF(), FG(), f)
}

@instance(Coproduct::class)
interface CoproductComonadInstance<F, G> : Comonad<CoproductPartialOf<F, G>> {

    fun CF(): Comonad<F>

    fun CG(): Comonad<G>

    override fun <A, B> coflatMap(fa: CoproductOf<F, G, A>, f: (CoproductOf<F, G, A>) -> B): Coproduct<F, G, B> = fa.fix().coflatMap(CF(), CG(), f)

    override fun <A> extractM(fa: CoproductOf<F, G, A>): A = fa.fix().extractM(CF(), CG())

    override fun <A, B> map(fa: CoproductOf<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.fix().map(CF(), CG(), f)

}

@instance(Coproduct::class)
interface CoproductFoldableInstance<F, G> : Foldable<CoproductPartialOf<F, G>> {

    fun FF(): Foldable<F>

    fun FG(): Foldable<G>

    override fun <A, B> foldLeft(fa: CoproductOf<F, G, A>, b: B, f: (B, A) -> B): B = fa.fix().foldLeft(b, f, FF(), FG())

    override fun <A, B> foldRight(fa: CoproductOf<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.fix().foldRight(lb, f, FF(), FG())

}

@instance(Coproduct::class)
interface CoproductTraverseInstance<F, G> : Traverse<CoproductPartialOf<F, G>> {

    fun TF(): Traverse<F>

    fun TG(): Traverse<G>

    override fun <H, A, B> traverse(fa: CoproductOf<F, G, A>, f: (A) -> Kind<H, B>, GA: Applicative<H>): Kind<H, Coproduct<F, G, B>> =
            fa.fix().traverse(f, GA, TF(), TG())

    override fun <A, B> foldLeft(fa: CoproductOf<F, G, A>, b: B, f: (B, A) -> B): B = fa.fix().foldLeft(b, f, TF(), TG())

    override fun <A, B> foldRight(fa: CoproductOf<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.fix().foldRight(lb, f, TF(), TG())

}