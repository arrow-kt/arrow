package arrow.syntax.`try`

import kategory.DeprecatedAmbiguity
import kategory.None
import kategory.Option
import kategory.Some

@Deprecated(DeprecatedAmbiguity, ReplaceWith("Try { body }.toOption()"))
inline fun <T> optionTry(body: () -> T): Option<T> = try {
    Some(body())
} catch (e: Throwable) {
    None
}