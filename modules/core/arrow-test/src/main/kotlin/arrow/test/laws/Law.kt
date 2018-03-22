package arrow.test.laws

import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

data class Law(val name: String, val test: () -> Unit)

inline fun <A> A.equalUnderTheLaw(b: A, eq: Eq<A>): Boolean =
        eq.run { eqv(b) }

fun <A> forFew(amount: Int, gena: Gen<A>, fn: (a: A) -> Boolean): Unit {
    for (k in 0..amount) {
        val a = gena.generate()
        val passed = fn(a)
        if (!passed) {
            throw AssertionError("Property failed for\n$a)")
        }
    }
}

fun <A, B> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean): Unit {
    for (k in 0..amount) {
        val a = gena.generate()
        val b = genb.generate()
        val passed = fn(a, b)
        if (!passed) {
            throw AssertionError("Property failed for\n$a\n$b)")
        }
    }
}

fun <A, B, C> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
    for (k in 0..amount) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val passed = fn(a, b, c)
        if (!passed) {
            throw AssertionError("Property failed for\n$a\n$b\n$c)")
        }
    }
}

fun <A, B, C, D> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
    for (k in 0..amount) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val passed = fn(a, b, c, d)
        if (!passed) {
            throw AssertionError("Property failed for\n$a\n$b\n$c\n$d)")
        }
    }
}

fun <A, B, C, D, E> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
    for (k in 0..amount) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val e = gene.generate()
        val passed = fn(a, b, c, d, e)
        if (!passed) {
            throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\$e)")
        }
    }
}