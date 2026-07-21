package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticsNames
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate

object OpticsPredicates {
  val optics = DeclarationPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }
  val opticsLookup = LookupPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }
}

fun keyOf(origin: FirDeclarationOrigin): GeneratedDeclarationKey? = (origin as? FirDeclarationOrigin.Plugin)?.key
