package org.funktionale.utils

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/05/13
 * Time: 13:06
 */
trait SetterOperation<K, V> {
    val setter: (K, V) -> Unit

    public fun set(key: K, value: V) {
        setter(key, value)
    }
}

class SetterOperationImpl<K, V>(override val setter: (K, V) -> Unit): SetterOperation<K, V>