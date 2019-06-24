package arrow.sample

import arrow.extension

interface Semigroup<A> {
  fun A.combine(other: A): A
}

data class MyNumber(val n: Int) {
  companion object {
    fun semigroup(): Semigroup<MyNumber> =
      TODO()
  }
}

@extension
interface MyNumberSemigroup: Semigroup<MyNumber> {
  override fun MyNumber.combine(other: MyNumber): MyNumber =
    MyNumber(n + other.n)
}