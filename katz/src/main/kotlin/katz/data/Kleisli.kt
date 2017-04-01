package katz.data

import katz.Functor
import katz.HK
import katz.Monad

class Kleisli<F, D, out A>(val run: (D) -> HK<F, A>) {

    fun <B> map(ft: Functor<F>, f: (A) -> B): Kleisli<F, D, B> = Kleisli { ft.map(run(it), f) }

    fun <B> flatMap(m: Monad<F>, f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> = Kleisli {
        m.flatMap(run(it)) { a -> f(a).run(it) }
    }

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli { run(f(it)) }
}

typealias ReaderT<F, D, A> = Kleisli<F, D, A>
