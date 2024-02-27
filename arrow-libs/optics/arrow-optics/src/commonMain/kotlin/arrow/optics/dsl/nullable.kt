package arrow.optics.dsl

import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Prism
import arrow.optics.Traversal

/**
 * DSL to compose an [Optional] with focus on a nullable type with [notNull].
 *
 * @receiver [Optional], [Lens], or [Prism] with a focus in <[S]?>
 * @return [Optional] with a focus in [S]
 */
public inline val <T, S> Optional<T, S?>.notNull: Optional<T, S> inline get() = this.compose(POptional.notNull())

/**
 * DSL to compose a [Traversal] with focus on a nullable type with [notNull].
 *
 * @receiver [Traversal] with a focus in <[S]?>
 * @return [Traversal] with a focus in [S]
 */
public inline val <T, S> Traversal<T, S?>.notNull: Traversal<T, S> inline get() = this.compose(POptional.notNull())
