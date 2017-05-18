package katz

import java.util.Vector

inline fun <reified A> VectorMonoid(): Monoid<Vector<A>> = object : Monoid<Vector<A>>, GlobalInstance<Monoid<Vector<A>>>() {
    override fun empty(): Vector<A> = Vector()

    override fun combine(a: Vector<A>, b: Vector<A>): Vector<A> = a.apply { addAll(b) }
}