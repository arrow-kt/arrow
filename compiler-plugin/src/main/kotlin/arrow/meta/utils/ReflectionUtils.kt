package arrow.meta.utils

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * The nastier bits
 */
@Throws(Exception::class)
fun setFinalStatic(field: Field, newValue: Any) {
  field.isAccessible = true

  val modifiersField = Field::class.java.getDeclaredField("modifiers")
  modifiersField.isAccessible = true
  modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

  field.set(null, newValue)
}


