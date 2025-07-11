package arrow.optics.dsl

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.optics.Every
import arrow.optics.Traversal
import kotlin.jvm.JvmName

/**
 * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param tr [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Traversal<T, S>.every(tr: Traversal<S, A>): Traversal<T, A> = this.compose(tr)

public val <T, A> Traversal<T, List<A>>.every: Traversal<T, A>
  get() = this.compose(Every.list())

@get:JvmName("everyRight")
public val <T, Error, A> Traversal<T, Either<Error, A>>.every: Traversal<T, A>
  get() = this.compose(Every.either())

@get:JvmName("everyValue")
public val <T, K, V> Traversal<T, Map<K, V>>.every: Traversal<T, V>
  get() = this.compose(Every.map())

@get:JvmName("everyNonEmptyList")
public val <T, A> Traversal<T, NonEmptyList<A>>.every: Traversal<T, A>
  get() = this.compose(Every.nonEmptyList())

@get:JvmName("everySome")
public val <T, A> Traversal<T, Option<A>>.every: Traversal<T, A>
  get() = this.compose(Every.option())

@get:JvmName("everySequence")
public val <T, A> Traversal<T, Sequence<A>>.every: Traversal<T, A>
  get() = this.compose(Every.sequence())

@get:JvmName("everyChar")
public val <T> Traversal<T, String>.every: Traversal<T, Char>
  get() = this.compose(Every.string())
