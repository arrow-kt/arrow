package arrow.mtl

import arrow.*
import arrow.core.Tuple2
import arrow.typeclasses.Monad

/** A monad that support monoidal accumulation (e.g. logging List<String>) */
@typeclass
interface MonadWriter<F, W> : Monad<F>, TC {

    /** Lift a writer action into the effect */
    fun <A> writer(aw: Tuple2<W, A>): Kind<F, A>

    /** Run the effect and pair the accumulator with the result */
    fun <A> listen(fa: Kind<F, A>): Kind<F, Tuple2<W, A>>

    /** Apply the effectful function to the accumulator */
    fun <A> pass(fa: Kind<F, Tuple2<(W) -> W, A>>): Kind<F, A>

    /** Lift the log into the effect */
    fun tell(w: W): Kind<F, Unit> = writer(Tuple2(w, Unit))

    /** Pair the value with an inspection of the accumulator */
    fun <A, B> listens(fa: Kind<F, A>, f: (W) -> B): Kind<F, Tuple2<B, A>> = map(listen(fa)) { Tuple2(f(it.a), it.b) }

    /** Modify the accumulator */
    fun <A> censor(fa: Kind<F, A>, f: (W) -> W): Kind<F, A> = flatMap(listen(fa)) { writer(Tuple2(f(it.a), it.b)) }

    companion object {

        inline fun <reified F, reified W> invoke(MWF: MonadWriter<F, W> = monadWriter()) = MWF
    }
}