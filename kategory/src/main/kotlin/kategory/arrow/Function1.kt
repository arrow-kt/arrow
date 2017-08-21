package kategory

fun <I, O> ((I) -> O).k(): Function1<I, O> = Function1(this)

operator fun <I, O> Function1Kind<I, O>.invoke(i: I): O = this.ev().f(i)

@higherkind open class Function1<I, out O>(val f: (I) -> O) : Function1Kind<I, O> {

    companion object {

        fun <P> functor() = object : Function1Instances<P> {}

        fun <P> applicative() = object : Function1Instances<P> {}

        fun <P> monad() = object : Function1Instances<P> {}

        fun <P> monadReader() = object : Function1Instances<P> {}
    }
}
