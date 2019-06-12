package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin

@AutoService(ComponentRegistrar::class)
class HigherKindPlugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      newMethod { origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, value: Any? ->
        println("New Method from meta: $name")
      },
      newField {  origin, access, name, desc, signature, value ->
        println("New Field from meta: $name")
      },
      newAnnotation { desc, visible ->
        println("New Annotation from meta: $desc")
      }
    )
}