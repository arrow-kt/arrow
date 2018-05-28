package `arrow`.`ap`.`objects`.`lens`

inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.field: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`> get()= arrow.optics.Lens(
        get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`field` },
        set = { value: `kotlin`.`String` ->
            { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
                lens.copy(`field` = value)
            }
        }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Lens<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Lens<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Optional<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Getter<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Getter<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Setter<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Traversal<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.field: arrow.optics.Fold<S, `kotlin`.`String`> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.field

inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.nullableNullable: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`?> get()= arrow.optics.Lens(
        get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`nullable` },
        set = { value: `kotlin`.`String`? ->
            { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
                lens.copy(`nullable` = value)
            }
        }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Lens<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Lens<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Optional<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Optional<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Getter<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Getter<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Setter<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Traversal<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.nullableNullable: arrow.optics.Fold<S, `kotlin`.`String`?> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.nullableNullable

inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.optionOption: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `arrow`.`core`.`Option`<`kotlin`.`String`>> get()= arrow.optics.Lens(
        get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`option` },
        set = { value: `arrow`.`core`.`Option`<`kotlin`.`String`> ->
            { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
                lens.copy(`option` = value)
            }
        }
)

inline val <S> arrow.optics.Iso<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Lens<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Lens<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Lens<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Optional<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Optional<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Prism<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Optional<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Getter<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Getter<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Setter<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Setter<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Traversal<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Traversal<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
inline val <S> arrow.optics.Fold<S, `arrow`.`ap`.`objects`.`lens`.`Lens`>.optionOption: arrow.optics.Fold<S, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get() = this + `arrow`.`ap`.`objects`.`lens`.`Lens`.optionOption
