package arrow.core

import arrow.typeclasses.Semigroup

inline fun <A> identity(a: A): A = a

@PublishedApi
internal object ArrowCoreInternalException : RuntimeException(
  "Arrow-Core internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow-core/issues/new/choose",
  null
) {
  override fun fillInStackTrace(): Throwable = this
}

const val TailRecMDeprecation: String =
  "tailRecM is deprecated together with the Kind type classes since it's meant for writing kind-based polymorphic stack-safe programs."

const val FoldRightDeprecation: String =
  "foldRight, and all lazy folds, are being deprecated as a normal fold can sufficiently cover all its use cases as long as it's inline. See https://github.com/arrow-kt/arrow/pull/2370 for more information."

/**
 * This is a work-around for having nested nulls in generic code.
 * This allows for writing faster generic code instead of using `Option`.
 * This is only used as an optimisation technique in low-level code,
 * always prefer to use `Option` in actual business code when needed in generic code.
 */
@PublishedApi
internal object EmptyValue {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <A> unbox(value: Any?): A =
    if (value === this) null as A else value as A
}

/**
 * Like [Semigroup.maybeCombine] but for using with [EmptyValue]
 */
@PublishedApi
internal fun <T> Semigroup<T>.emptyCombine(first: Any?, second: T): T =
  if (first == EmptyValue) second else (first as T).combine(second)
