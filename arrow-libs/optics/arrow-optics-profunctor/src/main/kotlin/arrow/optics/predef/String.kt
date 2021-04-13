package arrow.optics.predef

import arrow.optics.Fold
import arrow.optics.FoldK
import arrow.optics.Optic
import arrow.optics.Optic_
import arrow.optics.compose
import arrow.optics.get

@JvmName("string_folded")
fun Optic.Companion.folded(): Fold<String, Char> =
  Optic.get { str: String -> str.asIterable() }.compose(folded<Iterable<Char>, Char>())

@JvmName("string_folded")
fun <K : FoldK, S> Optic_<K, S, String>.folded(): Optic_<FoldK, S, Char> =
  compose(arrow.optics.Optic.folded())
