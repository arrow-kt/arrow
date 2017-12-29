package arrow.recursion.typeclass

import arrow.Typeclass

interface Birecursive<F, G> : Typeclass, Recursive<F, G>, Corecursive<F, G>