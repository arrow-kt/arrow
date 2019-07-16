package consumer

object test {
  @JvmStatic
  fun main(args : Array<String>) {
    println("Before calling Foo")
    FooClass().foo()
    FooClass().test()
    FooClass.bar()
    println("After calling Foo")
  }
}