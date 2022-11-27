@file:JvmMultifileClass
@file:JvmName("EitherKt")
package arrow.core

import arrow.typeclasses.Semigroup
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@PublishedApi
internal val unit: Either<Nothing, Unit> = Either.Right(Unit)

public inline fun <E, A, B, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  transform: (A, B) -> Z,
): Either<E, Z> = zip(combine, b, unit, unit, unit, unit, unit, unit, unit, unit) { a, bb, _, _, _, _, _, _, _, _ ->
  transform(a, bb)
}

public inline fun <E, A, B, C, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  transform: (A, B, C) -> Z,
): Either<E, Z> = zip(combine, b, c, unit, unit, unit, unit, unit, unit, unit) { a, bb, cc, _, _, _, _, _, _, _ ->
  transform(a, bb, cc)
}

public inline fun <E, A, B, C, D, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  transform: (A, B, C, D) -> Z,
): Either<E, Z> = zip(combine, b, c, d, unit, unit, unit, unit, unit, unit) { a, bb, cc, dd, _, _, _, _, _, _ ->
  transform(a, bb, cc, dd)
}

public inline fun <E, A, B, C, D, EE, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  transform: (A, B, C, D, EE) -> Z,
): Either<E, Z> = zip(combine, b, c, d, e, unit, unit, unit, unit, unit) { a, bb, cc, dd, ee, _, _, _, _, _ ->
  transform(a, bb, cc, dd, ee)
}

public inline fun <E, A, B, C, D, EE, FF, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, FF>,
  transform: (A, B, C, D, EE, FF) -> Z,
): Either<E, Z> = zip(combine, b, c, d, e, f, unit, unit, unit, unit) { a, bb, cc, dd, ee, ff, _, _, _, _ ->
  transform(a, bb, cc, dd, ee, ff)
}

public inline fun <E, A, B, C, D, EE, F, G, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  transform: (A, B, C, D, EE, F, G) -> Z,
): Either<E, Z> = zip(combine, b, c, d, e, f, g, unit, unit, unit) { a, bb, cc, dd, ee, ff, gg, _, _, _ ->
  transform(a, bb, cc, dd, ee, ff, gg)
}

public inline fun <E, A, B, C, D, EE, F, G, H, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  h: Either<E, H>,
  transform: (A, B, C, D, EE, F, G, H) -> Z,
): Either<E, Z> = zip(combine, b, c, d, e, f, g, h, unit, unit) { a, bb, cc, dd, ee, ff, gg, hh, _, _ ->
  transform(a, bb, cc, dd, ee, ff, gg, hh)
}

public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  h: Either<E, H>,
  i: Either<E, I>,
  transform: (A, B, C, D, EE, F, G, H, I) -> Z,
): Either<E, Z> = zip(combine, b, c, d, e, f, g, h, i, unit) { a, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
  transform(a, bb, cc, dd, ee, ff, gg, hh, ii)
}

