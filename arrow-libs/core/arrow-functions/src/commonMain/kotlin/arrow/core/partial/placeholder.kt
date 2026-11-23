package arrow.core.partial

public data object Placeholder

/** Use it and rename it as you wish
 *
 * ```
 * import arrow.core.partial.placeholder as _arg
 * ```
 */
public val placeholder: Placeholder = Placeholder
public val `__`: Placeholder = Placeholder

public inline operator fun <A, B, C> ((A, B) -> C).invoke(a: A, b: Placeholder): (B) -> C = { b -> this(a, b) }

public inline operator fun <A, B, C> (suspend (A, B) -> C).invoke(a: A, b: Placeholder): suspend (B) -> C = { b -> this(a, b) }

public inline operator fun <A, B, C, D> ((A, B, C) -> D).invoke(a: A, b: Placeholder, c: Placeholder): (B, C) -> D = { b, c -> this(a, b, c) }
public inline operator fun <A, B, C, D> ((A, B, C) -> D).invoke(a: A, b: B, c: Placeholder): (C) -> D = { c -> this(a, b, c) }

public inline operator fun <A, B, C, D> (suspend (A, B, C) -> D).invoke(a: A, b: Placeholder, c: Placeholder): suspend (B, C) -> D = { b, c -> this(a, b, c) }
public inline operator fun <A, B, C, D> (suspend (A, B, C) -> D).invoke(a: A, b: B, c: Placeholder): suspend (C) -> D = { c -> this(a, b, c) }

public inline operator fun <A, B, C, D, E> ((A, B, C, D) -> E).invoke(a: A, b: Placeholder, c: Placeholder, d: Placeholder): (B, C, D) -> E = { b, c, d -> this(a, b, c, d) }
public inline operator fun <A, B, C, D, E> ((A, B, C, D) -> E).invoke(a: A, b: B, c: Placeholder, d: Placeholder): (C, D) -> E = { c, d -> this(a, b, c, d) }
public inline operator fun <A, B, C, D, E> ((A, B, C, D) -> E).invoke(a: A, b: B, c: C, d: Placeholder): (D) -> E = { d -> this(a, b, c, d) }

public inline operator fun <A, B, C, D, E> (suspend (A, B, C, D) -> E).invoke(a: A, b: Placeholder, c: Placeholder, d: Placeholder): suspend (B, C, D) -> E = { b, c, d -> this(a, b, c, d) }
public inline operator fun <A, B, C, D, E> (suspend (A, B, C, D) -> E).invoke(a: A, b: B, c: Placeholder, d: Placeholder): suspend (C, D) -> E = { c, d -> this(a, b, c, d) }
public inline operator fun <A, B, C, D, E> (suspend (A, B, C, D) -> E).invoke(a: A, b: B, c: C, d: Placeholder): suspend (D) -> E = { d -> this(a, b, c, d) }
