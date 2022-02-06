package arrow.optics.plugin

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

val KSClassDeclaration.companionObject: KSClassDeclaration?
  get() = declarations.filterIsInstance<KSClassDeclaration>().firstOrNull { it.isCompanionObject }

val KSClassDeclaration.isSealed
  get() = modifiers.contains(Modifier.SEALED)

val KSClassDeclaration.isData
  get() = modifiers.contains(Modifier.DATA)
