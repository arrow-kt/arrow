package arrow.syntax.`try`

import arrow.DeprecatedAmbiguity
import arrow.None
import arrow.Option
import arrow.Some

@Deprecated(DeprecatedAmbiguity, ReplaceWith("Try { body }.toOption()"))
inline fun <T> optionTry(body: () -> T): Option<T> = try {
    Some(body())
} catch (e: Throwable) {
    None
}