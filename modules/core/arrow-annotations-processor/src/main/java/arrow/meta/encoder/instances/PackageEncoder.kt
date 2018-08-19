package arrow.meta.encoder.instances

import arrow.meta.ast.PackageName
import arrow.meta.encoder.EncodingError
import arrow.meta.encoder.EncodingResult
import arrow.meta.encoder.MetaEncoder
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement

object PackageEncoder : MetaEncoder<PackageName> {

  override fun encode(element: Element): EncodingResult<PackageName> =
    when (element) {
      is PackageElement -> EncodingResult.just(PackageName(element.qualifiedName.toString()))
      else -> EncodingResult.raiseError(
        EncodingError.UnsupportedElementType("Unsupported $element, as (${element.kind}) to PackageName", element)
      )
    }

}

