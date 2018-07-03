package `arrow`.`ap`.`objects`.`lens`



inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.field: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`> inline get()= arrow.optics.Lens(
  get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`field` },
  set = { value: `kotlin`.`String` ->
    { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
      lens.copy(`field` = value)
    }
  }
)

inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.nullableNullable: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`?> inline get()= arrow.optics.Lens(
  get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`nullable` },
  set = { value: `kotlin`.`String`? ->
    { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
      lens.copy(`nullable` = value)
    }
  }
)

inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.optionOption: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get()= arrow.optics.Lens(
  get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`option` },
  set = { value: `arrow`.`core`.`Option`<`kotlin`.`String`> ->
    { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
      lens.copy(`option` = value)
    }
  }
)
