package kategory

interface FunctionInject<out R> {
    fun <P> invokeInject(p: P): R
}

interface FunctionSurject<in P> {
    fun <R> invokeSurject(p: P): R
}