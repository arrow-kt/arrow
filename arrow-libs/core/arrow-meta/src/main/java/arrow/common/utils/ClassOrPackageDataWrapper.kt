package arrow.common.utils

import me.eugeniomarletti.kotlin.metadata.ClassData
import me.eugeniomarletti.kotlin.metadata.PackageData
import org.jetbrains.kotlin.metadata.ProtoBuf.Constructor
import org.jetbrains.kotlin.metadata.ProtoBuf.Function
import org.jetbrains.kotlin.metadata.ProtoBuf.Property
import org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter
import org.jetbrains.kotlin.metadata.deserialization.NameResolver

public sealed class ClassOrPackageDataWrapper {
  public abstract val `package`: String
  public abstract val nameResolver: NameResolver
  public abstract val constructorList: List<Constructor>
  public abstract val functionList: List<Function>
  public abstract val propertyList: List<Property>
  public abstract val typeParameters: List<TypeParameter>
  public abstract fun getTypeParameter(typeParameterIndex: Int): TypeParameter?

  public class Package(
    override val nameResolver: NameResolver,
    public val packageProto: org.jetbrains.kotlin.metadata.ProtoBuf.Package,
    override val `package`: String
  ) : ClassOrPackageDataWrapper() {
    override val constructorList: List<Constructor> get() = emptyList()
    override val functionList: List<Function> get() = packageProto.functionList
    override val propertyList: List<Property> get() = packageProto.propertyList
    override val typeParameters: List<TypeParameter> = emptyList()
    override fun getTypeParameter(typeParameterIndex: Int): TypeParameter? = null
  }

  public class Class(
    override val nameResolver: NameResolver,
    public val classProto: org.jetbrains.kotlin.metadata.ProtoBuf.Class,
    override val `package`: String
  ) : ClassOrPackageDataWrapper() {
    override val constructorList: List<Constructor> get() = classProto.constructorList
    override val functionList: List<Function> get() = classProto.functionList
    override val propertyList: List<Property> get() = classProto.propertyList
    override val typeParameters: List<TypeParameter> = classProto.typeParameterList
    override fun getTypeParameter(typeParameterIndex: Int): TypeParameter? =
      classProto.typeParameterList.getOrNull(typeParameterIndex)
  }
}

public fun ClassData.asClassOrPackageDataWrapper(`package`: String): ClassOrPackageDataWrapper.Class =
  ClassOrPackageDataWrapper.Class(nameResolver, classProto, `package`)

public fun PackageData.asClassOrPackageDataWrapper(`package`: String): ClassOrPackageDataWrapper.Package =
  ClassOrPackageDataWrapper.Package(nameResolver, packageProto, `package`)
