package kategory

typealias TrampolineKind<A> = HK<Trampoline.F, A>

fun <A> TrampolineKind<A>.ev(): Trampoline<A> =
        this as Trampoline<A>

/**
 * Trampoline is often used to emulate tail recursion. The idea is to have some step code that can be trampolined itself
 * to emulate recursion. The difference with standard recursion would be that there is no need to rewind the whole stack
 * when we reach the end of the stack, since the first value returned that is not a trampoline would be directly
 * returned as the overall result value for the whole function chain. That means Trampoline emulates what tail recursion
 * does.
 */
sealed class Trampoline<out A> : TrampolineKind<A> {

    class F private constructor()

    class More<out A>(val f: () -> Trampoline<A>) : Trampoline<A>()
    class Done<out A>(val result: A) : Trampoline<A>()

    fun runT(): A = when (this) {
        is More -> f().runT()
        is Done -> result
    }
}
