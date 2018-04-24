package arrow.recursion.typeclass

interface Birecursive<F, G> : Recursive<F, G>, Corecursive<F, G>