package arrow.core

abstract class PartialFunction<in A, out B> : (A) -> B {
  abstract fun isDefinedAt(a: A): Boolean
}

infix fun <A, B> PartialFunction<A, B>.orElse(f: PartialFunction<A, B>): PartialFunction<A, B> =
  object : PartialFunction<A, B>() {
    override fun isDefinedAt(a: A): Boolean =
      this@orElse.isDefinedAt(a) || f.isDefinedAt(a)

    override fun invoke(x: A): B =
      if (this@orElse.isDefinedAt(x)) {
        this@orElse(x)
      } else {
        f(x)
      }

  }

fun <A, B> PartialFunction(definedAt: (A) -> Boolean, ifDefined: (A) -> B): PartialFunction<A, B> =
  object : PartialFunction<A, B>() {
    override fun invoke(p1: A): B {
      if (definedAt(p1)) {
        return ifDefined(p1)
      } else {
        throw IllegalArgumentException("Value: ($p1) isn't supported by this function")
      }
    }

    override fun isDefinedAt(a: A) = definedAt(a)
  }

/**
 * Turns this partial function into a plain function returning an Option result.
 */
fun <A, B> PartialFunction<A, B>.lift(): (A) -> Option<B> = Lifted(this)

/**
 * Applies this partial function to the given argument when it is contained in the function domain.
 * Applies fallback function where this partial function is not defined.
 */
fun <A, B : B1, A1 : A, B1> PartialFunction<A, B>.invokeOrElse(x: A1, default: (A1) -> B1): B1 =
  if (isDefinedAt(x)) invoke(x) else default(x)

fun <P1, R> PartialFunction<P1, R>.invokeOrElse(p1: P1, default: R): R = if (this.isDefinedAt(p1)) {
  this(p1)
} else {
  default
}

fun <A, B, C> PartialFunction<A, B>.andThen(f: (B) -> C): PartialFunction<A, C> =
  object : PartialFunction<A, C>() {
    override fun isDefinedAt(a: A): Boolean = this@andThen.isDefinedAt(a)
    override fun invoke(a: A): C = f(this@andThen(a))
  }

private class Lifted<A, B>(val pf: PartialFunction<A, B>) : (A) -> Option<B> {
  override fun invoke(x: A): Option<B> = pf.andThen { Some(it) }.invokeOrElse(x) { None }
}

fun <A, B> ((A) -> B).toPartialFunction(definedAt: (A) -> Boolean): PartialFunction<A, B> = PartialFunction(definedAt, this)

fun <A, B> case(ff: Tuple2<(A) -> Boolean, (A) -> B>): PartialFunction<A, B> =
  object : PartialFunction<A, B>() {
    override fun isDefinedAt(a: A): Boolean = ff.a(a)
    override fun invoke(a: A): B = ff.b(a)
  }

infix fun <A, B> ((A) -> Boolean).then(f: (A) -> B): Tuple2<(A) -> Boolean, (A) -> B> = Tuple2(this, f)