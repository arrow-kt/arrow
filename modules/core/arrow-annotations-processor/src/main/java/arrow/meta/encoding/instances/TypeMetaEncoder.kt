package arrow.meta.encoding.instances

import arrow.meta.ast.PackageName
import arrow.meta.encoding.EncodingError
import arrow.meta.encoding.EncodingResult
import arrow.meta.encoding.MetaEncoder
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement

class PackageMetaEncoder : MetaEncoder<PackageName> {

  override fun Element.encode(): EncodingResult<PackageName> =
    when (this) {
      is PackageElement -> EncodingResult.just(PackageName(qualifiedName.toString()))
      else -> EncodingResult.raiseError(
        EncodingError.UnsupportedElementType("Unsupported $this, as (${kind}) to PackageName", this)
      )
    }

}