package arrow.mtl.typeclasses

import arrow.instance
import arrow.typeclasses.Invariant
import arrow.typeclasses.Monoid

@instance(Invariant::class)
interface InvariantMonoidInstance<T> : Invariant<Monoid<T>> {
  fun <A, B> Monoid<A>.imap(f: (A) -> B, fi: (B) -> A): Monoid<B>
}

// TODO("Pending response if correct")