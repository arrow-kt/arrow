package kategory.rx.observable.instances

import kategory.*

class ObservableTraverse() : Traverse<ObservableKindAdapter.F> {
    override fun <G, A, B> traverse(fa: ObservableKind<A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, ObservableKind<B>> {
        TODO()
    }

    override fun <A, B> foldL(fa: ObservableKind<A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldLeft(b, f).value()

    override fun <A, B> foldR(fa: ObservableKind<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        TODO()
    }
}