package arrow.syntax.collections

import arrow.syntax.function.toOption
import arrow.GetterOperation
import arrow.GetterOperationImpl
import arrow.Option

val <K, V> Map<K, V>.option: GetterOperation<K, Option<V>>
    get() {
        return GetterOperationImpl { k -> this[k].toOption() }
    }