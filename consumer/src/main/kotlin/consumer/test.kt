package consumer

object test {
  @JvmStatic
  fun main(args : Array<String>) {
    val opOfInt: OptionOf<Int> = Option.None
    /**
     * `x` is implicitly casted since the KindAwareTypeChecker
     * establishes an iso between OptionOf<Int> <-> Option<Int>
     */
    val optIn: Option<Int> = opOfInt
    println("Option kind!: $optIn")

    val eitherOfStringOrInt: EitherOf<String, Int> = Either.Right(1)
    /**
     * `x` is implicitly casted since the KindAwareTypeChecker
     * establishes an iso between OptionOf<Int> <-> Option<Int>
     */
    val eitherStringOrInt: Either<String, Int> = eitherOfStringOrInt
    println("Either Kind!: $eitherStringOrInt")

    println(testConversion())
    println(testConversionFlatMap())


  }
}