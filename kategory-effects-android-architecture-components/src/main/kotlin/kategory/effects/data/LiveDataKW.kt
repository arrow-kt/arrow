package kategory.effects

import kategory.*
import kotlin.coroutines.experimental.CoroutineContext
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

fun <A> LiveData<A>.k() = LiveDataKW(this)

fun <A> LiveDataKWKind<A>.value(): LiveData<A> = (this as LiveDataKW<A>).liveData

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class
)
data class LiveDataKW<A>(val liveData: LiveData<A>) : LiveDataKWKind<A> {

    fun <B> map(f: (A) -> B): LiveDataKW<B> =
            android.arch.lifecycle.Transformations.map<A, B>(liveData, { f(it) }).k()

    fun <B> ap(fa: LiveDataKWKind<(A) -> B>): LiveDataKW<B> =
            this.flatMap { a: A -> fa.ev().map { ff: (A) -> B -> ff(a) } }

    fun <B> flatMap(f: (A) -> LiveDataKWKind<B>): LiveDataKW<B> =
            android.arch.lifecycle.Transformations.switchMap<A, B>(liveData, { value: A -> f(value).value() }).k()

    companion object {
        fun <A> pure(a: A): LiveDataKW<A> =
                LiveDataKW<A>(MutableLiveData<A>().apply { postValue(a) })

        fun <A, B> tailRecM(a: A, f: (A) -> LiveDataKWKind<Either<A, B>>): LiveDataKW<B> =
                LiveDataKW(MutableLiveData())
        /*        f(a).value().let { initial: LiveData<Either<A, B>> ->
                    var current: LiveData<Either<A, B>> = initial
                    async(Unconfined, CoroutineStart.LAZY) {
                        val result: B
                        while (true) {
                            val actual: Either<A, B> = current.await()
                            if (actual is Right) {
                                result = actual.b
                                break
                            } else if (actual is Left) {
                                current = f(actual.a).ev()
                            }
                        }
                        result
                    }.k()
                }
                */
    }
}
