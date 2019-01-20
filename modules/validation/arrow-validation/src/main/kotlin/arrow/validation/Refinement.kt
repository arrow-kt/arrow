package arrow.validation

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.maybe
import arrow.data.ListK
import arrow.data.Nel
import arrow.data.fix
import arrow.data.k
import arrow.data.extensions.listk.traverse.traverse
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Traverse

data class RefinedPredicateException(val msg: String) : IllegalArgumentException(msg)

interface Refinement<F, A> {
  fun applicativeError(): ApplicativeError<F, Nel<RefinedPredicateException>>

  fun A.refinement(): Boolean

  fun invalidValueMsg(a: A): String

  fun refine(value: A): Kind<F, A> = applicativeError().run {
    value.refinement()
      .maybe { just(value) }
      .getOrElse { raiseError(Nel.of(RefinedPredicateException(invalidValueMsg(value)))) }
  }

  fun <B> refine(value: A, f: (A) -> B): Kind<F, B> =
    applicativeError().run { refine(value).map(f) }

  fun <B> refine2(a: A, b: A, f: (Tuple2<A, A>) -> B): Kind<F, B> =
    applicativeError().run { map(refine(a), refine(b), f) }

  fun <B> refine3(a: A, b: A, c: A, f: (Tuple3<A, A, A>) -> B): Kind<F, B> =
    applicativeError().run { map(refine(a), refine(b), refine(c), f) }

  fun <B> refine4(a: A, b: A, c: A, d: A, f: (Tuple4<A, A, A, A>) -> B): Kind<F, B> =
    applicativeError().run { map(refine(a), refine(b), refine(c), refine(d), f) }

  fun <G, B> refineTraverse(value: Kind<G, A>, traverse: Traverse<G>, f: (A) -> B): Kind<F, Kind<G, B>> =
    traverse.run { value.traverse(applicativeError()) { refine(it, f) } }

  fun <B> refineTraverseList(elements: List<A>, f: (A) -> B): Kind<F, List<B>> =
    applicativeError().run { refineTraverse(elements.k(), ListK.traverse(), f).map { it.fix() } }

  fun <G> refineSequence(value: Kind<G, A>, traverse: Traverse<G>): Kind<F, Kind<G, A>> =
    refineTraverse(value, traverse, ::identity)

  fun refineSequenceList(elements: List<A>): Kind<F, List<A>> =
    applicativeError().run { refineSequence(elements.k(), ListK.traverse()).map { it.fix() } }
}