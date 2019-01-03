package arrow.meta.encoder.jvm

import javax.lang.model.element.Element

sealed class EncodingError {
  data class UnsupportedElementType(val msg: String, val element: Element) : EncodingError()
}
