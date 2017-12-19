package arrow.syntax.collections

import arrow.syntax.function.toOption
import kategory.GetterOperation
import kategory.GetterOperationImpl
import kategory.Option

val <K, V> Map<K, V>.option: GetterOperation<K, Option<V>>
    get() {
        return GetterOperationImpl { k -> this[k].toOption() }
    }