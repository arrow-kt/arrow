package arrow.generic

interface Generic<T, Repr> {

  /** Convert an instance of the concrete type to the generic value representation */
  fun to(t: T): Repr

  /** Convert an instance of the generic representation to an instance of the concrete type */
  fun from(r: Repr): T
}
