package java_lang

import arrow.instances.StringEqInstance
import arrow.instances.StringMonoidInstance
import arrow.instances.StringSemigroupInstance

object StringMonoidInstanceImplicits {
    fun instance(): StringMonoidInstance = StringMonoidInstance
}

object StringSemigroupInstanceImplicits {
    fun instance(): StringSemigroupInstance = StringSemigroupInstance
}

object StringEqInstanceImplicits {

    fun instance(): StringEqInstance = StringEqInstance
}