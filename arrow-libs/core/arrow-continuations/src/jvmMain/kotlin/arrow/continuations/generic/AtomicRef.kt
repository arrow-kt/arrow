@file:JvmName("AtomicReferenceActual")

package arrow.continuations.generic

import java.util.concurrent.atomic.AtomicReference

public actual typealias AtomicRef<V> = AtomicReference<V>
