package kategory.effects

import kategory.*
import kotlin.coroutines.experimental.CoroutineContext
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations

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
            Transformations.map(liveData, { f(it) }).k()

    fun <B> ap(fa: LiveDataKWKind<(A) -> B>): LiveDataKW<B> =
            this.flatMap { a: A -> fa.ev().map { ff: (A) -> B -> ff(a) } }

    fun <B> flatMap(f: (A) -> LiveDataKWKind<B>): LiveDataKW<B> =
            Transformations.switchMap(liveData, { value: A -> f(value).value() }).k()

    companion object {
        fun <A> pure(a: A): LiveDataKW<A> =
                LiveDataKW(MutableLiveData<A>().apply { setValue(a) })

        fun <A, B> tailRecM(a: A, f: (A) -> LiveDataKWKind<Either<A, B>>): LiveDataKW<B> =
                f(a).ev().flatMap {
                    it.fold({ tailRecM(a, f).ev() }, { LiveDataKW.pure(it).ev() })
                }
    }
}
