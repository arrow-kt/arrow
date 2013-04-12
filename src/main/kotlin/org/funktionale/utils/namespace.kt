package org.funktionale.utils

val<T> identity = {(t: T) -> t }

fun<P1, T> constant(t: T): (P1) -> T {
    return {(p1: P1) -> t }
}
