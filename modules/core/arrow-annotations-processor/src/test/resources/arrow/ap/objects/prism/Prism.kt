package `arrow`.`ap`.`objects`.`prism`

import arrow.core.left
import arrow.core.right

inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed1: arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> get()= arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1` -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = { it }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed1: arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed1


inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed2: arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> get()= arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2` -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = { it }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`prism`.`Prism`>.prismSealed2: arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> inline get() = this + `arrow`.`ap`.`objects`.`prism`.`Prism`.prismSealed2
