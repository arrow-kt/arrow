package kategory

import io.reactivex.Observable

interface ObservableMonad : Monad<ObservableKindAdapter.F> {

    override fun <A> pure(a: A): ObservableKind<A> = ObservableKindAdapter(Observable.just(a))

    override fun <A, B> map(fa: ObservableKind<A>, f: (A) -> B): ObservableKind<B> = fa.ev().map(f)

    override fun <A, B> flatMap(fa: ObservableKind<A>, f: (A) -> ObservableKind<B>): ObservableKind<B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKind<Either<A, B>>): ObservableKind<B> {
        return f(a).ev().flatMap { it: Either<A, B> ->
            when (it) {
                is Either.Left<A> -> tailRecM(it.a, f)
                is Either.Right<B> -> pure(it.b)
            }
        }
    }
}
