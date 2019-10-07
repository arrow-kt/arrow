package kastree.ast

interface ExtrasMap {
  fun extrasBefore(v: Node): List<Node.Extra>
  fun extrasWithin(v: Node): List<Node.Extra>
  fun extrasAfter(v: Node): List<Node.Extra>

  fun docComment(v: Node): Node.Extra.Comment? {
    for (extra in extrasBefore(v)) if (extra is Node.Extra.Comment && extra.text.startsWith("/**")) return extra
    return null
  }
}
