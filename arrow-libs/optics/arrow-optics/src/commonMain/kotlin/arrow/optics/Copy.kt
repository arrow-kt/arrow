package arrow.optics

public interface Copy<A> {
  /**
   * Changes the value of the element(s) pointed by the [Setter].
   */
  public infix fun <B> Setter<A, B>.set(b: B)

  /**
   * Transforms the value of the element(s) pointed by the [Traversal].
   */
  public infix fun <B> Traversal<A, B>.transform(f: (B) -> B)

  /**
   * Declares a block in which all optics are nested within
   * the given [field]. Instead of:
   *
   * ```
   * x.copy {
   *   X.a.this set "A"
   *   X.a.that set "B"
   * }
   * ```
   *
   * you can write:
   *
   * ```
   * x.copy {
   *   inside(X.a) {
   *     A.this set "A"
   *     A.that set "B"
   *   }
   * }
   * ```
   */
  public fun <B> inside(field: Traversal<A, B>, f: Copy<B>.() -> Unit): Unit =
    field.transform { it.copy(f) }
}

// mutable builder of copies
private class CopyImpl<A>(var current: A): Copy<A> {
  override fun <B> Setter<A, B>.set(b: B) {
    current = this.set(current, b)
  }
  override fun <B> Traversal<A, B>.transform(f: (B) -> B) {
    current = this.modify(current, f)
  }
}

/**
 * Small DSL which parallel Kotlin's built-in `copy`,
 * but using optics instead of field names. See [Copy]
 * for the operations allowed inside the block.
 *
 * This allows declaring changes on nested elements,
 * preventing the "nested `copy` problem". Instead of:
 *
 * ```
 * person.copy(address = person.address.copy(city = "Madrid"))
 * ```
 *
 * you can write:
 *
 * ```
 * person.copy {
 *   Person.address.city set "Madrid"
 * }
 * ```
 */
public fun <A> A.copy(f: Copy<A>.() -> Unit): A =
  CopyImpl(this).also(f).current
