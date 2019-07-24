package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative

/**
 * A wrapper data type also considered by the @extension mechanisms to forward type class
 * instance methods into both the wrapper and the wrapped data type. Ex. List<A>#foldMap(M: Monoid<A>)
 *
 * A data type is considered a wrapper if
 * - It contains a single constructor with one parameter whose type is a type constructor with one type parameter
 * - It's a sub type of the type it wraps
 *
 * The type class @extension mechanism will project then all syntax generated for the Wrapper also into the Wrapped
 * type constructor as extension functions.
 */
@higherkind
data class ListK<out A>(private val list: List<A>) : ListKOf<A>, List<A> by list {

  fun <B> flatMap(f: (A) -> ListKOf<B>): ListK<B> = list.flatMap { f(it).fix().list }.k()

  fun <B> map(f: (A) -> B): ListK<B> = list.map(f).k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold(b, f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: ListK<A>): Eval<B> = when {
      fa_p.list.isEmpty() -> lb
      else -> f(fa_p.fix().list.first(), Eval.defer { loop(fa_p.list.drop(1).k()) })
    }
    return Eval.defer { loop(this) }
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is ListK<*> -> this.list == other.list
      is List<*> -> this.list == other
      else -> false
    }

  fun <B> ap(ff: ListKOf<(A) -> B>): ListK<B> = ff.fix().flatMap { f -> map(f) }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> =
    foldRight(Eval.always { GA.just(emptyList<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { (listOf(it.a) + it.b).k() } }
    }.value()

  fun <B, Z> map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    flatMap { a ->
      fb.fix().map { b ->
        f(Tuple2(a, b))
      }
    }

  fun <B> filterMap(f: (A) -> Option<B>): ListK<B> =
    flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

  override fun hashCode(): Int = list.hashCode()

  companion object {

    fun <A> just(a: A): ListK<A> = listOf(a).k()

    fun <A> empty(): ListK<A> = emptyList<A>().k()

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
      buf: ArrayList<B>,
      f: (A) -> Kind<ForListK, Either<A, B>>,
      v: ListK<Either<A, B>>
    ) {
      if (!v.isEmpty()) {
        val head: Either<A, B> = v.first()
        when (head) {
          is Either.Right -> {
            buf += head.b
            go(buf, f, v.drop(1).k())
          }
          is Either.Left -> go(buf, f, (f(head.a).fix() + v.drop(1)).k())
        }
      }
    }

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForListK, Either<A, B>>): ListK<B> {
      val buf = ArrayList<B>()
      go(buf, f, f(a).fix())
      return ListK(buf)
    }
  }
}

fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
  (fix() + y.fix()).k()

fun <A> List<A>.k(): ListK<A> = ListK(this)
