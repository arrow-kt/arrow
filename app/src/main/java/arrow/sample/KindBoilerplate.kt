package arrow.sample

//import arrow.sample.ForOption

interface Kind<out F, out A>

sealed class Option<out A> {
  fun h(): Option<Int> {
    val x : Kind<ForOption, Int> = None
    val y = x
    return y
  }

  fun <B> map(f: (A) -> B): Option<B> =
    when (this) {
      is None -> None
      is Some -> Some(f(a))
    }
}

object None : Option<Nothing>()

data class Some<out A>(val a: A) : Option<A>()