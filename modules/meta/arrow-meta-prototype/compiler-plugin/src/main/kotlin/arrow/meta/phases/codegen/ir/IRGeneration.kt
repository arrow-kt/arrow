package arrow.meta.phases.codegen.ir

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.resolve.BindingContext

interface IRGeneration : ExtensionPhase {

  fun CompilerContext.generate(
    file: IrFile,
    backendContext: BackendContext,
    bindingContext: BindingContext
  )

}