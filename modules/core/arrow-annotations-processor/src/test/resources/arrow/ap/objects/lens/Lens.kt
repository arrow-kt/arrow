package `arrow`.`ap`.`objects`.`lens`



/**
 * [arrow.optics.Lens] that can see into arrow.ap.objects.lens.Lens and focus in its property field [kotlin.String]
 */
inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.field: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`> inline get()= arrow.optics.Lens(
  get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`field` },
  set = { value: `kotlin`.`String` ->
    { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
      lens.copy(`field` = value)
    }
  }
)

/**
 * [arrow.optics.Lens] that can see into arrow.ap.objects.lens.Lens and focus in its property nullableNullable [kotlin.String?]
 */
inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.nullableNullable: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`?> inline get()= arrow.optics.Lens(
  get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`nullable` },
  set = { value: `kotlin`.`String`? ->
    { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
      lens.copy(`nullable` = value)
    }
  }
)

/**
 * [arrow.optics.Lens] that can see into arrow.ap.objects.lens.Lens and focus in its property optionOption [arrow.core.Option<kotlin.String>]
 */
inline val `arrow`.`ap`.`objects`.`lens`.`Lens`.Companion.optionOption: arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `arrow`.`core`.`Option`<`kotlin`.`String`>> inline get()= arrow.optics.Lens(
  get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`option` },
  set = { value: `arrow`.`core`.`Option`<`kotlin`.`String`> ->
    { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
      lens.copy(`option` = value)
    }
  }
)
