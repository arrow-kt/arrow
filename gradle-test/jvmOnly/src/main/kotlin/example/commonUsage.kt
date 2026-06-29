package example

import arrow.optics.set
import arrow.optics.transform

val nameLens = Person.address.street

fun test(person: Person): Person = person.copy {
  name set "John"
  age transform { it + 1 }
}
