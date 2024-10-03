package arrow.match

import arrow.core.Tuple4
import arrow.core.Tuple5
import kotlin.reflect.KProperty1

/**
 * A matcher is a [KProperty1] which may throw [DoesNotMatch]
 * to signal that it does not match the value.
 */
public typealias Matcher<S, A> = KProperty1<S, A>

public expect fun <S, A> Matcher(
  name: String,
  get: (S) -> A
): Matcher<S, A>

public expect class DoesNotMatch(): Throwable

public fun <S> identity(): Matcher<S, S> = Matcher("identity") { it }

public inline fun <S, reified A: S> instanceOf(): Matcher<S, A> =
  Matcher("instanceOf<${A::class.simpleName}>") {
    if (it is A) it else throw DoesNotMatch()
  }

public fun <S, A> Matcher<S, A>.takeIf(
  description: String? = null,
  predicate: (A) -> Boolean
): Matcher<S, A> = Matcher("${this.name}.${description ?: "suchThat"}") {
  val value = this.get(it)
  if (predicate(value)) value else throw DoesNotMatch()
}

public val <S, A> Matcher<S, Collection<A>>.isNotEmpty: Matcher<S, Collection<A>>
  get() = this.takeIf("isNotEmpty") { it.isNotEmpty() }

public fun <S, A, B> Matcher<S, A>.of(
  field: Matcher<A, B>
): Matcher<S, B> = Matcher("${this.name}.of(${field.name})") {
  field.get(this.get(it))
}

public fun <S, A, B, C> Matcher<S, A>.of(
  field1: Matcher<A, B>,
  field2: Matcher<A, C>
): Matcher<S, Pair<B, C>> = Matcher(
  "${this.name}.of(${field1.name}, ${field2.name})"
) {
  val a = this.get(it)
  Pair(field1.get(a), field2.get(a))
}

public fun <S, A, B, C, D> Matcher<S, A>.of(
  field1: Matcher<A, B>,
  field2: Matcher<A, C>,
  field3: Matcher<A, D>
): Matcher<S, Triple<B, C, D>> = Matcher(
  "${this.name}.of(${field1.name}, ${field2.name}, ${field3.name})"
) {
  val a = this.get(it)
  Triple(field1.get(a), field2.get(a), field3.get(a))
}

public fun <S, A, B, C, D, E> Matcher<S, A>.of(
  field1: Matcher<A, B>,
  field2: Matcher<A, C>,
  field3: Matcher<A, D>,
  field4: Matcher<A, E>
): Matcher<S, Tuple4<B, C, D, E>> = Matcher(
  "${this.name}.of(${field1.name}, ${field2.name}, ${field3.name}, ${field4.name})"
) {
  val a = this.get(it)
  Tuple4(field1.get(a), field2.get(a), field3.get(a), field4.get(a))
}

public fun <S, A, B, C, D, E, F> Matcher<S, A>.of(
  field1: Matcher<A, B>,
  field2: Matcher<A, C>,
  field3: Matcher<A, D>,
  field4: Matcher<A, E>,
  field5: Matcher<A, F>
): Matcher<S, Tuple5<B, C, D, E, F>> = Matcher(
  "${this.name}.of(${field1.name}, ${field2.name}, ${field3.name}, ${field4.name}, ${field5.name})"
) {
  val a = this.get(it)
  Tuple5(field1.get(a), field2.get(a), field3.get(a), field4.get(a), field5.get(a))
}