@Suppress("DuplicatedCode")
public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> Either<E, A>.zip(
  combine: Semigroup<E>,
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  h: Either<E, H>,
  i: Either<E, I>,
  j: Either<E, J>,
  transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
): Either<E, Z> =
  if (this is Either.Right && b is Either.Right && c is Either.Right && d is Either.Right && e is Either.Right && f is Either.Right && g is Either.Right && h is Either.Right && i is Either.Right && j is Either.Right) {
    Either.Right(transform(this.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
  } else {
    var accumulatedError: Any? = EmptyValue
    accumulatedError = if (this@zip is Either.Left) this@zip.value else accumulatedError
    accumulatedError = if (b is Either.Left) EmptyValue.combine(accumulatedError, b.value, combine) else accumulatedError
    accumulatedError = if (c is Either.Left) EmptyValue.combine(accumulatedError, c.value, combine) else accumulatedError
    accumulatedError = if (d is Either.Left) EmptyValue.combine(accumulatedError, d.value, combine) else accumulatedError
    accumulatedError = if (e is Either.Left) EmptyValue.combine(accumulatedError, e.value, combine) else accumulatedError
    accumulatedError = if (f is Either.Left) EmptyValue.combine(accumulatedError, f.value, combine) else accumulatedError
    accumulatedError = if (g is Either.Left) EmptyValue.combine(accumulatedError, g.value, combine) else accumulatedError
    accumulatedError = if (h is Either.Left) EmptyValue.combine(accumulatedError, h.value, combine) else accumulatedError
    accumulatedError = if (i is Either.Left) EmptyValue.combine(accumulatedError, i.value, combine) else accumulatedError
    accumulatedError = if (j is Either.Left) EmptyValue.combine(accumulatedError, j.value, combine) else accumulatedError
    
    @Suppress("UNCHECKED_CAST")
    (Either.Left(accumulatedError as E))
  }

public inline fun <E, A, B, Z> Either<E, A>.zip(
  b: Either<E, B>,
  transform: (A, B) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, unit, unit, unit, unit, unit, unit, unit, unit) { a, bb, _, _, _, _, _, _, _, _ ->
  transform(a, bb)
}

public inline fun <E, A, B, C, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  transform: (A, B, C) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, unit, unit, unit, unit, unit, unit, unit) { a, bb, cc, _, _, _, _, _, _, _ ->
  transform(a, bb, cc)
}

public inline fun <E, A, B, C, D, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  transform: (A, B, C, D) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, unit, unit, unit, unit, unit, unit) { a, bb, cc, dd, _, _, _, _, _, _ ->
  transform(a, bb, cc, dd)
}

public inline fun <E, A, B, C, D, EE, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  transform: (A, B, C, D, EE) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, unit, unit, unit, unit, unit) { a, bb, cc, dd, ee, _, _, _, _, _ ->
  transform(a, bb, cc, dd, ee)
}

public inline fun <E, A, B, C, D, EE, FF, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, FF>,
  transform: (A, B, C, D, EE, FF) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, unit, unit, unit, unit) { a, bb, cc, dd, ee, ff, _, _, _, _ ->
  transform(a, bb, cc, dd, ee, ff)
}

public inline fun <E, A, B, C, D, EE, F, G, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  transform: (A, B, C, D, EE, F, G) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, g, unit, unit, unit) { a, bb, cc, dd, ee, ff, gg, _, _, _ ->
  transform(a, bb, cc, dd, ee, ff, gg)
}

public inline fun <E, A, B, C, D, EE, F, G, H, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  h: Either<E, H>,
  transform: (A, B, C, D, EE, F, G, H) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, g, h, unit, unit) { a, bb, cc, dd, ee, ff, gg, hh, _, _ ->
  transform(a, bb, cc, dd, ee, ff, gg, hh)
}

public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  h: Either<E, H>,
  i: Either<E, I>,
  transform: (A, B, C, D, EE, F, G, H, I) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, g, h, i, unit) { a, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
  transform(a, bb, cc, dd, ee, ff, gg, hh, ii)
}

