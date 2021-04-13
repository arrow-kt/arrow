package arrow.optics.predef

import arrow.optics.Optic
import arrow.optics.PTraversal
import arrow.optics.TraversalK
import arrow.optics.compose
import arrow.optics.internal.Applicative
import arrow.optics.internal.Kind
import arrow.optics.internal.WanderF
import arrow.optics.traversing

fun <A, B> Optic.Companion.traversedList(): PTraversal<List<A>, List<B>, A, B> =
  traversing(object : WanderF<List<A>, List<B>, A, B> {
    override fun <F> invoke(AF: Applicative<F>, source: List<A>, f: (A) -> Kind<F, B>): Kind<F, List<B>> {
      val mutList = mutableListOf<B>()
      val fa = source.fold(AF.pure(Unit)) { acc, a ->
        AF.ap(AF.map(acc) { { a -> mutList += a } }, f(a))
      }
      return AF.map(fa) { mutList }
    }
  })

@JvmName("list_traversed")
fun <K : TraversalK, S, T, A, B> Optic<K, S, T, List<A>, List<B>>.traversed(): Optic<TraversalK, S, T, A, B> =
  compose(Optic.traversedList())
