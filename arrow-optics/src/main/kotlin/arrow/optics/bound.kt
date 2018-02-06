package arrow.optics

import arrow.core.Option

class BoundSetter<S, A>(val value: S, val setter: Setter<S, A>) {

    fun <T> compose(other: Setter<A, T>)    = BoundSetter(value, setter + other)
    fun <T> compose(other: Optional<A, T>)  = BoundSetter(value, setter + other)
    fun <T> compose(other: Prism<A, T>)     = BoundSetter(value, setter + other)
    fun <T> compose(other: Lens<A, T>)      = BoundSetter(value, setter + other)
    fun <T> compose(other: Iso<A, T>)       = BoundSetter(value, setter + other)
    fun <T> compose(other: Traversal<A, T>) = BoundSetter(value, setter + other)

    fun modify(f: (A) -> A) = setter.modify(value, f)

    fun set(a: A) = setter.set(value, a)
}

fun <T> T.setter() = BoundSetter(this, Setter.id())

val <T, A> BoundSetter<T, A?>.nullable get(): BoundSetter<T, A> = compose(nullableOptional())

val <T, A> BoundSetter<T, Option<A>>.some get(): BoundSetter<T, A> = compose(optionOptional())

