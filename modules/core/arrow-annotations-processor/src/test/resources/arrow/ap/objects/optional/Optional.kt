package `arrow`.`ap`.`objects`.`optional`

import arrow.core.left
import arrow.core.right
import arrow.core.toOption

inline val `arrow`.`ap`.`objects`.`optional`.`Optional`.Companion.nullable: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> get()= arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`nullable`?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` ->
      optional.copy(`nullable` = value)
    }
  }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Setter<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Traversal<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.nullable: arrow.optics.Fold<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.nullable

inline val `arrow`.`ap`.`objects`.`optional`.`Optional`.Companion.option: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> get()= arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`option`.orNull()?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` ->
      optional.copy(`option` = value.toOption())
    }
  }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Setter<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Traversal<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`optional`.`Optional`>.option: arrow.optics.Fold<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`optional`.`Optional`.option
