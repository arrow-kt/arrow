package arrow.ap.objects.autofold

inline fun <A, B, C, D> `arrow`.`optics`.`AutoFoldWithGenerics`<A, B, C>.fold(
  crossinline first: (`arrow`.`optics`.`AutoFoldWithGenerics`.`First`<A>) -> D,
  crossinline second: (`arrow`.`optics`.`AutoFoldWithGenerics`.`Second`<A, B>) -> D
): D = when (this) {
  is `arrow`.`optics`.`AutoFoldWithGenerics`.`First` -> `first`(this)
  is `arrow`.`optics`.`AutoFoldWithGenerics`.`Second` -> `second`(this)
}