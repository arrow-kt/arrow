package arrow.sample

interface Kind<out F, out A>

//class ForOption

//typealias OptionOf<A> = Kind<ForOption, A>

sealed class Option<out A>
object None : Option<Nothing>()
data class Some<out A>(val a: A): Option<A>()

object test {
  @JvmStatic
  fun main(args: Array<String>) {
    println(Option::class.java.interfaces.toList().map { it.name })
   println(Class.forName("arrow.sample.ForOption"))
  }
}