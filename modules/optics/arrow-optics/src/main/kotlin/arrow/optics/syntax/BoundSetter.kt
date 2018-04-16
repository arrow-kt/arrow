package arrow.optics.syntax

import arrow.optics.*
import arrow.optics.typeclasses.*

/**
 * A [BoundSetter] is an optic that allows to see into a bound structure and set or modify its focus.
 * It is used to enable a DSL to work with values of immutable structures where the full power of Optics is not required.
 *
 * @param S the source of a [BoundSetter]
 * @param A the focus of a [BoundSetter]
 */
class BoundSetter<S, A>(val value: S, val setter: Setter<S, A>) {

  /**
   * Modify the focus of a [BoundSetter] with a function [f].
   */
  fun modify(f: (A) -> A): S =
    setter.modify(value, f)

  /**
   * Set the focus of a [BoundSetter] with a value [a].
   */
  fun set(a: A): S =
    setter.set(value, a)

  /**
   * Set the focus of a [BoundSetter] in every element [T] of [A].
   */
  fun <T> every(EA: Each<A, T>): BoundSetter<S, T> =
    compose(EA.each())

  /**
   * Set the focus of a [BoundSetter] in [T] at index [I] of [A].
   */
  fun <T, I> at(AT: At<A, I, T>, i: I): BoundSetter<S, T> =
    compose(AT.at(i))

  /**
   * Compose a [BoundSetter] with a [Setter].
   */
  fun <T> compose(other: Setter<A, T>): BoundSetter<S, T> =
    BoundSetter(value, setter + other)

  /**
   * Compose a [BoundSetter] with a [Optional]
   */
  fun <T> compose(other: Optional<A, T>): BoundSetter<S, T> =
    BoundSetter(value, setter + other)

  /**
   * Compose a [BoundSetter] with a [Prism]
   */
  fun <T> compose(other: Prism<A, T>): BoundSetter<S, T> =
    BoundSetter(value, setter + other)

  /**
   * Compose a [BoundSetter] with a [Lens]
   */
  fun <T> compose(other: Lens<A, T>): BoundSetter<S, T> =
    BoundSetter(value, setter + other)

  /**
   * Compose a [BoundSetter] with a [Iso]
   */
  fun <T> compose(other: Iso<A, T>): BoundSetter<S, T> =
    BoundSetter(value, setter + other)

  /**
   * Compose a [BoundSetter] with a [Traversal]
   */
  fun <T> compose(other: Traversal<A, T>): BoundSetter<S, T> =
    BoundSetter(value, setter + other)

}