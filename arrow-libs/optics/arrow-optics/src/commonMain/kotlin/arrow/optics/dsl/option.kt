package arrow.optics.dsl

import arrow.core.Option
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Lens] with a focus of [Option]<[S]>
 *
 * @receiver [Lens] with a focus in [Option]<[S]>
 * @return [Optional] with a focus in [S]
 */
public inline val <T, S> Lens<T, Option<S>>.some: Optional<T, S> inline get() = this.compose(Prism.some())


/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Prism] with a focus of [Option]<[S]>
 *
 * @receiver [Prism] with a focus in [Option]<[S]>
 * @return [Prism] with a focus in [S]
 */
public inline val <T, S> Prism<T, Option<S>>.some: Prism<T, S> inline get() = this.compose(Prism.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Optional] with a focus of [Option]<[S]>
 *
 * @receiver [Optional] with a focus in [Option]<[S]>
 * @return [Optional] with a focus in [S]
 */
public inline val <T, S> Optional<T, Option<S>>.some: Optional<T, S> inline get() = this.compose(Prism.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Traversal] with a focus of [Option]<[S]>
 *
 * @receiver [Traversal] with a focus in [Option]<[S]>
 * @return [Traversal] with a focus in [S]
 */
public inline val <T, S> Traversal<T, Option<S>>.some: Traversal<T, S> inline get() = this.compose(Prism.some())
