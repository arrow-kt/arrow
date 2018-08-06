package arrow.generic

import arrow.coproduct
import arrow.core.Option
import arrow.core.toOption
import arrow.generic.coproduct2.Coproduct2
import arrow.generic.coproduct2.cop
import java.math.BigDecimal

@coproduct
object Coproduct //TODO hopefully we don't need this just to start generation.

////TODO I think we may be able to generate type safe methods for this, as in the type A needs to be included in the Generic Holder somewhere.
////We just need to add methods for each placeholder? Coproduct<Generic2<*, A>> / Coproduct<Generic2<A, *>>> ?
//inline fun <reified A> Coproduct2<A, *>.select(): Option<A> = (value as? A).toOption()
//inline fun <reified B> Coproduct2<*, B>.select(dummy0: Unit = Unit): Option<B> = (value as? B).toOption()
//
//fun test() {
//    val stringCoproduct = "String".cop<String, Long>()
//
//    stringCoproduct.select<String>() //Option<String>
//
//    stringCoproduct.select<Long>() //Option<Long>
//
//    stringCoproduct.select<BigDecimal>() //Invalid, Success!
//}