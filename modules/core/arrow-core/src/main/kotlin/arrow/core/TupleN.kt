package arrow.core

import arrow.*

@higherkind
data class Tuple2<out A, out B>(val a: A, val b: B) : Tuple2Of<A, B> {
    fun <C> map(f: (B) -> C) =
            a toT f(b)

    fun <C> ap(f: Tuple2Of<*, (B) -> C>) =
            map(f.fix().b)

    fun <C> flatMap(f: (B) -> Tuple2Of<@UnsafeVariance A, C>) =
            f(b).fix()

    fun <C> coflatMap(f: (Tuple2Of<A, B>) -> C) =
            a toT f(this)

    fun extract() =
            b

    fun <C> foldL(b: C, f: (C, B) -> C) =
            f(b, this.b)

    fun <C> foldR(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>) =
            f(b, lb)

    fun reverse(): Tuple2<B, A> = Tuple2(b, a)

    companion object
}

data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) {
    fun reverse(): Tuple3<C, B, A> = Tuple3(c, b, a)
    companion object
}

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) {
    fun reverse(): Tuple4<D, C, B, A> = Tuple4(d, c, b, a)
    companion object
}

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
    fun reverse(): Tuple5<E, D, C, B, A> = Tuple5(e, d, c, b, a)
    companion object
}

data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) {
    fun reverse(): Tuple6<F, E, D, C, B, A> = Tuple6(f, e, d, c, b, a)
    companion object
}

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G) {
    fun reverse(): Tuple7<G, F, E, D, C, B, A> = Tuple7(g, f, e, d, c, b, a)
    companion object
}

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H) {
    fun reverse(): Tuple8<H, G, F, E, D, C, B, A> = Tuple8(h, g, f, e, d, c, b, a)
    companion object
}

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G,
                                                                                 val h: H, val i: I) {
    fun reverse(): Tuple9<I, H, G, F, E, D, C, B, A> = Tuple9(i, h, g, f, e, d, c, b, a)
    companion object
}

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G,
                                                                                         val h: H, val i: I, val j: J) {
    fun reverse(): Tuple10<J, I, H, G, F, E, D, C, B, A> = Tuple10(j, i, h, g, f, e, d, c, b, a)
    companion object
}

infix fun <A, B> A.toT(b: B): Tuple2<A, B> = Tuple2(this, b)
