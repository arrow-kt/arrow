package arrow.optics

class BoundSetter<S, A>(private val value: S, private val setter: Setter<S, A>) {

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