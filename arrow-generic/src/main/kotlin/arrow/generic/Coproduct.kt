package arrow.generic

class Coproduct<A, B, C> {

    object companion {
        operator fun <A, B, C> invoke(a: A): Coproduct<A, B, C> =

    }

}
