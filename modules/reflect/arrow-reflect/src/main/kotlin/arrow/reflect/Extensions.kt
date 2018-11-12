package arrow.reflect

import io.github.classgraph.*

object Extensions {
  @JvmStatic
  fun main(args: Array<String>) {
    val pkg = "arrow"
    val routeAnnotation = "$pkg.extension"
    ClassGraph()
      .verbose()                   // Log to stderr
      .enableAllInfo()             // Scan classes, methods, fields, annotations
      .whitelistPackages(pkg)      // Scan com.xyz and subpackages (omit to scan all packages)
      .scan().use {                   // Start the scan
        scanResult ->
        for (routeClassInfo in scanResult.getClassesWithAnnotation(routeAnnotation)) {
          val typeClass: ClassInfo = routeClassInfo.interfaces[0]
          val dataType: TypeArgument = routeClassInfo.typeSignature.superinterfaceSignatures[0].typeArguments[0]

          // val routeAnnotationInfo = routeClassInfo.getAnnotationInfo(routeAnnotation)
          // @com.xyz.Route has one required parameter
          println("$dataType provides ${typeClass.name} instance through ${routeClassInfo.name}")
        }
      }
  }
}
