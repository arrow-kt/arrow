package java_lang

import kategory.*

object StringMonoidInstanceImplicits {
    @JvmStatic fun instance(): StringMonoidInstance = StringMonoidInstance
}

object StringSemigroupInstanceImplicits {
    @JvmStatic fun instance(): StringSemigroupInstance = StringSemigroupInstance
}

object StringEqInstanceImplicits {
    @JvmStatic
    fun instance(): StringEqInstance = StringEqInstance
}