package arrow.optics.plugin

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

val KSClassDeclaration.companionObject: KSClassDeclaration?
  get() = declarations.filterIsInstance<KSClassDeclaration>().firstOrNull { it.isCompanionObject }

val KSClassDeclaration.isSealed
  get() = modifiers.contains(Modifier.SEALED)

val KSClassDeclaration.isDataClass
  get() = classKind == ClassKind.CLASS && modifiers.contains(Modifier.DATA)

val KSClassDeclaration.isValue
  get() = modifiers.contains(Modifier.VALUE)
