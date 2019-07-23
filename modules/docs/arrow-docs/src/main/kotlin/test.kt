import arrow.core.None
import arrow.core.Some
import arrow.optics.Prism

val doubleToInt: Prism<Double, Int> = Prism(
  getOption = { double: Double ->
    val i = double.toInt()
    if (i.toDouble() == double) Some(i) else None
  },
  reverseGet = Int::toDouble
)
