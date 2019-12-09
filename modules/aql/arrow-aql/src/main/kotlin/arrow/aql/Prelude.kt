package arrow.aql

import arrow.Kind

typealias Source<F, A> = Kind<F, A>
typealias Selection<A, Z> = A.() -> Z
