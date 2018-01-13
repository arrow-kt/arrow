package arrow.free

import arrow.*
import arrow.core.*
import arrow.typeclasses.*

typealias CofreeEval<S, A> = HK<S, Cofree<S, A>>

@higherkind data class Cofree<S, A>(val FS: Functor<S>, val head: A, val tail: Eval<CofreeEval<S, A>>) : CofreeKind<S, A>, CofreeKindedJ<S, A> {

    fun tailForced(): CofreeEval<S, A> = tail.value()

    inline fun <B> transform(f: (A) -> B, noinline g: (Cofree<S, A>) -> Cofree<S, B>): Cofree<S, B> = Cofree(FS, f(head), tail.map { FS.map(it, g) })

    fun <B> map(f: (A) -> B): Cofree<S, B> = transform(f, { it.map(f) })

    fun mapBranchingRoot(fk: FunctionK<S, S>): Cofree<S, A> = Cofree(FS, head, tail.map { fk(it) })

    // Due to the recursive nature of this function, it cannot be reified for FT to use functor<T>()
    fun <T> mapBranchingS(fk: FunctionK<S, T>, FT: Functor<T>): Cofree<T, A> = Cofree(FT, head, tail.map { ce -> fk(FS.map(ce, { it.mapBranchingS(fk, FT) })) })

    // Due to the recursive nature of this function, it cannot be reified for FT to use functor<T>()
    fun <T> mapBranchingT(fk: FunctionK<S, T>, FT: Functor<T>): Cofree<T, A> = Cofree(FT, head, tail.map { ce -> FT.map(fk(ce), { it.mapBranchingT(fk, FT) }) })

    fun <B> coflatMap(f: (Cofree<S, A>) -> B): Cofree<S, B> = Cofree(FS, f(this), tail.map { FS.map(it, { coflatMap(f) }) })

    fun duplicate(): Cofree<S, Cofree<S, A>> = Cofree(FS, this, tail.map { FS.map(it, { duplicate() }) })

    fun runTail(): Cofree<S, A> = Cofree(FS, head, Eval.now(tail.value()))

    fun run(): Cofree<S, A> = Cofree(FS, head, Eval.now(tail.map { FS.map(it, { it.run() }) }.value()))

    fun extract(): A = head

    companion object {
        inline fun <reified S, A> unfold(a: A, noinline f: (A) -> HK<S, A>, FS: Functor<S> = arrow.typeclasses.functor<S>()): Cofree<S, A> = create(a, f, FS)

        fun <S, A> create(a: A, f: (A) -> HK<S, A>, FS: Functor<S>): Cofree<S, A> = Cofree(FS, a, Eval.later { FS.map(f(a), { create(it, f, FS) }) })

    }
}

fun <F, A, B> Cofree<F, A>.cata(folder: (A, HK<F, B>) -> Eval<B>, TF: Traverse<F>): Eval<B> {
    val ev: Eval<HK<F, B>> = TF.traverse(this.tailForced(), { it.cata(folder, TF) }, applicative()).ev()
    return ev.flatMap { folder(extract(), it) }
}

fun <F, M, A, B> Cofree<F, A>.cataM(folder: (A, HK<F, B>) -> HK<M, B>, inclusion: FunctionK<EvalHK, M>, TF: Traverse<F>, MM: Monad<M>): HK<M, B> {
    fun loop(ev: Cofree<F, A>): Eval<HK<M, B>> {
        val looped: HK<M, HK<F, B>> = TF.traverse(ev.tailForced(), { MM.flatten(inclusion(Eval.defer { loop(it) })) }, MM)
        val folded: HK<M, B> = MM.flatMap(looped) { fb -> folder(ev.head, fb) }
        return Eval.now(folded)
    }
    return MM.flatten(inclusion(loop(this)))
}
