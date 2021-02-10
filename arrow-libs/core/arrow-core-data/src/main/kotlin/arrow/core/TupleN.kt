@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import java.util.Collections
import kotlin.collections.LinkedHashMap

class ForTuple11 private constructor() {
  companion object
}
typealias Tuple11Of<A, B, C, D, E, F, G, H, I, J, K> = arrow.Kind11<ForTuple11, A, B, C, D, E, F, G, H, I, J, K>
typealias Tuple11PartialOf<A, B, C, D, E, F, G, H, I, J> = arrow.Kind10<ForTuple11, A, B, C, D, E, F, G, H, I, J>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K> Tuple11Of<A, B, C, D, E, F, G, H, I, J, K>.fix(): Tuple11<A, B, C, D, E, F, G, H, I, J, K> =
  this as Tuple11<A, B, C, D, E, F, G, H, I, J, K>

data class Tuple11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K
) : Tuple11Of<A, B, C, D, E, F, G, H, I, J, K> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k

  companion object
}

class ForTuple12 private constructor() {
  companion object
}
typealias Tuple12Of<A, B, C, D, E, F, G, H, I, J, K, L> = arrow.Kind12<ForTuple12, A, B, C, D, E, F, G, H, I, J, K, L>
typealias Tuple12PartialOf<A, B, C, D, E, F, G, H, I, J, K> = arrow.Kind11<ForTuple12, A, B, C, D, E, F, G, H, I, J, K>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Tuple12Of<A, B, C, D, E, F, G, H, I, J, K, L>.fix(): Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> =
  this as Tuple12<A, B, C, D, E, F, G, H, I, J, K, L>

data class Tuple12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L
) : Tuple12Of<A, B, C, D, E, F, G, H, I, J, K, L> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l

  companion object
}

class ForTuple13 private constructor() {
  companion object
}
typealias Tuple13Of<A, B, C, D, E, F, G, H, I, J, K, L, M> = arrow.Kind13<ForTuple13, A, B, C, D, E, F, G, H, I, J, K, L, M>
typealias Tuple13PartialOf<A, B, C, D, E, F, G, H, I, J, K, L> = arrow.Kind12<ForTuple13, A, B, C, D, E, F, G, H, I, J, K, L>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Tuple13Of<A, B, C, D, E, F, G, H, I, J, K, L, M>.fix(): Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> =
  this as Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>

data class Tuple13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M
) : Tuple13Of<A, B, C, D, E, F, G, H, I, J, K, L, M> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m

  companion object
}

class ForTuple14 private constructor() {
  companion object
}
typealias Tuple14Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = arrow.Kind14<ForTuple14, A, B, C, D, E, F, G, H, I, J, K, L, M, N>
typealias Tuple14PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M> = arrow.Kind13<ForTuple14, A, B, C, D, E, F, G, H, I, J, K, L, M>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Tuple14Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.fix(): Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> =
  this as Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>

data class Tuple14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N
) : Tuple14Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n

  companion object
}

class ForTuple15 private constructor() {
  companion object
}
typealias Tuple15Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = arrow.Kind15<ForTuple15, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
typealias Tuple15PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = arrow.Kind14<ForTuple15, A, B, C, D, E, F, G, H, I, J, K, L, M, N>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Tuple15Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.fix(): Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> =
  this as Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>

data class Tuple15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O
) : Tuple15Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o

  companion object
}

class ForTuple16 private constructor() {
  companion object
}
typealias Tuple16Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = arrow.Kind16<ForTuple16, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
typealias Tuple16PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = arrow.Kind15<ForTuple16, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Tuple16Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.fix(): Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> =
  this as Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>

data class Tuple16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P
) : Tuple16Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p

  companion object
}

class ForTuple17 private constructor() {
  companion object
}
typealias Tuple17Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = arrow.Kind17<ForTuple17, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>
typealias Tuple17PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = arrow.Kind16<ForTuple17, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Tuple17Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.fix(): Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> =
  this as Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>

data class Tuple17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P,
  @Deprecated("Use seventeenth instead", ReplaceWith("seventeenth"))
  val q: Q
) : Tuple17Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p
  val seventeenth: Q = q

  companion object
}

class ForTuple18 private constructor() {
  companion object
}
typealias Tuple18Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = arrow.Kind18<ForTuple18, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>
typealias Tuple18PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = arrow.Kind17<ForTuple18, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Tuple18Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.fix(): Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> =
  this as Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>

data class Tuple18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P,
  @Deprecated("Use seventeenth instead", ReplaceWith("seventeenth"))
  val q: Q,
  @Deprecated("Use eighteenth instead", ReplaceWith("eighteenth"))
  val r: R
) : Tuple18Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p
  val seventeenth: Q = q
  val eighteenth: R = r

  companion object
}

