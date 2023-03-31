package arrow.optics.plugin

import org.junit.jupiter.api.Test

class DSLTests {

  @Test
  fun `DSL is generated for complex model with Every`() {
    """
      |$`package`
      |$imports
      |$dslModel
      |$dslValues
      |val modify = Employees.employees.every(Every.list()).company.notNull.address
      |  .street.name.modify(employees, String::toUpperCase)
      |val r = modify.employees.map { it.company?.address?.street?.name }.toString()
      """.evals("r" to "[LAMBDA STREET, LAMBDA STREET]")
  }

  @Test
  fun `DSL is generated for complex model with At`() {
    """
      |$`package`
      |$imports
      |$dslModel
      |$dslValues
      |val modify = Db.content.at(At.map(), One).set(db, None)
      |val r = modify.toString()
      """.evals("r" to "Db(content={Two=two, Three=three, Four=four})")
  }

  @Test
  fun `DSL works with extensions in the file, issue #2803`() {
    // it's important to keep the 'Source' name for the class,
    // because files in the test are named 'Source.kt'
    """
      |$`package`
      |$imports
      |
      |@optics
      |data class Source(val id: Int) {
      |  companion object
      |}
      |
      |fun Source.toSomeObject() = 5
      """.compilationSucceeds()
  }

  @Test
  fun `DSL for a data class with property named as a package directive`() {
    """
      |package main.program
      |
      |$imports
      |
      |@optics
      |data class Source(val program: String) {
      |  companion object
      |}
      |
      """.compilationSucceeds()
  }

  @Test
  fun `DSL for a class in a package including keywords, issue #2996`() {
    """
      |package id.co.app_name.features.main.transaction.internal.outgoing.data.OutgoingInternalTransaction
      |
      |$imports
      |
      |@optics
      |data class Source(val program: String) {
      |  companion object
      |}
      |
      """.compilationSucceeds()
  }

  // Db.content.at(At.map(), One).set(db, None)
}
