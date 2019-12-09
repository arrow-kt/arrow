package arrow.generic.coproduct2

import arrow.core.Option
import arrow.core.toOption
import kotlin.Suppress
import kotlin.Unit

sealed class Coproduct2<A, B>

internal data class First<A, B>(val a: A) : Coproduct2<A, B>()
internal data class Second<A, B>(val b: B) : Coproduct2<A, B>()

fun <A> Coproduct2<A, *>.select(): Option<A> = (this as? First)?.a.toOption()

@Suppress("UNUSED_PARAMETER")
fun <B> Coproduct2<*, B>.select(dummy0: Unit = Unit): Option<B> = (this as? Second)?.b.toOption()

fun <A, B, RESULT> Coproduct2<A, B>.fold(a: (A) -> RESULT, b: (B) -> RESULT): RESULT = when (this) {
    is First -> a(this.a)
    is Second -> b(this.b)
}
