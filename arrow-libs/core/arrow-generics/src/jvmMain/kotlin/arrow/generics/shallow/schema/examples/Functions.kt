package arrow.generics.shallow.schema.examples

import arrow.generics.shallow.* // ktlint-disable no-wildcard-imports
import arrow.generics.shallow.schema.* // ktlint-disable no-wildcard-imports

// generic function

public fun <T : Any, Repr : SumRepr> Schema<Repr>.gshow(value: T): String =
  when (val chosen = this.introduce(value)) {
    is Or<*, *, *> -> "${chosen.klass.simpleName} { ${chosen.fields.gshow(value)} }"
    else -> throw IllegalArgumentException("the schema does not correspond to ${value.javaClass.name}")
  }

internal fun <T : Any, Repr : ProductRepr> ProductSchema<Repr>.gshow(value: T): String = when (this) {
  is And<*, *> -> "${field.name} = ${get(value)}" +
    if (rest is Done) "" else ", ${rest.gshow(value)}"
  Done -> ""
}
