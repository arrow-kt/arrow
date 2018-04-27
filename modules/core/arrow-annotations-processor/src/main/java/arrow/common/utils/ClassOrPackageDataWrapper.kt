package arrow.common.utils

import me.eugeniomarletti.kotlin.metadata.ClassData
import me.eugeniomarletti.kotlin.metadata.PackageData
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver

sealed class ClassOrPackageDataWrapper {
  abstract val `package`: String
  abstract val nameResolver: NameResolver
  abstract val constructorList: List<ProtoBuf.Constructor>
  abstract val functionList: List<ProtoBuf.Function>
  abstract val propertyList: List<ProtoBuf.Property>
  abstract val typeParameters: List<ProtoBuf.TypeParameter>
  abstract fun getTypeParameter(typeParameterIndex: Int): ProtoBuf.TypeParameter?

  class Package(
    override val nameResolver: NameResolver,
    val packageProto: ProtoBuf.Package,
    override val `package`: String
  ) : ClassOrPackageDataWrapper() {
    override val constructorList: List<ProtoBuf.Constructor> get() = emptyList()
    override val functionList: List<ProtoBuf.Function> get() = packageProto.functionList
    override val propertyList: List<ProtoBuf.Property> get() = packageProto.propertyList
    override val typeParameters: List<ProtoBuf.TypeParameter> = emptyList()
    override fun getTypeParameter(typeParameterIndex: Int): ProtoBuf.TypeParameter? = null
  }

  class Class(
    override val nameResolver: NameResolver,
    val classProto: ProtoBuf.Class,
    override val `package`: String
  ) : ClassOrPackageDataWrapper() {
    override val constructorList: List<ProtoBuf.Constructor> get() = classProto.constructorList
    override val functionList: List<ProtoBuf.Function> get() = classProto.functionList
    override val propertyList: List<ProtoBuf.Property> get() = classProto.propertyList
    override val typeParameters: List<ProtoBuf.TypeParameter> = classProto.typeParameterList
    override fun getTypeParameter(typeParameterIndex: Int): ProtoBuf.TypeParameter? = classProto.getTypeParameter(typeParameterIndex)
  }
}

fun ClassData.asClassOrPackageDataWrapper(`package`: String) =
  ClassOrPackageDataWrapper.Class(nameResolver, classProto, `package`)

fun PackageData.asClassOrPackageDataWrapper(`package`: String) =
  ClassOrPackageDataWrapper.Package(nameResolver, packageProto, `package`)
