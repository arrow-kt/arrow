package arrow.optics.dsl

import arrow.core.Option
import arrow.optics.Fold
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Setter
import arrow.optics.Traversal
import arrow.optics.some

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Lens] with a focus of [Option]<[S]>
 *
 * @receiver [Lens] with a focus in [Option]<[S]>
 * @return [Optional] with a focus in [S]
 */
inline val <T, S> Lens<T, Option<S>>.some: Optional<T, S> inline get() = this.compose(Option.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Iso] with a focus of [Option]<[S]>
 *
 * @receiver [Iso] with a focus in [Option]<[S]>
 * @return [Prism] with a focus in [S]
 */
inline val <T, S> Iso<T, Option<S>>.some: Prism<T, S> inline get() = this.compose(Option.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Prism] with a focus of [Option]<[S]>
 *
 * @receiver [Prism] with a focus in [Option]<[S]>
 * @return [Prism] with a focus in [S]
 */
inline val <T, S> Prism<T, Option<S>>.some: Prism<T, S> inline get() = this.compose(Option.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Optional] with a focus of [Option]<[S]>
 *
 * @receiver [Optional] with a focus in [Option]<[S]>
 * @return [Optional] with a focus in [S]
 */
inline val <T, S> Optional<T, Option<S>>.some: Optional<T, S> inline get() = this.compose(Option.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Setter] with a focus of [Option]<[S]>
 *
 * @receiver [Setter] with a focus in [Option]<[S]>
 * @return [Setter] with a focus in [S]
 */
inline val <T, S> Setter<T, Option<S>>.some: Setter<T, S> inline get() = this.compose(Option.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Traversal] with a focus of [Option]<[S]>
 *
 * @receiver [Traversal] with a focus in [Option]<[S]>
 * @return [Traversal] with a focus in [S]
 */
inline val <T, S> Traversal<T, Option<S>>.some: Traversal<T, S> inline get() = this.compose(Option.some())

/**
 * DSL to compose a [Prism] with focus [arrow.core.Some] with a [Fold] with a focus of [Option]<[S]>
 *
 * @receiver [Fold] with a focus in [Option]<[S]>
 * @return [Fold] with a focus in [S]
 */
inline val <T, S> Fold<T, Option<S>>.some: Fold<T, S> inline get() = this.compose(Option.some())
