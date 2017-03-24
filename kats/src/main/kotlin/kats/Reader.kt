package kats

/**
 * Basic implementation of the Reader monad. Provides an "implicit" context (configuration) for
 * function execution. Intended to provide Dependency Injection.
 */
class Reader<C, A>(val rd: (C) -> A) {

  inline fun <B> map(crossinline f: (A) -> B): Reader<C, B> = Reader { c -> f(rd(c)) }

  inline fun <B> flatMap(crossinline f: (A) -> Reader<C, B>): Reader<C, B> = Reader { c ->
    f(rd(c)).rd(c)
  }

  fun run(c: C) = rd(c)
}
