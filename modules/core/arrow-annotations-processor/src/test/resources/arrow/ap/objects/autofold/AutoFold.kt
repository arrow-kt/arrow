package arrow.ap.objects.autofold

inline fun <A> `arrow`.`optics`.`AutoFold`.fold(
  crossinline first: (`arrow`.`optics`.`AutoFold`.`First`) -> A,
  crossinline second: (`arrow`.`optics`.`AutoFold`.`Second`) -> A
): A = when (this) {
  is `arrow`.`optics`.`AutoFold`.`First` -> `first`(this)
  is `arrow`.`optics`.`AutoFold`.`Second` -> `second`(this)
}