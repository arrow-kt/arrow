package katz.data

import katz.Functor
import katz.HK
import katz.Monad

class Kleisli<F, D, out A>(val run: (D) -> HK<F, A>) {

    fun <B> map(ft: Functor<F>, f: (A) -> B): Kleisli<F, D, B> = Kleisli { a -> ft.map(run(a), f) }

    fun <B> flatMap(m: Monad<F>, f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> = Kleisli {
        d -> m.flatMap(run(d)) { a -> f(a).run(d) }
    }

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli { dd -> run(f(dd)) }
}

typealias ReaderT<F, D, A> = Kleisli<F, D, A>
