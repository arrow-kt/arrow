package arrow.plugin

import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.resolve.BindingContext

class MetaIrGenerationExtension : IrGenerationExtension {
  override fun generate(
    file: IrFile,
    backendContext: BackendContext,
    bindingContext: BindingContext
  ) {
    println("IrGenerationExtension.generate")
  }
}
