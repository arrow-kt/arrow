package `arrow`.`ap`.`objects`.`autofold`

inline fun <A, B, C, D> `arrow`.`ap`.`objects`.`autofold`.`AutoFoldWithGenerics`<A, B, C>.fold(
  crossinline first: (`arrow`.`ap`.`objects`.`autofold`.`AutoFoldWithGenerics`.`First`<A>) -> D,
  crossinline second: (`arrow`.`ap`.`objects`.`autofold`.`AutoFoldWithGenerics`.`Second`<A, B>) -> D
): D = when (this) {
  is `arrow`.`ap`.`objects`.`autofold`.`AutoFoldWithGenerics`.`First` -> `first`(this)
  is `arrow`.`ap`.`objects`.`autofold`.`AutoFoldWithGenerics`.`Second` -> `second`(this)
}