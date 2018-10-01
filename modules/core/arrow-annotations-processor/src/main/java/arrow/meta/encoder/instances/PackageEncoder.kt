package arrow.meta.encoder.instances

import shadow.core.*
import arrow.meta.ast.PackageName
import arrow.meta.encoder.EncodingError
import arrow.meta.encoder.MetaEncoder
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement

object PackageEncoder : MetaEncoder<PackageName> {

  override fun encode(element: Element): Either<EncodingError, PackageName> =
    when (element) {
      is PackageElement -> Either.Right(PackageName(element.qualifiedName.toString()))
        else
      -> Either.Left(EncodingError.UnsupportedElementType("Unsupported $element, as (${element.kind}) to PackageName", element))
    }

}

