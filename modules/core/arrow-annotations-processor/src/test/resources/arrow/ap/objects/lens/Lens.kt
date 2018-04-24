package `arrow`.`ap`.`objects`.`lens`

fun lensField(): arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`> = arrow.optics.Lens(
        get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`field` },
        set = { value: `kotlin`.`String` ->
            { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
                lens.copy(`field` = value)
            }
        }
)
fun lensNullableNullable(): arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `kotlin`.`String`?> = arrow.optics.Lens(
        get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`nullable` },
        set = { value: `kotlin`.`String`? ->
            { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
                lens.copy(`nullable` = value)
            }
        }
)
fun lensOptionOption(): arrow.optics.Lens<`arrow`.`ap`.`objects`.`lens`.`Lens`, `arrow`.`core`.`Option`<`kotlin`.`String`>> = arrow.optics.Lens(
        get = { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` -> lens.`option` },
        set = { value: `arrow`.`core`.`Option`<`kotlin`.`String`> ->
            { lens: `arrow`.`ap`.`objects`.`lens`.`Lens` ->
                lens.copy(`option` = value)
            }
        }
)