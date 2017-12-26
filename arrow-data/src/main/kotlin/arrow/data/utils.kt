package arrow

fun <P1, T> constant(t: T): (P1) -> T = { _: P1 -> t }

typealias Predicate<T> = (T) -> Boolean

fun <T : Any> Predicate<T>.mapNullable(): (T?) -> Boolean = { it?.let { this@mapNullable(it) } ?: false }

inline fun <T> T?.hashCodeForNullable(i: Int, f: (Int, Int) -> Int): Int = when (this) {
        null -> i
        else -> f(i, this.hashCode())
    }

interface GetterOperation<in K, out V> {
    val getter: (K) -> V
    operator fun get(key: K): V = getter(key)
}

class GetterOperationImpl<in K, out V>(override val getter: (K) -> V) : GetterOperation<K, V>

const val DeprecatedUnsafeAccess: String = "This function is unsafe and will be removed in future versions of Arrow. Replace or import `arrow.syntax.unsafe.*` if you wish to continue using it in this way"
const val DeprecatedAmbiguity: String = "This function is ambiguous and will be removed in future versions of Arrow"