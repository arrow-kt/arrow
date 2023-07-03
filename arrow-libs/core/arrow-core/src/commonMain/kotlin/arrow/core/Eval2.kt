package arrow.core

import kotlin.DeepRecursiveFunction

public object Token

public typealias Eval2<A> = suspend DeepRecursiveScope<Token, *>.(Token) -> A

public fun <A> Eval2<A>.value(): A = DeepRecursiveFunction(this).invoke(Token)

public inline fun <A, B> Eval2<A>.map(crossinline f: (A) -> B): Eval2<B> = flatMap { Eval2Utils.now(f(it)) }

public fun <A, B> Eval2<A>.flatMap(f: (A) -> Eval2<B>): Eval2<B> = { token ->
  val x = this@flatMap(token)
  callRecursive(token)
  f(x)(token)
}

public fun <A> Eval2<A>.memoize(): Eval2<A> = this

public object Eval2Utils {
  public fun <A> now(a: A): Eval2<A> = { a }

  public fun <A> later(f: () -> A): Eval2<A> {
    val value by lazy(f)
    return { value }
  }

  public fun <A> always(f: () -> A): Eval2<A> = { f() }

  public fun <A> defer(f: () -> Eval2<A>): Eval2<A> = { token ->
    callRecursive(token)
    f()(token)
  }
}

public fun <E, L, R> foldEval2(
  elements: List<E>,
  zero: R,
  f: (R, E) -> Eval2<Either<L, R>>
): Eval2<Either<L, R>> =
  when (val head = elements.firstOrNull()) {
    null -> Eval2Utils.now(zero.right())
    else -> f(zero, head).flatMap { result ->
      when (result) {
        is Either.Left -> Eval2Utils.now(result)
        is Either.Right -> foldEval2(elements.drop(1), result.value, f)
      }
    }
  }

public fun <A, B> List<A>.foldRightEval2(b: Eval2<B>, f: (A, Eval2<B>) -> B): Eval2<B> =
  when (val head = firstOrNull()) {
    null -> b
    else -> Eval2Utils.later { f(head, drop(1).foldRightEval2(b, f)) }
  }
