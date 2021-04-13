package arrow.optics

import arrow.optics.internal.Function1
import arrow.optics.internal.fix

typealias Setter<S, A> = Optic_<SetterK, S, A>
typealias PSetter<S, T, A, B> = Optic<SetterK, S, T, A, B>

// TODO constructors for PSetter and Setter

fun <K : SetterK, S, T, A, B> S.modify(optic: Optic<K, S, T, A, B>, f: (A) -> B): T =
  Function1.traversing().run { optic.run { transform(Function1(f)) } }.fix().f(this)

fun <K : SetterK, S, T, A, B> S.set(optic: Optic<K, S, T, A, B>, v: B): T =
  modify(optic) { v }
