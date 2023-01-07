package arrow.optics.dsl

import arrow.core.Either
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PPrism
import arrow.optics.Prism
import arrow.optics.Traversal

/**
 * DSL to compose a [Prism] with focus [Either] with a [Prism] with a focus of [Either.Left]<[L]>
 *
 * @receiver [Prism] with a focus in [Either]
 * @return [Prism] with a focus in [L]
 */
public inline val <A, L, R> Prism<A, Either<L, R>>.left: Prism<A, L> inline get() = this.compose(PPrism.left())

/**
 * DSL to compose a [Optional] with focus [Either] with a [Prism] with a focus of [Either.Left]<[L]>
 *
 * @receiver [Lens] or [Optional] with a focus in [Either]
 * @return [Optional] with a focus in [L]
 */
public inline val <A, L, R> Optional<A, Either<L, R>>.left: Optional<A, L> inline get() = this.compose(PPrism.left())

/**
 * DSL to compose a [Traversal] with focus [Either] with a [Prism] with a focus of [Either.Left]<[L]>
 *
 * @receiver [Traversal] with a focus in [Either]
 * @return [Traversal] with a focus in [L]
 */
public inline val <A, L, R> Traversal<A, Either<L, R>>.left: Traversal<A, L> inline get() = this.compose(PPrism.left())

/**
 * DSL to compose a [Prism] with focus [Either] with a [Prism] with a focus of [Either.Right]<[R]>
 *
 * @receiver [Prism] with a focus in [Either]
 * @return [Prism] with a focus in [R]
 */
public inline val <A, L, R> Prism<A, Either<L, R>>.right: Prism<A, R> inline get() = this.compose(PPrism.right())

/**
 * DSL to compose a [Optional] with focus [Either] with a [Prism] with a focus of [Either.Right]<[R]>
 *
 * @receiver [Lens] or [Optional] with a focus in [Either]
 * @return [Optional] with a focus in [R]
 */
public inline val <A, L, R> Optional<A, Either<L, R>>.right: Optional<A, R> inline get() = this.compose(PPrism.right())

/**
 * DSL to compose a [Traversal] with focus [Either] with a [Prism] with a focus of [Either.Right]<[R]>
 *
 * @receiver [Traversal] with a focus in [Either]
 * @return [Traversal] with a focus in [R]
 */
public inline val <A, L, R> Traversal<A, Either<L, R>>.right: Traversal<A, R> inline get() = this.compose(PPrism.right())