class ForTuple19 private constructor() {
  companion object
}
typealias Tuple19Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = arrow.Kind19<ForTuple19, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>
typealias Tuple19PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = arrow.Kind18<ForTuple19, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Tuple19Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.fix(): Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> =
  this as Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>

data class Tuple19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P,
  @Deprecated("Use seventeenth instead", ReplaceWith("seventeenth"))
  val q: Q,
  @Deprecated("Use eighteenth instead", ReplaceWith("eighteenth"))
  val r: R,
  @Deprecated("Use nineteenth instead", ReplaceWith("nineteenth"))
  val s: S
) : Tuple19Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p
  val seventeenth: Q = q
  val eighteenth: R = r
  val nineteenth: S = s

  companion object
}

class ForTuple20 private constructor() {
  companion object
}
typealias Tuple20Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = arrow.Kind20<ForTuple20, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>
typealias Tuple20PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = arrow.Kind19<ForTuple20, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Tuple20Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.fix(): Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =
  this as Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>

data class Tuple20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P,
  @Deprecated("Use seventeenth instead", ReplaceWith("seventeenth"))
  val q: Q,
  @Deprecated("Use eighteenth instead", ReplaceWith("eighteenth"))
  val r: R,
  @Deprecated("Use nineteenth instead", ReplaceWith("nineteenth"))
  val s: S,
  @Deprecated("Use twentieth instead", ReplaceWith("twentieth"))
  val t: T
) : Tuple20Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p
  val seventeenth: Q = q
  val eighteenth: R = r
  val nineteenth: S = s
  val twentieth: T = t

  companion object
}

class ForTuple21 private constructor() {
  companion object
}
typealias Tuple21Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = arrow.Kind21<ForTuple21, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>
typealias Tuple21PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = arrow.Kind20<ForTuple21, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Tuple21Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.fix(): Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =
  this as Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>

data class Tuple21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P,
  @Deprecated("Use seventeenth instead", ReplaceWith("seventeenth"))
  val q: Q,
  @Deprecated("Use eighteenth instead", ReplaceWith("eighteenth"))
  val r: R,
  @Deprecated("Use nineteenth instead", ReplaceWith("nineteenth"))
  val s: S,
  @Deprecated("Use twentieth instead", ReplaceWith("twentieth"))
  val t: T,
  @Deprecated("Use twentyFirst instead", ReplaceWith("twentyFirst"))
  val u: U
) : Tuple21Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p
  val seventeenth: Q = q
  val eighteenth: R = r
  val nineteenth: S = s
  val twentyFirst: U = u

  companion object
}

class ForTuple22 private constructor() {
  companion object
}
typealias Tuple22Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = arrow.Kind22<ForTuple22, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
typealias Tuple22PartialOf<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = arrow.Kind21<ForTuple22, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Tuple22Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.fix(): Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =
  this as Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>

data class Tuple22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G,
  @Deprecated("Use eighth instead", ReplaceWith("eighth"))
  val h: H,
  @Deprecated("Use ninth instead", ReplaceWith("ninth"))
  val i: I,
  @Deprecated("Use tenth instead", ReplaceWith("tenth"))
  val j: J,
  @Deprecated("Use eleventh instead", ReplaceWith("eleventh"))
  val k: K,
  @Deprecated("Use twelfth instead", ReplaceWith("twelfth"))
  val l: L,
  @Deprecated("Use thirteenth instead", ReplaceWith("thirteenth"))
  val m: M,
  @Deprecated("Use fourteenth instead", ReplaceWith("fourteenth"))
  val n: N,
  @Deprecated("Use fifteenth instead", ReplaceWith("fifteenth"))
  val o: O,
  @Deprecated("Use sixteenth instead", ReplaceWith("sixteenth"))
  val p: P,
  @Deprecated("Use seventeenth instead", ReplaceWith("seventeenth"))
  val q: Q,
  @Deprecated("Use eighteenth instead", ReplaceWith("eighteenth"))
  val r: R,
  @Deprecated("Use nineteenth instead", ReplaceWith("nineteenth"))
  val s: S,
  @Deprecated("Use twentieth instead", ReplaceWith("twentieth"))
  val t: T,
  @Deprecated("Use twentyFirst instead", ReplaceWith("twentyFirst"))
  val u: U,
  @Deprecated("Use twentySecond instead", ReplaceWith("twentySecond"))
  val v: V
) : Tuple22Of<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> {
  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g
  val eight: H = h
  val ninth: I = i
  val tenth: J = j
  val eleventh: K = k
  val twelfth: L = l
  val thirteenth: M = m
  val fourteenth: N = n
  val fifteenth: O = o
  val sixteenth: P = p
  val seventeenth: Q = q
  val eighteenth: R = r
  val nineteenth: S = s
  val twentyFirst: U = u
  val twentySecond: V = v

  companion object
}

