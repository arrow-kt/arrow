package katz.data

sealed class Trampoline<out A> {

    class More<out A>(val f: () -> Trampoline<A>) : Trampoline<A>()
    class Done<out A>(val result: A) : Trampoline<A>()

    fun runT(): A = when (this) {
        is More -> f().runT()
        is Done -> result
    }
}
