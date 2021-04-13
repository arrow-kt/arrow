package arrow.optics.combinators

import arrow.core.Ior
import arrow.core.unalign
import arrow.optics.FoldK
import arrow.optics.IxFoldF
import arrow.optics.IxLens
import arrow.optics.IxTraversal
import arrow.optics.Lens
import arrow.optics.Optic
import arrow.optics.PIxLens
import arrow.optics.PIxTraversal
import arrow.optics.PLens
import arrow.optics.TraversalK
import arrow.optics.collectOf
import arrow.optics.internal.Applicative
import arrow.optics.internal.IxWanderF
import arrow.optics.internal.Kind
import arrow.optics.internal.backwards
import arrow.optics.ixCollectOf
import arrow.optics.ixFolding
import arrow.optics.ixLens
import arrow.optics.ixTraverseOf
import arrow.optics.ixTraverseOf_
import arrow.optics.ixTraversing
import arrow.optics.lens
import arrow.optics.modify
import arrow.optics.traverseOf
import arrow.optics.traversing

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


fun <K : TraversalK, I, S, T, A> Optic<K, I, S, T, A, A>.take(n: Int): PIxTraversal<I, S, T, A, A> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, A> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, A>): Kind<F, T> {
      var counter = 0
      return source.ixTraverseOf(this@take, AF) { i, a ->
        if (counter++ < n) f(i, a)
        else AF.pure(a)
      }
    }
  })

fun <K : TraversalK, I, S, T, A> Optic<K, I, S, T, A, A>.drop(n: Int): PIxTraversal<I, S, T, A, A> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, A> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, A>): Kind<F, T> {
      var counter = 0
      return source.ixTraverseOf(this@drop, AF) { i, a ->
        if (counter++ < n) AF.pure(a)
        else f(i, a)
      }
    }
  })

fun <K: TraversalK, I, S, T, A> Optic<K, I, S, T, A, A>.takeWhile(filter: (A) -> Boolean): PIxTraversal<I, S, T, A, A> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, A> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, A>): Kind<F, T> {
      var tripped = false
      return source.ixTraverseOf(this@takeWhile, AF) { i, a ->
        if (!tripped && filter(a)) f(i, a)
        else AF.pure(a).also { tripped = true }
      }
    }
  })

fun <K: TraversalK, I, S, T, A> Optic<K, I, S, T, A, A>.dropWhile(filter: (A) -> Boolean): PIxTraversal<I, S, T, A, A> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, A> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, A>): Kind<F, T> {
      var tripped = false
      return source.ixTraverseOf(this@dropWhile, AF) { i, a ->
        if (!tripped && filter(a)) AF.pure(a).also { tripped = true }
        else f(i, a)
      }
    }
  })

fun <K: TraversalK, I, S, T, A> Optic<K, I, S, T, A, A>.ixTakeWhile(filter: (I, A) -> Boolean): PIxTraversal<I, S, T, A, A> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, A> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, A>): Kind<F, T> {
      var tripped = false
      return source.ixTraverseOf(this@ixTakeWhile, AF) { i, a ->
        if (!tripped && filter(i, a)) f(i, a)
        else AF.pure(a).also { tripped = true }
      }
    }
  })

fun <K: TraversalK, I, S, T, A> Optic<K, I, S, T, A, A>.ixDropWhile(filter: (I, A) -> Boolean): PIxTraversal<I, S, T, A, A> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, A> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, A>): Kind<F, T> {
      var tripped = false
      return source.ixTraverseOf(this@ixDropWhile, AF) { i, a ->
        if (!tripped && filter(i, a)) AF.pure(a).also { tripped = true }
        else f(i, a)
      }
    }
  })

fun <K : TraversalK, I, S, T, A, B> Optic<K, I, S, T, A, B>.backwards(): PIxTraversal<I, S, T, A, B> =
  Optic.ixTraversing(object : IxWanderF<I, S, T, A, B> {
    override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, B>): Kind<F, T> =
      source.ixTraverseOf(this@backwards, AF.backwards(), f)
  })
