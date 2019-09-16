package arrow.meta.quotes

import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.LazyDeclarationResolver

interface QuasiQuoteContext {
  val compilerContext: CompilerContext

  companion object {
    operator fun invoke(compilerContext: CompilerContext) =
      object : QuasiQuoteContext {
        override val compilerContext: CompilerContext = compilerContext
      }
  }
}