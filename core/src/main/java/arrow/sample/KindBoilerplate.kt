//package arrow.sample
//
//import arrow.extension
//
////import arrow.sample.ForOption
//
//interface Kind<out F, out A>
//
//sealed class Option<out A> {
//  fun h(): Option<Int> {
//    val x : Kind<ForOption, Int> = None
//    val y = x
//    return y
//  }
//
//  fun <B> map(f: (A) -> B): Option<B> =
//    when (this) {
//      is None -> None
//      is Some -> Some(f(a))
//    }
//}
//
//object None : Option<Nothing>()
//
//data class Some<out A>(val a: A) : Option<A>()
//
//interface Functor<F> {
//  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
//}
//
//@extension
//interface OptionFunctor: Functor<ForOption> {
//  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> = TODO()
//}