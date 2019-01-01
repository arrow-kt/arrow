package arrow.free

import arrow.core.*
import arrow.core.FunctionK
import arrow.instances.function0.monad.monad

/**
 * Trampoline is often used to emulate tail recursion. The idea is to have some step code that can be trampolined itself
 * to emulate recursion. The difference with standard recursion would be that there is no need to rewind the whole stack
 * when we reach the end of the stack, since the first value returned that is not a trampoline would be directly
 * returned as the overall result value for the whole function chain. That means Trampoline emulates what tail recursion
 * does.
 */
typealias TrampolineF<A> = Free<ForFunction0, A>

object Trampoline : TrampolineFunctions

interface TrampolineFunctions {

  fun <A> done(a: A): TrampolineF<A> = Free.just<ForFunction0, A>(a)

  fun <A> defer(a: () -> TrampolineF<A>): TrampolineF<A> = Free.defer(a)

  fun <A> delay(a: () -> A): TrampolineF<A> = defer { done(a()) }
}

fun <A> TrampolineF<A>.runT(): A = this.foldMap(FunctionK.id(), Function0.monad()).fix().invoke()