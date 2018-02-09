package java_lang

import arrow.optics.instances.StringFilterIndexInstance
import arrow.optics.instances.StringIndexInstance

object StringFilterIndexInstanceImplicits {
    @JvmStatic
    fun instance(): StringFilterIndexInstance = StringFilterIndexInstance
}

object StringIndexInstanceImplicits {
    @JvmStatic
    fun instance(): StringIndexInstance = StringIndexInstance
}

