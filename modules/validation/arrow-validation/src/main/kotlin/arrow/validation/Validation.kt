package arrow.validation

import arrow.core.Either
import arrow.core.identity

private fun <A, B> Either<A, B>.get(): B =
  fold({ throw NoSuchElementException("Either.get on a Left value") }, ::identity)

@Deprecated(message = "This data type has been replaced by arrow.core.Validated, arrow.core.Either and their `map` methods equivalent to the `validate` API")
class Validation<out E : Any>(vararg disjunctionSequence: Either<E, *>) {

  val failures: List<E> = disjunctionSequence.filter { it.isLeft() }.map { it.swap().get() }

  val hasFailures: Boolean = failures.isNotEmpty()
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2) { (a, b) -> ifValid(a, b) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  ifValid: (R1, R2) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3) { (a, b, c) -> ifValid(a, b, c) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  ifValid: (R1, R2, R3) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4) { (a, b, c, d) -> ifValid(a, b, c, d) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  ifValid: (R1, R2, R3, R4) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5) { (a, b, c, d, e) -> ifValid(a, b, c, d, e) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  ifValid: (R1, R2, R3, R4, R5) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6) { (a, b, c, d, e, f) -> ifValid(a, b, c, d, e, f) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  ifValid: (R1, R2, R3, R4, R5, R6) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7) { (a, b, c, d, e, f, g) -> ifValid(a, b, c, d, e, f, g) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8) { (a, b, c, d, e, f, g, h) -> ifValid(a, b, c, d, e, f, g, h) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9) { (a, b, c, d, e, f, g, h, i) -> ifValid(a, b, c, d, e, f, g, h, i) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) { (a, b, c, d, e, f, g, h, i, j) -> ifValid(a, b, c, d, e, f, g, h, i, j) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) { (a, b, c, d, e, f, g, h, i, j, k) -> ifValid(a, b, c, d, e, f, g, h, i, j, k) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) { (a, b, c, d, e, f, g, h, i, j, k, l) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) { (a, b, c, d, e, f, g, h, i, j, k, l, m) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  p17: Either<L, R17>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  p17: Either<L, R17>,
  p18: Either<L, R18>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  p17: Either<L, R17>,
  p18: Either<L, R18>,
  p19: Either<L, R19>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any, R20 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  p17: Either<L, R17>,
  p18: Either<L, R18>,
  p19: Either<L, R19>,
  p20: Either<L, R20>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get(), p20.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any, R20 : Any, R21 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  p17: Either<L, R17>,
  p18: Either<L, R18>,
  p19: Either<L, R19>,
  p20: Either<L, R20>,
  p21: Either<L, R21>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20, R21) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get(), p20.get(), p21.get()))
  }
}

@Deprecated(
  message = "This is deprecated and will be deleted in favor of the more general map(either, either, ...) { (a, b, ...) -> result }",
  replaceWith = ReplaceWith(
    expression = "map(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) -> ifValid(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) }",
    imports = arrayOf(
      "arrow.core.extensions.either.apply.map"
    )
  )
)
fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any, R20 : Any, R21 : Any, R22 : Any> validate(
  p1: Either<L, R1>,
  p2: Either<L, R2>,
  p3: Either<L, R3>,
  p4: Either<L, R4>,
  p5: Either<L, R5>,
  p6: Either<L, R6>,
  p7: Either<L, R7>,
  p8: Either<L, R8>,
  p9: Either<L, R9>,
  p10: Either<L, R10>,
  p11: Either<L, R11>,
  p12: Either<L, R12>,
  p13: Either<L, R13>,
  p14: Either<L, R14>,
  p15: Either<L, R15>,
  p16: Either<L, R16>,
  p17: Either<L, R17>,
  p18: Either<L, R18>,
  p19: Either<L, R19>,
  p20: Either<L, R20>,
  p21: Either<L, R21>,
  p22: Either<L, R22>,
  ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20, R21, R22) -> R
): Either<List<L>, R> {
  val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22)
  return if (validation.hasFailures) {
    Either.Left(validation.failures)
  } else {
    Either.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get(), p20.get(), p21.get(), p22.get()))
  }
}
