package arrow.utils

import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.tools.FileObject
import javax.tools.StandardLocation

fun Filer.createKotlinFile(`package`: String, fileName: String, element: Element): FileObject = createResource(StandardLocation.SOURCE_OUTPUT, `package`, "$fileName.kt", element)
