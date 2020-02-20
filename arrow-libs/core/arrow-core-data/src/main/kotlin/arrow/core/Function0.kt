package arrow.core

import arrow.Kind
import arrow.core.Function0.Companion.just
import arrow.higherkind

fun <A> (() -> A).k(): Function0<A> = Function0(this)

operator fun <A> Function0Of<A>.invoke(): A = this.fix().f()

@higherkind
data class Function0<out A>(internal val f: () -> A) : Function0Of<A> {

  fun <B> map(f: (A) -> B): Function0<B> = just(f(this()))

  fun <B> flatMap(ff: (A) -> Function0Of<B>): Function0<B> = ff(f()).fix()

  fun <B> coflatMap(f: (Function0Of<A>) -> B): Function0<B> = { f(this) }.k()

  fun <B> ap(ff: Function0Of<(A) -> B>): Function0<B> = ff.fix().flatMap { f -> map(f) }.fix()

  fun extract(): A = f()

  companion object {

    fun <A> just(a: A): Function0<A> = { a }.k()

    tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForFunction0, Either<A, B>>): B {
      val fa = f(a).fix()()
      return when (fa) {
        is Either.Right -> fa.b
        is Either.Left -> loop(fa.a, f)
      }
    }

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFunction0, Either<A, B>>): Function0<B> = { loop(a, f) }.k()
  }
}

fun <A, B> Function0<Either<A, B>>.select(f: Function0Of<(A) -> B>): Function0<B> =
  flatMap { it.fold({ l -> just(l).ap(f) }, { r -> just(r) }) }
