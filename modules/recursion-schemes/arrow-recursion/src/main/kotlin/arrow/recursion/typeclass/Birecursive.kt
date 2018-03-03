package arrow.recursion.typeclass

import arrow.TC

interface Birecursive<F, G> : TC, Recursive<F, G>, Corecursive<F, G>