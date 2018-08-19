package arrow.meta.decoder

import arrow.meta.ast.Code
import arrow.meta.ast.Tree
import arrow.meta.ast.Type
import com.squareup.kotlinpoet.TypeSpec

interface MetaDecoder<in A: Tree> {
  fun decode(tree: A): Code
}

object TypeDecoder : MetaDecoder<Type> {
  override fun decode(tree: Type): Code {
    when (tree.kind) {
      Type.Kind.Class -> TypeSpec.classBuilder(tree.name)
      Type.Kind.Interface -> TODO()
      Type.Kind.Object -> TODO()
    }
  }
}