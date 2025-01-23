@file:JvmName("AtomicActual")

package arrow.atomic

import java.util.concurrent.atomic.AtomicReference

public actual typealias Atomic<V> = AtomicReference<V>
