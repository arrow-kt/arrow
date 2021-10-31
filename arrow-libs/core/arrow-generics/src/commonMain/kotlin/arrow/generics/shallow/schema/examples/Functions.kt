package arrow.generics.shallow.schema.examples

import arrow.generics.shallow.*
import arrow.generics.shallow.And
import arrow.generics.shallow.Done
import arrow.generics.shallow.Or
import arrow.generics.shallow.ProductSchema
import arrow.generics.shallow.Schema
import arrow.generics.shallow.introduce

// generic function

public fun <T : Any, Repr : SumRepr> Schema<Repr>.gshow(value: T): String =
  when (val chosen = this.introduce(value)) {
    is Or<*, *, *> -> "${chosen.klass.simpleName} { ${chosen.fields.gshow(value)} }"
    else -> throw IllegalArgumentException("the schema does not correspond to ${value::class.simpleName}")
  }

internal fun <T : Any, Repr : ProductRepr> ProductSchema<Repr>.gshow(value: T): String = when (this) {
  is And<*, *, *> -> "${field.name} = ${field.unsafeInvoke(value)}" +
    if (rest is Done) "" else ", ${rest.gshow(value)}"
  Done -> ""
}
