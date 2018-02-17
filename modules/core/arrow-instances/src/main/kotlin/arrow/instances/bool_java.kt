package java_lang

import arrow.instances.BooleanEqInstance
import arrow.instances.BooleanHashInstance

object BooleanEqInstanceImplicits {
    @JvmStatic fun instance(): BooleanEqInstance = BooleanEqInstance
}

object BooleanHashInstanceImplicits {
    @JvmStatic fun instance(): BooleanHashInstance = BooleanHashInstance
}
