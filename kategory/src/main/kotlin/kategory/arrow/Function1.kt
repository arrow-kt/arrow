package kategory

typealias Function1Kind<I, O> = HK2<Function1.F, I, O>
typealias Function1F<I> = HK<Function1.F, I>

fun <I, O> ((I) -> O).k(): Function1<I, O> = Function1(this)

fun <I, O> Function1Kind<I, O>.ev(): Function1<I, O> = this as Function1<I, O>

operator fun <I, O> Function1Kind<I, O>.invoke(i: I): O = this.ev().f(i)

class Function1<I, out O>(val f: (I) -> O) : Function1Kind<I, O> {

    class F private constructor()

    companion object {

        fun <P> functor() = object : Function1Instances<P> {}

        fun <P> applicative() = object : Function1Instances<P> {}

        fun <P> monad() = object : Function1Instances<P> {}

        fun <P> monadReader() = object : Function1Instances<P> {}
    }
}