package arrow.meta.utils

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DeclarationReturnTypeSanitizer
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.WrappedTypeFactory

class MetaDeclarationReturnTypeSanitizer : DeclarationReturnTypeSanitizer by DeclarationReturnTypeSanitizer.Default {
  override fun sanitizeReturnType(inferred: UnwrappedType, wrappedTypeFactory: WrappedTypeFactory, trace: BindingTrace, languageVersionSettings: LanguageVersionSettings): UnwrappedType {
    println("MetaDeclarationReturnTypeSanitizer.sanitizeReturnType: $inferred")
    return DeclarationReturnTypeSanitizer.Default.sanitizeReturnType(inferred, wrappedTypeFactory, trace, languageVersionSettings)
  }
}