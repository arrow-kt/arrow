package arrow.meta.encoder

import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

data class TypeClassInstance(
  val instance: Type,
  val dataType: Type,
  val typeClass: Type,
  val instanceTypeElement: TypeElement,
  val dataTypeTypeElement: TypeElement,
  val typeClassTypeElement: TypeElement,
  val projectedCompanion: TypeName
)

interface MetaApi {

  fun TypeName.WildcardType.removeConstrains(): TypeName.WildcardType
  fun TypeName.ParameterizedType.removeConstrains(): TypeName.ParameterizedType
  fun TypeName.Classy.removeConstrains(): TypeName.Classy
  fun TypeName.removeConstrains(): TypeName
  fun Parameter.removeConstrains(): Parameter
  fun Func.addExtraDummyArg(): Func
  fun Func.removeDummyArgs(): Func
  fun Func.countDummyArgs(): Int
  fun Func.removeConstrains(): Func
  fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable
  fun TypeName.TypeVariable.disambiguate(existing: List<TypeName.TypeVariable>, prefix: String = "_"): TypeName.TypeVariable
  fun Parameter.downKind(): Parameter
  fun Parameter.defaultDummyArgValue(): Parameter
  fun Func.downKindParameters(): Func
  fun Func.downKindReceiver(): Func
  fun Func.downKindReturnType(): Func
  fun Func.defaultDummyArgValues(): Func
  tailrec fun TypeName.asType(): Type?
  val Code.Companion.TODO: Code
  fun TypeName.TypeVariable.downKind(): TypeName.TypeVariable
  fun TypeName.ParameterizedType.isKinded(): Boolean
  fun TypeName.TypeVariable.nestedTypeVariables(): List<TypeName>
  fun TypeName.WildcardType.nestedTypeVariables(): List<TypeName>
  fun TypeName.ParameterizedType.nestedTypeVariables(): List<TypeName>
  fun TypeName.Classy.nestedTypeVariables(): List<TypeName>
  fun TypeName.nestedTypeVariables(): List<TypeName>
  fun TypeName.ParameterizedType.downKind(): TypeName.ParameterizedType
  fun Func.containsModifier(modifier: Modifier): Boolean
  fun TypeName.WildcardType.downKind(): TypeName.WildcardType
  fun TypeName.Classy.downKind(): TypeName.Classy
  fun typeNameDownKindImpl(typeName: TypeName): TypeName
  fun TypeName.downKind(): TypeName
  fun TypeName.asKotlin(): TypeName
  fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable
  fun TypeName.ParameterizedType.asKotlin(): TypeName.ParameterizedType
  fun TypeName.WildcardType.asKotlin(): TypeName.WildcardType
  fun TypeName.Classy.asKotlin(): TypeName.Classy
  val TypeClassInstance.requiredAbstractFunctions: List<Func>
  val TypeClassInstance.requiredParameters: List<Parameter>
  fun <A : Any> TypeName.Companion.typeNameOf(clazz: KClass<A>): TypeName
  fun JvmName(name: String): Annotation
  fun SuppressAnnotation(vararg names: String): Annotation
  fun TypeName.projectedCompanion(): TypeName
  fun TypeElement.typeClassInstance(): TypeClassInstance?
}