private const val INT_MAX_POWER_OF_TWO: Int = Int.MAX_VALUE / 2 + 1

infix fun <A, B> A.toT(b: B): Tuple2<A, B> = Tuple2(this, b)

fun <A, B> Tuple2<A, B>.toPair(): Pair<A, B> = Pair(this.a, this.b)

fun <A, B> Pair<A, B>.toTuple2(): Tuple2<A, B> = Tuple2(this.first, this.second)

fun <A, B, C> Tuple3<A, B, C>.toTriple(): Triple<A, B, C> = Triple(this.a, this.b, this.c)

fun <A, B, C> Triple<A, B, C>.toTuple3(): Tuple3<A, B, C> = Tuple3(this.first, this.second, this.third)

fun <K, V> Iterable<Tuple2<K, V>>.toMap(): Map<K, V> {
  if (this is Collection) {
    return when (size) {
      0 -> emptyMap()
      1 -> mapOf(if (this is List) this[0] else iterator().next())
      else -> toMap(LinkedHashMap(mapCapacity(size)))
    }
  }
  return toMap(LinkedHashMap()).optimizeReadOnlyMap()
}

fun <K, V> Array<out Tuple2<K, V>>.toMap(): Map<K, V> = when (size) {
  0 -> emptyMap()
  1 -> mapOf(this[0])
  else -> toMap(LinkedHashMap(mapCapacity(size)))
}

fun <K, V> Sequence<Tuple2<K, V>>.toMap(): Map<K, V> = toMap(LinkedHashMap()).optimizeReadOnlyMap()

fun <K, V> mapOf(pair: Tuple2<K, V>): Map<K, V> = Collections.singletonMap(pair.a, pair.b)

internal fun <K, V, M : MutableMap<in K, in V>> Iterable<Tuple2<K, V>>.toMap(destination: M): M =
  destination.apply { putAll(this@toMap) }

internal fun <K, V, M : MutableMap<in K, in V>> Array<out Tuple2<K, V>>.toMap(destination: M): M =
  destination.apply { putAll(this@toMap) }

internal fun <K, V, M : MutableMap<in K, in V>> Sequence<Tuple2<K, V>>.toMap(destination: M): M =
  destination.apply { putAll(this@toMap) }

internal fun <K, V> MutableMap<in K, in V>.putAll(tuples: Iterable<Tuple2<K, V>>) {
  for ((key, value) in tuples) {
    put(key, value)
  }
}

internal fun <K, V> MutableMap<in K, in V>.putAll(tuples: Array<out Tuple2<K, V>>) {
  for ((key, value) in tuples) {
    put(key, value)
  }
}

internal fun <K, V> MutableMap<in K, in V>.putAll(tuples: Sequence<Tuple2<K, V>>) {
  for ((key, value) in tuples) {
    put(key, value)
  }
}

operator fun <K, V> Map<out K, V>.plus(tuple: Tuple2<K, V>): Map<K, V> =
  if (this.isEmpty()) mapOf(tuple) else LinkedHashMap(this).apply { put(tuple.a, tuple.b) }

operator fun <K, V> Map<out K, V>.plus(tuples: Iterable<Tuple2<K, V>>): Map<K, V> =
  if (this.isEmpty()) tuples.toMap() else LinkedHashMap(this).apply { putAll(tuples) }

operator fun <K, V> Map<out K, V>.plus(tuples: Array<out Tuple2<K, V>>): Map<K, V> =
  if (this.isEmpty()) tuples.toMap() else LinkedHashMap(this).apply { putAll(tuples) }

operator fun <K, V> Map<out K, V>.plus(tuples: Sequence<Tuple2<K, V>>): Map<K, V> =
  LinkedHashMap(this).apply { putAll(tuples) }.optimizeReadOnlyMap()

fun <K, V> Map.Entry<K, V>.toTuple2(): Tuple2<K, V> = Tuple2(key, value)

internal fun mapCapacity(expectedSize: Int): Int =
  when {
    expectedSize < 3 -> expectedSize + 1
    expectedSize < INT_MAX_POWER_OF_TWO -> expectedSize + expectedSize / 3
    else -> Int.MAX_VALUE
  }

// do not expose for now @PublishedApi
internal fun <K, V> Map<K, V>.optimizeReadOnlyMap() =
  when (size) {
    0 -> emptyMap()
    1 -> this.toSingletonMap()
    else -> this
  }

// creates a singleton copy of map
internal fun <K, V> Map<out K, V>.toSingletonMap(): Map<K, V> =
  with(entries.iterator().next()) {
    Collections.singletonMap(key, value)
  }
