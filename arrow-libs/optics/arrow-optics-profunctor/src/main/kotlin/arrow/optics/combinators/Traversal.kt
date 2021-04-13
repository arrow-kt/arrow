package arrow.optics.combinators

import arrow.core.Ior
import arrow.core.unalign
import arrow.optics.IxLens
import arrow.optics.Lens
import arrow.optics.Optic
import arrow.optics.PIxLens
import arrow.optics.PLens
import arrow.optics.TraversalK
import arrow.optics.collectOf
import arrow.optics.ixCollectOf
import arrow.optics.ixLens
import arrow.optics.lens
import arrow.optics.modify

fun <K : TraversalK, I, S, A> Optic<K, I, S, S, A, A>.partsOf(): Lens<S, List<A>> =
  Optic.lens({ s ->
    s.collectOf(this)
  }, { s: S, xs: List<A> ->
    val buf = xs.toMutableList()
    val ys = s.modify(this) {
      if (buf.isEmpty()) it
      else buf.removeFirst()
    }
    ys
  })

fun <K : TraversalK, I, S, T, A, B> Optic<K, I, S, T, A, B>.unsafePartsOf(): PLens<S, T, List<A>, List<B>> =
  Optic.lens({ s ->
    s.collectOf(this as Optic<K, I, S, S, A, A>)
  }, { s: S, xs: List<B> ->
    val buf = xs.toMutableList()
    s.modify(this) {
      if (buf.isEmpty()) throw IllegalStateException("Optic.unsafePartsOf empty list! Make sure to not modify the list size, or at least guarantee it is not smaller.")
      else buf.removeFirst()
    }
  })

fun <K : TraversalK, I, S, A> Optic<K, I, S, S, A, A>.ixPartsOf(): IxLens<List<I>, S, List<A>> =
  Optic.ixLens({ s ->
    s.ixCollectOf(this).unalign { Ior.Both(it.first, it.second) }
  }, { s: S, xs: List<A> ->
    val buf = xs.toMutableList()
    val ys = s.modify(this) {
      if (buf.isEmpty()) it
      else buf.removeFirst()
    }
    ys
  })

fun <K : TraversalK, I, S, T, A, B> Optic<K, I, S, T, A, B>.unsafeIxPartsOf(): PIxLens<List<I>, S, T, List<A>, List<B>> =
  Optic.ixLens({ s ->
    s.ixCollectOf(this as Optic<K, I, S, S, A, A>)
      .unalign { Ior.Both(it.first, it.second) }
  }, { s: S, xs: List<B> ->
    val buf = xs.toMutableList()
    s.modify(this) {
      if (buf.isEmpty()) throw IllegalStateException("Optic.unsafeIxPartsOf empty list! Make sure to not modify the list size, or at least guarantee it is not smaller.")
      else buf.removeFirst()
    }
  })

