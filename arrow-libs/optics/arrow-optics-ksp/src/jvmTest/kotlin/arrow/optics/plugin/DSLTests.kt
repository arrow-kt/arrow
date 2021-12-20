package arrow.optics.plugin

import org.junit.jupiter.api.Test

class DSLTests {

  @Test
  fun `DSL is generated for complex model with Every`() {
    """
      |$imports
      |$dslModel
      |$dslValues
      |val modify = Employees.employees.every(Every.list()).company.address
      |  .street.name.modify(employees, String::toUpperCase)
      |val r = modify.employees.map { it.company?.address?.street?.name }.toString()
      """ {
      "r".source.evalsTo("[LAMBDA STREET, LAMBDA STREET]")
    }
  }

  @Test
  fun `DSL is generated for complex model with At`() {
    """
      |$imports
      |$dslModel
      |$dslValues
      |val modify = Db.content.at(At.map(), One).set(db, None)
      |val r = modify.toString()
      """ {
      "r".source.evalsTo("Db(content={Two=two, Three=three, Four=four})")
    }
  }

  // Db.content.at(At.map(), One).set(db, None)
}
