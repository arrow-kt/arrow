package kastree.ast

open class Writer(
  val app: Appendable = StringBuilder(),
  val extrasMap: ExtrasMap? = null,
  val includeExtraBlankLines: Boolean = extrasMap == null
) : Visitor() {

  protected var indent = ""
  protected var elemsSinceLastLine = emptyList<Node>()

  protected fun endLine() = also {
    val elems = elemsSinceLastLine
    elemsSinceLastLine = emptyList()
    elems.forEach { it.writeExtrasLineEnd() }
    append('\n')
  }

  protected fun line() = endLine()
  protected fun line(str: String) = append(indent).append(str).endLine()
  protected fun lineBegin(str: String = "") = append(indent).append(str)
  protected fun lineEnd(str: String = "") = append(str).endLine()
  protected fun append(ch: Char) = also { app.append(ch) }
  protected fun append(str: String) = also { app.append(str) }
  protected fun appendName(name: String) =
    if (name.shouldEscapeIdent) append("`$name`") else append(name)

  protected fun appendNames(names: List<String>, sep: String) = also {
    names.forEachIndexed { index, name ->
      if (index > 0) append(sep)
      appendName(name)
    }
  }

  protected fun <T> indented(fn: () -> T): T = run {
    indent += "    "
    fn().also { indent = indent.dropLast(4) }
  }

  fun write(v: Node) {
    visit(v, v)
  }

  override fun visit(v: Node?, parent: Node) {
    v?.writeExtrasBefore()
    v?.apply {
      val replacedSource = this.dynamic
      if (replacedSource != null) append(replacedSource)
      else when (this) {
        is Node.File -> {
          if (anns.isNotEmpty()) childAnns().line()
          childrenLines(pkg, extraEndLines = 1)
          childrenLines(imports, extraEndLines = 1)
          childrenLines(decls, extraMidLines = 1)
        }
        is Node.Package ->
          childMods().append("package ").appendNames(names, ".")
        is Node.Import -> {
          append("import ").appendNames(names, ".")
          if (wildcard) append(".*") else if (alias != null) append(" as ").appendName(alias)
        }
        is Node.Decl.Structured -> childMods().also {
          append(when (form) {
            Node.Decl.Structured.Form.CLASS -> "class "
            Node.Decl.Structured.Form.ENUM_CLASS -> "enum class "
            Node.Decl.Structured.Form.INTERFACE -> "interface "
            Node.Decl.Structured.Form.OBJECT -> "object "
            Node.Decl.Structured.Form.COMPANION_OBJECT -> "companion object "
          })
          if (form != Node.Decl.Structured.Form.COMPANION_OBJECT || name != "Companion") appendName(name)
          bracketedChildren(typeParams)
          children(primaryConstructor)
          if (parents.isNotEmpty()) {
            append(" : ")
            children(parentAnns)
            children(parents, ", ")
          }
          childTypeConstraints(typeConstraints)
          if (members.isNotEmpty()) lineEnd(" {").indented {
            // First, do all the enum entries if there are any
            val enumEntries = members.map { it as? Node.Decl.EnumEntry }.takeWhile { it != null }
            enumEntries.forEachIndexed { index, enumEntry ->
              lineBegin().also { children(enumEntry) }
              when (index) {
                members.size - 1 -> lineEnd()
                enumEntries.size - 1 -> lineEnd(";").line()
                else -> lineEnd(",")
              }
            }
            // Now the rest of the members
            childrenLines(members.drop(enumEntries.size), extraMidLines = 1)
          }.lineBegin("}")

          // As a special case, if an object is nameless and bodyless, we should give it an empty body
          // to avoid ambiguities with the next item
          // See: https://youtrack.jetbrains.com/issue/KT-25581
          if ((form == Node.Decl.Structured.Form.COMPANION_OBJECT ||
              form == Node.Decl.Structured.Form.OBJECT) && name == "Companion" && members.isEmpty())
            append("{}")
        }
        is Node.Decl.Structured.Parent.CallConstructor -> {
          children(type)
          parenChildren(args)
        }
        is Node.Decl.Structured.Parent.Type -> {
          children(type)
          if (by != null) append(" by ").also { children(by) }
        }
        is Node.Decl.Structured.PrimaryConstructor -> {
          if (mods.isNotEmpty()) append(" ").also { childMods(newlines = false).append("constructor") }
          parenChildren(params)
        }
        is Node.Decl.Init ->
          append("init ").also { children(block) }
        is Node.Decl.Func -> {
          childMods().append("fun")
          if (name != null || typeParams.isNotEmpty() || receiverType != null) append(' ')
          bracketedChildren(typeParams, " ")
          if (receiverType != null) children(receiverType).append(".")
          name?.also { appendName(it) }
          bracketedChildren(paramTypeParams)
          parenChildren(params)
          if (type != null) append(": ").also { children(type) }
          childTypeConstraints(typeConstraints)
          if (body != null) append(' ').also { children(body) }
        }
        is Node.Decl.Func.Param -> {
          if (mods.isNotEmpty()) childMods(newlines = false).append(' ')
          if (readOnly == true) append("val ") else if (readOnly == false) append("var ")
          appendName(name)
          if (type != null) append(": ").also { children(type) }
          if (default != null) append(" = ").also { children(default) }
        }
        is Node.Decl.Func.Body.Block ->
          children(block)
        is Node.Decl.Func.Body.Expr ->
          append("= ").also { children(expr) }
        is Node.Decl.Property -> {
          childMods().append(if (readOnly) "val " else "var ")
          bracketedChildren(typeParams, " ")
          if (receiverType != null) children(receiverType).append('.')
          childVars(vars)
          childTypeConstraints(typeConstraints)
          if (expr != null) {
            if (delegated) append(" by ") else append(" = ")
            children(expr)
          }
          if (accessors != null) lineEnd().indented { children(accessors) }
        }
        is Node.Decl.Property.Var -> {
          appendName(name)
          if (type != null) append(": ").also { children(type) }
        }
        is Node.Decl.Property.Accessors -> {
          childrenLines(first)
          if (second != null) childrenLines(second)
        }
        is Node.Decl.Property.Accessor.Get -> {
          childMods().append("get")
          if (body != null) {
            append("()")
            if (type != null) append(": ").also { children(type) }
            append(' ').also { children(body) }
          }
        }
        is Node.Decl.Property.Accessor.Set -> {
          childMods().append("set")
          if (body != null) {
            append('(')
            childMods(paramMods, newlines = false)
            appendName(paramName ?: error("Missing setter param name when body present"))
            if (paramType != null) append(": ").also { children(paramType) }
            append(") ")
            children(body)
          }
        }
        is Node.Decl.TypeAlias -> {
          childMods().append("typealias ").appendName(name)
          bracketedChildren(typeParams).append(" = ")
          children(type)
        }
        is Node.Decl.Constructor -> {
          childMods().append("constructor")
          parenChildren(params)
          if (delegationCall != null) append(": ").also { children(delegationCall) }
          if (block != null) append(' ').also { children(block) }
        }
        is Node.Decl.Constructor.DelegationCall ->
          append(target.name.toLowerCase()).also { parenChildren(args) }
        is Node.Decl.EnumEntry -> {
          childMods().appendName(name)
          if (args.isNotEmpty()) parenChildren(args)
          if (members.isNotEmpty()) lineEnd(" {").indented {
            childrenLines(members, extraMidLines = 1)
          }.lineBegin("}")
        }
        is Node.TypeParam -> {
          childMods(newlines = false).appendName(name)
          if (type != null) append(": ").also { children(type) }
        }
        is Node.TypeConstraint ->
          childAnns(sameLine = true).appendName(name).append(": ").also { children(type) }
        is Node.TypeRef.Paren ->
          append('(').also {
            childModsBeforeType(type).also { children(type) }
          }.append(')')
        is Node.TypeRef.Func -> {
          if (receiverType != null) children(receiverType).append('.')
          parenChildren(params).append(" -> ").also { children(type) }
        }
        is Node.TypeRef.Func.Param -> {
          if (name != null) appendName(name).append(": ")
          children(type)
        }
        is Node.TypeRef.Simple ->
          children(pieces, ".")
        is Node.TypeRef.Simple.Piece ->
          appendName(name).also { bracketedChildren(typeParams) }
        is Node.TypeRef.Nullable ->
          children(type).append('?')
        is Node.TypeRef.Dynamic ->
          append("dynamic")
        is Node.Type ->
          childModsBeforeType(ref).also { children(ref) }
        is Node.ValueArg -> {
          if (name != null) appendName(name).append(" = ")
          if (asterisk) append('*')
          children(expr)
        }
        is Node.Expr.If -> {
          append("if (").also { children(expr) }.append(") ")
          children(body)
          if (elseBody != null) append(" else ").also { children(elseBody) }
        }
        is Node.Expr.Try -> {
          append("try ")
          children(block)
          if (catches.isNotEmpty()) children(catches, " ", prefix = " ")
          if (finallyBlock != null) append(" finally ").also { children(finallyBlock) }
        }
        is Node.Expr.Try.Catch -> {
          append("catch (")
          childAnns(sameLine = true)
          appendName(varName).append(": ").also { children(varType) }.append(") ")
          children(block)
        }
        is Node.Expr.For -> {
          append("for (")
          childAnns(sameLine = true)
          childVars(vars).append(" in ").also { children(inExpr) }.append(") ")
          children(body)
        }
        is Node.Expr.While -> {
          if (!doWhile) append("while (").also { children(expr) }.append(") ").also { children(body) }
          else append("do ").also { children(body) }.append(" while (").also { children(expr) }.append(')')
        }
        is Node.Expr.BinaryOp -> {
          // Some operations don't have separators
          val noSep = oper is Node.Expr.BinaryOp.Oper.Token && oper.token.let {
            it == Node.Expr.BinaryOp.Token.RANGE || it == Node.Expr.BinaryOp.Token.DOT ||
              it == Node.Expr.BinaryOp.Token.DOT_SAFE
          }
          children(listOf(lhs, oper, rhs), if (noSep) "" else " ")
        }
        is Node.Expr.BinaryOp.Oper.Infix ->
          append(str)
        is Node.Expr.BinaryOp.Oper.Token ->
          append(token.str)
        is Node.Expr.UnaryOp ->
          if (prefix) children(oper, expr) else children(expr, oper)
        is Node.Expr.UnaryOp.Oper ->
          append(token.str)
        is Node.Expr.TypeOp ->
          children(listOf(lhs, oper, rhs), " ")
        is Node.Expr.TypeOp.Oper ->
          append(token.str)
        is Node.Expr.DoubleColonRef.Callable -> {
          if (recv != null) children(recv)
          append("::").appendName(name)
        }
        is Node.Expr.DoubleColonRef.Class -> {
          if (recv != null) children(recv)
          append("::class")
        }
        is Node.Expr.DoubleColonRef.Recv.Expr ->
          children(expr)
        is Node.Expr.DoubleColonRef.Recv.Type ->
          children(type).append("?".repeat(questionMarks))
        is Node.Expr.Paren ->
          append('(').also { children(expr) }.append(')')
        is Node.Expr.StringTmpl ->
          if (raw) append("\"\"\"").also { children(elems) }.append("\"\"\"")
          else append('"').also { children(elems) }.append('"')
        is Node.Expr.StringTmpl.Elem.Regular ->
          append(str)
        is Node.Expr.StringTmpl.Elem.ShortTmpl ->
          append('$').appendName(str)
        is Node.Expr.StringTmpl.Elem.UnicodeEsc ->
          append("\\u").append(digits)
        is Node.Expr.StringTmpl.Elem.RegularEsc ->
          append('\\').append(when (char) {
            '\b' -> 'b'
            '\n' -> 'n'
            '\t' -> 't'
            '\r' -> 'r'
            else -> char
          })
        is Node.Expr.StringTmpl.Elem.LongTmpl ->
          append("\${").also { children(expr) }.append('}')
        is Node.Expr.Const ->
          append(value)
        is Node.Expr.Brace -> {
          append('{')
          if (params.isNotEmpty()) append(' ').also { children(params, ", ", "", " ->") }
          children(block).append('}')
        }
        is Node.Expr.Brace.Param -> {
          childVars(vars)
          if (destructType != null) append(": ").also { children(destructType) }
        }
        is Node.Expr.This -> {
          append("this")
          if (label != null) append('@').appendName(label)
        }
        is Node.Expr.Super -> {
          append("super")
          if (typeArg != null) append('<').also { children(typeArg) }.append('>')
          if (label != null) append('@').appendName(label)
        }
        is Node.Expr.When -> {
          append("when")
          if (expr != null) append('(').also { children(expr) }.append(')')
          lineEnd(" {").indented { childrenLines(entries) }.lineBegin("}")
        }
        is Node.Expr.When.Entry -> {
          if (conds.isEmpty()) append("else")
          else children(conds, ", ")
          append(" -> ").also { children(body) }
        }
        is Node.Expr.When.Cond.Expr ->
          children(expr)
        is Node.Expr.When.Cond.In -> {
          if (not) append('!')
          append("in ").also { children(expr) }
        }
        is Node.Expr.When.Cond.Is -> {
          if (not) append('!')
          append("is ").also { children(type) }
        }
        is Node.Expr.Object -> {
          append("object")
          if (parents.isNotEmpty()) append(" : ").also { children(parents, ", ") }
          if (members.isEmpty()) append(" {}") else lineEnd(" {").indented {
            childrenLines(members, extraMidLines = 1)
          }.lineBegin("}")
        }
        is Node.Expr.Throw ->
          append("throw ").also { children(expr) }
        is Node.Expr.Return -> {
          append("return")
          if (label != null) append('@').appendName(label)
          if (expr != null) append(' ').also { children(expr) }
        }
        is Node.Expr.Continue -> {
          append("continue")
          if (label != null) append('@').appendName(label)
        }
        is Node.Expr.Break -> {
          append("break")
          if (label != null) append('@').appendName(label)
        }
        is Node.Expr.CollLit ->
          children(exprs, ", ", "[", "]")
        is Node.Expr.Name ->
          appendName(name)
        is Node.Expr.Labeled ->
          appendName(label).append("@ ").also { children(expr) }
        is Node.Expr.Annotated ->
          childAnnsBeforeExpr(expr).also { children(expr) }
        is Node.Expr.Call -> {
          children(expr)
          bracketedChildren(typeArgs)
          if (args.isNotEmpty() || lambda == null) parenChildren(args)
          if (lambda != null) append(' ').also { children(lambda) }
        }
        is Node.Expr.Call.TrailLambda -> {
          if (anns.isNotEmpty()) childAnns(sameLine = true).append(' ')
          if (label != null) appendName(label).append("@ ")
          children(func)
        }
        is Node.Expr.ArrayAccess -> {
          children(expr)
          children(indices, ", ", "[", "]")
        }
        is Node.Expr.AnonFunc ->
          children(func)
        is Node.Expr.Property ->
          children(decl)
        is Node.Block -> {
          // Special case, no braces if the parent is a brace
          if (parent is Node.Expr.Brace) {
            if (stmts.isNotEmpty()) lineEnd().indented { childrenLines(stmts) }.lineBegin()
          } else if (stmts.isEmpty()) append("{}")
          else lineEnd("{").indented { childrenLines(stmts) }.lineBegin("}")
        }
        is Node.Stmt.Decl -> {
          children(decl)
        }
        is Node.Stmt.Expr ->
          children(expr)
        is Node.Modifier.AnnotationSet -> {
          append('@')
          if (target != null) append(target.name.toLowerCase()).append(':')
          if (anns.size == 1) children(anns)
          else children(anns, " ", "[", "]")
        }
        is Node.Modifier.AnnotationSet.Annotation -> {
          appendNames(names, ".")
          bracketedChildren(typeArgs)
          if (args.isNotEmpty()) parenChildren(args)
        }
        is Node.Modifier.Lit ->
          append(keyword.name.toLowerCase())
        else ->
          error("Unrecognized node type: $this")
      }
    }
    v?.writeExtrasAfter()
    v?.also { elemsSinceLastLine += it }
  }

  protected open fun Node.writeExtrasBefore() {
    if (extrasMap == null) return
    // Write everything before
    writeExtras(extrasMap.extrasBefore(this))
  }

  protected open fun Node.writeExtrasAfter() {
    if (extrasMap == null) return
    // Write everything after that doesn't start a line or end a line
    writeExtras(extrasMap.extrasAfter(this).takeWhile {
      it is Node.Extra.Comment && !it.startsLine && !it.endsLine
    })
  }

  protected open fun Node.writeExtrasLineEnd() {
    if (extrasMap == null) return
    // Write everything after the first non-line starter/ender
    writeExtras(extrasMap.extrasAfter(this).dropWhile {
      it is Node.Extra.Comment && !it.startsLine && !it.endsLine
    })
  }

  protected open fun Node.writeExtras(extras: List<Node.Extra>) {
    extras.forEach {
      when (it) {
        is Node.Extra.BlankLines -> {
          (2..it.count).forEach { line() }
          lineEnd().lineBegin()
        }
        is Node.Extra.Comment -> {
          if (it.startsLine && it.endsLine) lineEnd(it.text).lineBegin() else {
            if (!it.startsLine) append(' ')
            append(it.text)
            if (!it.endsLine) append(' ')
          }
        }
      }
    }
  }

  protected fun Node.childTypeConstraints(v: List<Node.TypeConstraint>) = this@Writer.also {
    if (v.isNotEmpty()) append(" where ").also { children(v, ", ") }
  }

  protected fun Node.childVars(vars: List<Node.Decl.Property.Var?>) =
    if (vars.size == 1) {
      if (vars.single() == null) append('_') else children(vars)
    } else {
      append('(')
      vars.forEachIndexed { index, v ->
        if (v == null) append('_') else children(v)
        if (index < vars.size - 1) append(", ")
      }
      append(')')
    }

  // Ends with newline (or space if sameLine) if there are any
  protected fun Node.WithAnnotations.childAnns(sameLine: Boolean = false) = this@Writer.also {
    if (anns.isNotEmpty()) (this@childAnns as Node).apply {
      if (sameLine) children(anns, " ", "", " ")
      else anns.forEach { ann -> lineBegin().also { children(ann) }.lineEnd() }
    }
  }

  protected fun Node.WithAnnotations.childAnnsBeforeExpr(expr: Node.Expr) = this@Writer.also {
    if (anns.isNotEmpty()) {
      // As a special case, if there is a trailing annotation with no args and expr is paren,
      // then we need to add an empty set of parens ourselves
      val lastAnn = anns.lastOrNull()?.anns?.singleOrNull()?.takeIf { it.args.isEmpty() }
      val shouldAddParens = lastAnn != null && expr is Node.Expr.Paren
      (this as Node).children(anns, " ")
      if (shouldAddParens) append("()")
      append(' ')
    }
  }

  // Ends with newline if last is ann or space is last is mod or nothing if empty
  protected fun Node.WithModifiers.childMods(newlines: Boolean = true) =
    (this@childMods as Node).childMods(mods, newlines)

  protected fun Node.childMods(mods: List<Node.Modifier>, newlines: Boolean = true) =
    this@Writer.also {
      if (mods.isNotEmpty()) this@childMods.apply {
        mods.forEachIndexed { index, mod ->
          children(mod)
          if (newlines && (mod is Node.Modifier.AnnotationSet ||
              mods.getOrNull(index + 1) is Node.Modifier.AnnotationSet))
            lineEnd().lineBegin()
          else append(' ')
        }
      }
    }

  protected fun Node.WithModifiers.childModsBeforeType(ref: Node.TypeRef) = this@Writer.also {
    if (mods.isNotEmpty()) {
      // As a special case, if there is a trailing annotation with no args and the ref has a paren which is a paren
      // type or a non-receiver fn type, then we need to add an empty set of parens ourselves
      val lastAnn = (mods.lastOrNull() as? Node.Modifier.AnnotationSet)?.anns?.singleOrNull()?.takeIf { it.args.isEmpty() }
      val shouldAddParens = lastAnn != null &&
        (ref is Node.TypeRef.Paren || (ref is Node.TypeRef.Func && (
          ref.receiverType == null || ref.receiverType.ref is Node.TypeRef.Paren)))
      (this as Node).children(mods, " ")
      if (shouldAddParens) append("()")
      append(' ')
    }
  }

  protected inline fun Node.children(vararg v: Node?) = this@Writer.also { v.forEach { visitChildren(it) } }

  // Null list values are asterisks
  protected fun Node.bracketedChildren(v: List<Node?>, appendIfNotEmpty: String = "") = this@Writer.also {
    if (v.isNotEmpty()) {
      append('<')
      v.forEachIndexed { index, node ->
        if (index > 0) append(", ")
        if (node == null) append('*') else children(node)
      }
      append('>').append(appendIfNotEmpty)
    }
  }

  protected fun Node.parenChildren(v: List<Node?>) = children(v, ", ", "(", ")")

  protected fun Node.childrenLines(v: Node?, extraMidLines: Int = 0, extraEndLines: Int = 0) =
    this@Writer.also { if (v != null) childrenLines(listOf(v), extraMidLines, extraEndLines) }

  protected fun Node.childrenLines(v: List<Node?>, extraMidLines: Int = 0, extraEndLines: Int = 0) =
    this@Writer.also {
      v.forEachIndexed { index, node ->
        lineBegin().also { children(node) }
        if (stmtRequiresEmptyBraceSetBeforeLineEnd(node, v.getOrNull(index + 1))) append(" {}")
        if (stmtRequiresSemicolonSetBeforeLineEnd(node, v.getOrNull(index + 1))) append(';')
        lineEnd()
        if (includeExtraBlankLines)
          (0 until if (index == v.size - 1) extraEndLines else extraMidLines).forEach { line() }
      }
    }

  protected fun stmtRequiresEmptyBraceSetBeforeLineEnd(v: Node?, next: Node?): Boolean {
    // As a special case, if this is a local memberless class decl stmt and the next line is a paren
    // or ann+paren, we have to explicitly provide an empty brace set
    // See: https://youtrack.jetbrains.com/issue/KT-25578
    // TODO: is there a better place to do this?
    if (v !is Node.Stmt.Decl || v.decl !is Node.Decl.Structured || v.decl.members.isNotEmpty() ||
      v.decl.form != Node.Decl.Structured.Form.CLASS) return false
    if (next !is Node.Stmt.Expr || (next.expr !is Node.Expr.Paren &&
        (next.expr !is Node.Expr.Annotated || next.expr.expr !is Node.Expr.Paren))) return false
    return true
  }

  protected fun stmtRequiresSemicolonSetBeforeLineEnd(v: Node?, next: Node?) =
    stmtHasModifierLocalVarDeclAmbiguity(v, next) || stmtHasTrailingLambdaAmbiguity(v, next)

  protected fun stmtHasModifierLocalVarDeclAmbiguity(v: Node?, next: Node?): Boolean {
    // As a special case, if there is just a name stmt, and it is a modifier, and the next stmt is
    // a decl, we need a semicolon
    // See: https://youtrack.jetbrains.com/issue/KT-25579
    // TODO: is there a better place to do this
    if (v !is Node.Stmt.Expr || v.expr !is Node.Expr.Name || next !is Node.Stmt.Decl) return false
    val name = v.expr.name.toUpperCase()
    return Node.Modifier.Keyword.values().any { it.name == name }
  }

  protected fun stmtHasTrailingLambdaAmbiguity(v: Node?, next: Node?): Boolean {
    // As a special case, if there is a function call stmt w/ no trailing lambda followed by a brace
    // stmt, the call needs a semicolon
    if (v !is Node.Stmt.Expr || v.expr !is Node.Expr.Call || v.expr.lambda != null) return false
    return next is Node.Stmt.Expr && next.expr is Node.Expr.Brace
  }

  protected fun Node.children(v: List<Node?>, sep: String = "", prefix: String = "", postfix: String = "") =
    this@Writer.also {
      append(prefix)
      v.forEachIndexed { index, t ->
        visit(t, this)
        if (index < v.size - 1) append(sep)
      }
      append(postfix)
    }

  // We accept lots of false positives to be simple and not have to bring in JVM dep to do accurate check
  protected val String.shouldEscapeIdent
    get() =
      KEYWORDS.contains(this) ||
        all { it == '_' } ||
        first() in '0'..'9' ||
        any { it !in 'a'..'z' && it !in 'A'..'Z' && it !in '0'..'9' && it != '_' }

  companion object {
    protected val KEYWORDS = setOf(
      "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface",
      "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias",
      "typeof", "val", "var", "when", "while"
    )

    fun write(v: Node, extrasMap: ExtrasMap? = null) =
      write(v, StringBuilder(), extrasMap).toString()

    fun <T : Appendable> write(v: Node, app: T, extrasMap: ExtrasMap? = null) =
      app.also { Writer(it, extrasMap).write(v) }
  }
}
