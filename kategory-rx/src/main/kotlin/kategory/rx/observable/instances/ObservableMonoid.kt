package kategory

import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class ObservableMonoid<A>(val monoid: Monoid<A>) : Monoid<Observable<A>> {

    override fun empty(): Observable<A> = Observable.just(monoid.empty())

    override fun combine(a: Observable<A>, b: Observable<A>): Observable<A> = Observable.zip(a, b, BiFunction(monoid::combine))
}
