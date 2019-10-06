package kastree.ast

open class Visitor {
  fun visit(v: Node) = visit(v, v)

  protected open fun visit(v: Node?, parent: Node) = v.run {
    when (this) {
      is Node.File -> {
        visitChildren(anns)
        visitChildren(pkg)
        visitChildren(imports)
        visitChildren(decls)
      }
      is Node.Script -> {
        visitChildren(anns)
        visitChildren(pkg)
        visitChildren(imports)
        visitChildren(exprs)
      }
      is Node.Package -> {
        visitChildren(mods)
      }
      is Node.Import -> {}
      is Node.Decl.Structured -> {
        visitChildren(mods)
        visitChildren(typeParams)
        visitChildren(primaryConstructor)
        visitChildren(parentAnns)
        visitChildren(parents)
        visitChildren(typeConstraints)
        visitChildren(members)
      }
      is Node.Decl.Structured.Parent.CallConstructor -> {
        visitChildren(type)
        visitChildren(typeArgs)
        visitChildren(args)
        visitChildren(lambda)
      }
      is Node.Decl.Structured.Parent.Type -> {
        visitChildren(type)
        visitChildren(by)
      }
      is Node.Decl.Structured.PrimaryConstructor -> {
        visitChildren(mods)
        visitChildren(params)
      }
      is Node.Decl.Init -> {
        visitChildren(block)
      }
      is Node.Decl.Func -> {
        visitChildren(mods)
        visitChildren(typeParams)
        visitChildren(receiverType)
        visitChildren(paramTypeParams)
        visitChildren(params)
        visitChildren(type)
        visitChildren(typeConstraints)
        visitChildren(body)
      }
      is Node.Decl.Func.Param -> {
        visitChildren(mods)
        visitChildren(type)
        visitChildren(default)
      }
      is Node.Decl.Func.Body.Block -> {
        visitChildren(block)
      }
      is Node.Decl.Func.Body.Expr -> {
        visitChildren(expr)
      }
      is Node.Decl.Property -> {
        visitChildren(mods)
        visitChildren(typeParams)
        visitChildren(receiverType)
        visitChildren(vars)
        visitChildren(typeConstraints)
        visitChildren(expr)
        visitChildren(accessors)
      }
      is Node.Decl.Property.Var -> {
        visitChildren(type)
      }
      is Node.Decl.Property.Accessors -> {
        visitChildren(first)
        visitChildren(second)
      }
      is Node.Decl.Property.Accessor.Get -> {
        visitChildren(mods)
        visitChildren(type)
        visitChildren(body)
      }
      is Node.Decl.Property.Accessor.Set -> {
        visitChildren(mods)
        visitChildren(paramMods)
        visitChildren(paramType)
        visitChildren(body)
      }
      is Node.Decl.TypeAlias -> {
        visitChildren(mods)
        visitChildren(typeParams)
        visitChildren(type)
      }
      is Node.Decl.Constructor -> {
        visitChildren(mods)
        visitChildren(params)
        visitChildren(delegationCall)
        visitChildren(block)
      }
      is Node.Decl.Constructor.DelegationCall -> {
        visitChildren(args)
      }
      is Node.Decl.EnumEntry -> {
        visitChildren(mods)
        visitChildren(args)
        visitChildren(members)
      }
      is Node.TypeParam -> {
        visitChildren(mods)
        visitChildren(type)
      }
      is Node.TypeConstraint -> {
        visitChildren(anns)
        visitChildren(type)
      }
      is Node.TypeRef.Paren -> {
        visitChildren(mods)
        visitChildren(type)
      }
      is Node.TypeRef.Func -> {
        visitChildren(receiverType)
        visitChildren(params)
        visitChildren(type)
      }
      is Node.TypeRef.Func.Param -> {
        visitChildren(type)
      }
      is Node.TypeRef.Simple -> {
        visitChildren(pieces)
      }
      is Node.TypeRef.Simple.Piece -> {
        visitChildren(typeParams)
      }
      is Node.TypeRef.Nullable -> {
        visitChildren(type)
      }
      is Node.TypeRef.Dynamic -> {}
      is Node.Type -> {
        visitChildren(mods)
        visitChildren(ref)
      }
      is Node.ValueArg -> {
        visitChildren(expr)
      }
      is Node.Expr.If -> {
        visitChildren(expr)
        visitChildren(body)
        visitChildren(elseBody)
      }
      is Node.Expr.Try -> {
        visitChildren(block)
        visitChildren(catches)
        visitChildren(finallyBlock)
      }
      is Node.Expr.Try.Catch -> {
        visitChildren(anns)
        visitChildren(varType)
        visitChildren(block)
      }
      is Node.Expr.For -> {
        visitChildren(anns)
        visitChildren(vars)
        visitChildren(inExpr)
        visitChildren(body)
      }
      is Node.Expr.While -> {
        visitChildren(expr)
        visitChildren(body)
      }
      is Node.Expr.BinaryOp -> {
        visitChildren(lhs)
        visitChildren(oper)
        visitChildren(rhs)
      }
      is Node.Expr.TypeOp.Oper -> {}
      is Node.Expr.TypeOp -> {
        visitChildren(lhs)
        visitChildren(oper)
        visitChildren(rhs)
      }
      is Node.Expr.BinaryOp.Oper.Infix -> {}
      is Node.Expr.BinaryOp.Oper.Token -> {}
      is Node.Expr.UnaryOp -> {
        visitChildren(expr)
        visitChildren(oper)
      }
      is Node.Expr.UnaryOp.Oper -> {}
      is Node.Expr.DoubleColonRef.Callable -> {
        visitChildren(recv)
      }
      is Node.Expr.DoubleColonRef.Class -> {
        visitChildren(recv)
      }
      is Node.Expr.DoubleColonRef.Recv.Expr -> {
        visitChildren(expr)
      }
      is Node.Expr.DoubleColonRef.Recv.Type -> {
        visitChildren(type)
      }
      is Node.Expr.Paren -> {
        visitChildren(expr)
      }
      is Node.Expr.StringTmpl -> {
        visitChildren(elems)
      }
      is Node.Expr.StringTmpl.Elem.Regular -> {}
      is Node.Expr.StringTmpl.Elem.ShortTmpl -> {}
      is Node.Expr.StringTmpl.Elem.UnicodeEsc -> {}
      is Node.Expr.StringTmpl.Elem.RegularEsc -> {}
      is Node.Expr.StringTmpl.Elem.LongTmpl -> {
        visitChildren(expr)
      }
      is Node.Expr.Const -> {}
      is Node.Expr.Brace -> {
        visitChildren(params)
        visitChildren(block)
      }
      is Node.Expr.Brace.Param -> {
        visitChildren(vars)
        visitChildren(destructType)
      }
      is Node.Expr.This -> {}
      is Node.Expr.Super -> {
        visitChildren(typeArg)
      }
      is Node.Expr.When -> {
        visitChildren(expr)
        visitChildren(entries)
      }
      is Node.Expr.When.Entry -> {
        visitChildren(conds)
        visitChildren(body)
      }
      is Node.Expr.When.Cond.Expr -> {
        visitChildren(expr)
      }
      is Node.Expr.When.Cond.In -> {
        visitChildren(expr)
      }
      is Node.Expr.When.Cond.Is -> {
        visitChildren(type)
      }
      is Node.Expr.Object -> {
        visitChildren(parents)
        visitChildren(members)
      }
      is Node.Expr.Throw -> {
        visitChildren(expr)
      }
      is Node.Expr.Return -> {
        visitChildren(expr)
      }
      is Node.Expr.Continue -> {}
      is Node.Expr.Break -> {}
      is Node.Expr.CollLit -> {
        visitChildren(exprs)
      }
      is Node.Expr.Name -> {}
      is Node.Expr.Labeled -> {
        visitChildren(expr)
      }
      is Node.Expr.Annotated -> {
        visitChildren(anns)
        visitChildren(expr)
      }
      is Node.Expr.Call -> {
        visitChildren(expr)
        visitChildren(typeArgs)
        visitChildren(args)
        visitChildren(lambda)
      }
      is Node.Expr.Call.TrailLambda -> {
        visitChildren(anns)
        visitChildren(func)
      }
      is Node.Expr.ArrayAccess -> {
        visitChildren(expr)
        visitChildren(indices)
      }
      is Node.Expr.AnonFunc -> {
        visitChildren(func)
      }
      is Node.Expr.Property ->  {
        visitChildren(decl)
      }
      is Node.Block -> {
        visitChildren(stmts)
      }
      is Node.Stmt.Decl -> {
        visitChildren(decl)
      }
      is Node.Stmt.Expr -> {
        visitChildren(expr)
      }
      is Node.Modifier.AnnotationSet -> {
        visitChildren(anns)
      }
      is Node.Modifier.AnnotationSet.Annotation -> {
        visitChildren(typeArgs)
        visitChildren(args)
      }
      is Node.Modifier.Lit -> {}
      is Node.Extra.BlankLines -> {}
      is Node.Extra.Comment -> {}
      null -> TODO()
    }
  }

  protected inline fun <T: Node?> Node?.visitChildren(v: T) { visit(v, this!!) }

  protected inline fun <T: Node?> Node?.visitChildren(v: List<T>) { v.forEach { orig -> visit(orig, this!!) } }

  companion object {
    fun visit(v: Node, fn: (v: Node?, parent: Node) -> Unit) = object : Visitor() {
      override fun visit(v: Node?, parent: Node) {
        fn(v, parent)
        super.visit(v, parent)
      }
    }.visit(v)
  }
}
