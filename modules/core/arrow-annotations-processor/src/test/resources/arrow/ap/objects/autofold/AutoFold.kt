package `arrow`.`ap`.`objects`.`autofold`

inline fun <A> `arrow`.`ap`.`objects`.`autofold`.`AutoFold`.fold(
  crossinline first: (`arrow`.`ap`.`objects`.`autofold`.`AutoFold`.`First`) -> A,
  crossinline second: (`arrow`.`ap`.`objects`.`autofold`.`AutoFold`.`Second`) -> A
): A = when (this) {
  is `arrow`.`ap`.`objects`.`autofold`.`AutoFold`.`First` -> `first`(this)
  is `arrow`.`ap`.`objects`.`autofold`.`AutoFold`.`Second` -> `second`(this)
}