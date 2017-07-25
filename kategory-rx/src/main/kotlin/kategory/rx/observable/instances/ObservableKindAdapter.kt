package kategory

import io.reactivex.Observable

typealias ObservableKind<A> = HK<ObservableKindAdapter.F, A>

fun <A> ObservableKind<A>.ev(): ObservableKindAdapter<A> = this as ObservableKindAdapter<A>
fun <A> ObservableKind<A>.value(): Observable<A> = this.ev().value

data class ObservableKindAdapter<A>(val value: Observable<A>) : ObservableKind<A> {

    class F private constructor()

    inline fun <B> map(noinline f: (A) -> B): ObservableKind<B> =
            ObservableKindAdapter(value.map { f(it) })

    inline fun <B> flatMap(noinline f: (A) -> ObservableKind<B>): ObservableKind<B> =
            ObservableKindAdapter(value.flatMap { f(it).value() })

    inline fun isEmpty(): Boolean = value.isEmpty.blockingGet()

    inline fun first(): A = value.blockingFirst()

    inline fun <B> foldLeft(b: B, noinline f: (B, A) -> B): Eval<B> {
        val r = value.reduce(b) { t1: B, t2: A -> f(t1, t2) }
        return Eval.later { r.blockingGet() }
    }
}
