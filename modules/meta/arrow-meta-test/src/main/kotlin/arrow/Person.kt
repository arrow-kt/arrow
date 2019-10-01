// metadebug
package arrow

data class Person(val name: String, val age: Int)

@higherkind
sealed class A {
  object B : A()
  data class C(val a: Int) : A()
  data class D<S>(val b: S) : A()
}
