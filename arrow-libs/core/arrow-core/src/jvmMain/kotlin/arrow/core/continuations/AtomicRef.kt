@file:JvmName("AtomicReferenceActual")

package arrow.core.continuations

import java.util.concurrent.atomic.AtomicReference

public actual typealias AtomicRef<V> = AtomicReference<V>
