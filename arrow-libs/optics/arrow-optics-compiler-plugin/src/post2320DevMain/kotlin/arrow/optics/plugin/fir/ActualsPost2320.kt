package arrow.optics.plugin.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.plugin.SimpleFunctionBuildingContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.Name

actual fun FirExtension.createMemberFunctionWithDefaultBody(
  owner: FirClassSymbol<*>,
  key: GeneratedDeclarationKey,
  name: Name,
  returnType: ConeKotlinType,
  config: SimpleFunctionBuildingContext.() -> Unit
): FirNamedFunction = createMemberFunction(owner, key, name, returnType) {
  config()
  withGeneratedDefaultBody()
}

