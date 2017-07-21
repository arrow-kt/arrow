package kategory

interface FunctionInject<out R> {
    operator fun <P> invoke(p: P): R
}

interface FunctionProject<in P> {
    operator fun <R> invoke(p: P): R
}