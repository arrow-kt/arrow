package arrow.optics.dsl

import arrow.core.Option
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Optional] with a focus of [Option]<[S]>
 *
 * @receiver [Optional], [Lens], or [Prism] with a focus in [Option]<[S]>
 * @return [Optional] with a focus in [S]
 */
public inline val <T, reified S> Optional<T, Option<S>>.some: Optional<T, S> inline get() = this.compose(Prism.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Traversal] with a focus of [Option]<[S]>
 *
 * @receiver [Traversal] with a focus in [Option]<[S]>
 * @return [Traversal] with a focus in [S]
 */
public inline val <T, reified S> Traversal<T, Option<S>>.some: Traversal<T, S> inline get() = this.compose(Prism.some())
