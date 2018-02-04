package `arrow`.`ap`.`objects`
import arrow.syntax.either.*
import arrow.syntax.option.toOption

fun optionalNullableOptional(): arrow.optics.Optional<`arrow`.`ap`.`objects`.`Optional`, `kotlin`.`String`> = arrow.optics.Optional(
        getOrModify = { optional: `arrow`.`ap`.`objects`.`Optional` -> optional.`nullable`?.right() ?: optional.left() },
        set = { value: `kotlin`.`String` ->
            { optional: `arrow`.`ap`.`objects`.`Optional` ->
                optional.copy(`nullable` = value)
            }
        }
)
fun optionalOptionOptional(): arrow.optics.Optional<`arrow`.`ap`.`objects`.`Optional`, `kotlin`.`String`> = arrow.optics.Optional(
        getOrModify = { optional: `arrow`.`ap`.`objects`.`Optional` -> optional.`option`.orNull()?.right() ?: optional.left() },
        set = { value: `kotlin`.`String` ->
            { optional: `arrow`.`ap`.`objects`.`Optional` ->
                optional.copy(`option` = value.toOption())
            }
        }
)