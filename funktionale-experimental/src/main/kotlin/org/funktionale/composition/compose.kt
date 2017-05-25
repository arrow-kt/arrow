package org.funktionale.composition

infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R = forwardCompose(f)

infix fun <IP, R> (() -> IP).forwardCompose(f: (IP) -> R): () -> R {
    return { f(this()) }
}