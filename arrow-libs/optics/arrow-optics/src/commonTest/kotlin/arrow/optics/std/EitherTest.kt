// package arrow.optics.std
//
// import arrow.core.Invalid
// import arrow.core.Valid
// import arrow.core.Validated
// import arrow.core.test.UnitSpec
// import arrow.core.test.generators.either
// import arrow.core.test.generators.functionAToB
// import arrow.core.test.generators.validated
// import arrow.optics.Iso
// import arrow.optics.test.laws.IsoLaws
// import arrow.typeclasses.Monoid
// import io.kotest.property.Arb
// import io.kotest.property.arbitrary.int
// import io.kotest.property.arbitrary.string
//
// class EitherTest : UnitSpec() {
//
//  init {
//    val VAL_MONOID: Monoid<Validated<String, Int>> = object : Monoid<Validated<String, Int>> {
//      override fun empty() = Valid(0)
//
//      override fun Validated<String, Int>.combine(b: Validated<String, Int>): Validated<String, Int> =
//        when (this) {
//          is Invalid -> {
//            when (b) {
//              is Invalid -> Invalid((value + b.value))
//              is Valid -> b
//            }
//          }
//          is Valid -> {
//            when (b) {
//              is Invalid -> b
//              is Valid -> arrow.core.Valid((value + b.value))
//            }
//          }
//        }
//    }
//    testLaws(
//      IsoLaws.laws(
//        iso = Iso.eitherToValidated(),
//        aGen = Arb.either(Arb.string(), Arb.int()),
//        bGen = Arb.validated(Arb.string(), Arb.int()),
//        funcGen = Arb.functionAToB(Arb.validated(Arb.string(), Arb.int())),
//      )
//    )
//  }
// }
