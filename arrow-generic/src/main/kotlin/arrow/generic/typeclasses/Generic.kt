package arrow.generic.typeclasses

import arrow.*

@typeclass
@higherkind
interface Generic<T, Repr> : TC, GenericKind<T, Repr> {
    fun from(r: Repr): T
    fun to(t: T): Repr
}