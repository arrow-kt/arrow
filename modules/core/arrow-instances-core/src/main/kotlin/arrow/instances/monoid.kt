package arrow.instances

import arrow.Kind
import arrow.instance
import arrow.typeclasses.*

@instance(Monoid::class)
interface MonoidInvariantInstance<A> : Invariant<ForMonoid> {
    override fun <A, B> Kind<ForMonoid, A>.imap(f: (A) -> B, g: (B) -> A): Monoid<B> =
        object : Monoid<B> {
            override fun empty(): B = f(this@imap.fix().empty())

            override fun B.combine(b: B): B {
                val lhs = this
                return f(this@imap.fix().run { g(lhs).combine(g(b)) })
            }
        }
}