package arrow.collections

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

fun <T> T?.toOption(): Option<T> = if (this != null) {
    Some(this)
} else {
    None
}