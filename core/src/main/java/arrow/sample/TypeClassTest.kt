package arrow.sample

import arrow.extension
import arrow.with

interface Semigroup<A> {
  fun A.combine(other: A): A
}

class MyNumber(val value: Int)

@extension
interface MyNumberSemigroup: Semigroup<MyNumber> {
  override fun MyNumber.combine(other: MyNumber): MyNumber =
    MyNumber(value + other.value)
}

object MyNumberSemigroupInstance : MyNumberSemigroup

object Contained {

  fun <A> add(@with S: Semigroup<A>, a: A, b: A): A =
    a.combine(b)

}

//object Invocation {
//  @JvmStatic
//  fun main(args: Array<String>) {
//    println(add(MyNumberSemigroupInstance, MyNumber(1), MyNumber(2)).value)
//  }
//}