package arrow.sample

import arrow.extension
import arrow.sample.Contained.add
import arrow.with

interface Semigroup<A> {
  fun A.combine(other: A): A
}

class MyNumber(val value: Int) {
  companion object {
    @extension
    interface MyNumberSemigroup : Semigroup<MyNumber> {
      override fun MyNumber.combine(other: MyNumber): MyNumber =
        MyNumber(value + other.value)
    }
  }
}


val extension: Nothing
  get() = throw RuntimeException("Call not processed by arrow meta typeclass support")

object Contained {

  fun <A> add(a: A, b: A, @with S: Semigroup<A>): A =
    a.combine(b)

}

object Invocation {
  @JvmStatic
  fun main(args: Array<String>) {
    add(MyNumber(1), MyNumber(2))
  }
}