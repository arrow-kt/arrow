package arrow.core

public fun <P1, T> constant(t: T): (P1) -> T = { _: P1 -> t }

public typealias Predicate<T> = (T) -> Boolean

public fun <T : Any> Predicate<T>.mapNullable(): (T?) -> Boolean = { t -> t?.let { this@mapNullable(it) } ?: false }

public const val DeprecatedUnsafeAccess: String = "This function is unsafe and will be removed in future versions of Arrow. Replace or import `arrow.syntax.unsafe.*` if you wish to continue using it in this way"
public const val DeprecatedAmbiguity: String = "This function is ambiguous and will be removed in future versions of Arrow"