@Suppress("DuplicatedCode")
public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> Either<E, A>.zip(
  b: Either<E, B>,
  c: Either<E, C>,
  d: Either<E, D>,
  e: Either<E, EE>,
  f: Either<E, F>,
  g: Either<E, G>,
  h: Either<E, H>,
  i: Either<E, I>,
  j: Either<E, J>,
  transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
): Either<NonEmptyList<E>, Z> =
  if (this is Either.Right && b is Either.Right && c is Either.Right && d is Either.Right && e is Either.Right && f is Either.Right && g is Either.Right && h is Either.Right && i is Either.Right && j is Either.Right) {
    Either.Right(transform(this.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
  } else {
    val list = buildList(9) {
      if (this@zip is Either.Left) add(this@zip.value)
      if (b is Either.Left) add(b.value)
      if (c is Either.Left) add(c.value)
      if (d is Either.Left) add(d.value)
      if (e is Either.Left) add(e.value)
      if (f is Either.Left) add(f.value)
      if (g is Either.Left) add(g.value)
      if (h is Either.Left) add(h.value)
      if (i is Either.Left) add(i.value)
      if (j is Either.Left) add(j.value)
    }
    Either.Left(NonEmptyList(list[0], list.drop(1)))
  }

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  transform: (A, B) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, unit, unit, unit, unit, unit, unit, unit, unit) { a, bb, _, _, _, _, _, _, _, _ ->
  transform(a, bb)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  transform: (A, B, C) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, unit, unit, unit, unit, unit, unit, unit) { a, bb, cc, _, _, _, _, _, _, _ ->
  transform(a, bb, cc)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  transform: (A, B, C, D) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, unit, unit, unit, unit, unit, unit) { a, bb, cc, dd, _, _, _, _, _, _ ->
  transform(a, bb, cc, dd)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, EE, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  e: Either<NonEmptyList<E>, EE>,
  transform: (A, B, C, D, EE) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, unit, unit, unit, unit, unit) { a, bb, cc, dd, ee, _, _, _, _, _ ->
  transform(a, bb, cc, dd, ee)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, EE, FF, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  e: Either<NonEmptyList<E>, EE>,
  f: Either<NonEmptyList<E>, FF>,
  transform: (A, B, C, D, EE, FF) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, unit, unit, unit, unit) { a, bb, cc, dd, ee, ff, _, _, _, _ ->
  transform(a, bb, cc, dd, ee, ff)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, EE, F, G, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  e: Either<NonEmptyList<E>, EE>,
  f: Either<NonEmptyList<E>, F>,
  g: Either<NonEmptyList<E>, G>,
  transform: (A, B, C, D, EE, F, G) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, g, unit, unit, unit) { a, bb, cc, dd, ee, ff, gg, _, _, _ ->
  transform(a, bb, cc, dd, ee, ff, gg)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, EE, F, G, H, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  e: Either<NonEmptyList<E>, EE>,
  f: Either<NonEmptyList<E>, F>,
  g: Either<NonEmptyList<E>, G>,
  h: Either<NonEmptyList<E>, H>,
  transform: (A, B, C, D, EE, F, G, H) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, g, h, unit, unit) { a, bb, cc, dd, ee, ff, gg, hh, _, _ ->
  transform(a, bb, cc, dd, ee, ff, gg, hh)
}

@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  e: Either<NonEmptyList<E>, EE>,
  f: Either<NonEmptyList<E>, F>,
  g: Either<NonEmptyList<E>, G>,
  h: Either<NonEmptyList<E>, H>,
  i: Either<NonEmptyList<E>, I>,
  transform: (A, B, C, D, EE, F, G, H, I) -> Z,
): Either<NonEmptyList<E>, Z> = zip(b, c, d, e, f, g, h, i, unit) { a, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
  transform(a, bb, cc, dd, ee, ff, gg, hh, ii)
}

@Suppress("DuplicatedCode")
@JvmName("zipNonEmptyList")
public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> Either<NonEmptyList<E>, A>.zip(
  b: Either<NonEmptyList<E>, B>,
  c: Either<NonEmptyList<E>, C>,
  d: Either<NonEmptyList<E>, D>,
  e: Either<NonEmptyList<E>, EE>,
  f: Either<NonEmptyList<E>, F>,
  g: Either<NonEmptyList<E>, G>,
  h: Either<NonEmptyList<E>, H>,
  i: Either<NonEmptyList<E>, I>,
  j: Either<NonEmptyList<E>, J>,
  transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
): Either<NonEmptyList<E>, Z> =
  if (this is Either.Right && b is Either.Right && c is Either.Right && d is Either.Right && e is Either.Right && f is Either.Right && g is Either.Right && h is Either.Right && i is Either.Right && j is Either.Right) {
    Either.Right(transform(this.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
  } else {
    val list = buildList {
      if (this@zip is Either.Left) addAll(this@zip.value)
      if (b is Either.Left) addAll(b.value)
      if (c is Either.Left) addAll(c.value)
      if (d is Either.Left) addAll(d.value)
      if (e is Either.Left) addAll(e.value)
      if (f is Either.Left) addAll(f.value)
      if (g is Either.Left) addAll(g.value)
      if (h is Either.Left) addAll(h.value)
      if (i is Either.Left) addAll(i.value)
      if (j is Either.Left) addAll(j.value)
    }
    Either.Left(NonEmptyList(list[0], list.drop(1)))
  }
