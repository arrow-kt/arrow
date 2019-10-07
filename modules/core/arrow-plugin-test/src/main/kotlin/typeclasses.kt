package arrow.extreme

import arrow.Kind
import arrow.given

//metadebug

interface Mappable<F> {
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
}

object Test {

  fun <F> Kind<F, Int>.addOne(M: Mappable<F> = given) : Kind<F, Int> =
    map { it + 1 }

}

fun foo() {
  Test.run {
    val result: Option<Int> = Some(1).addOne()
    println(result)
  }
}
