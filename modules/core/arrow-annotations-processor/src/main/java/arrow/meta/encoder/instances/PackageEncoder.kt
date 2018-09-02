package arrow.meta.encoder.instances

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.meta.ast.PackageName
import arrow.meta.encoder.EncodingError
import arrow.meta.encoder.MetaEncoder
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement

object PackageEncoder : MetaEncoder<PackageName> {

  override fun encode(element: Element): Either<EncodingError, PackageName> =
    when (element) {
      is PackageElement -> PackageName(element.qualifiedName.toString()).right()
      else -> EncodingError.UnsupportedElementType("Unsupported $element, as (${element.kind}) to PackageName", element).left()
    }

}

