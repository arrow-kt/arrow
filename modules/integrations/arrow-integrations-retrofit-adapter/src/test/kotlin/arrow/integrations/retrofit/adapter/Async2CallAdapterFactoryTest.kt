package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import arrow.test.UnitSpec
import com.google.common.reflect.TypeToken
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val NO_ANNOTATIONS = emptyArray<Annotation>()

@RunWith(KTestJUnitRunner::class)
class Async2CallAdapterFactoryTest : UnitSpec() {
  private val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost:1")
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(Async2CallAdapterFactory.create())
    .build()

  private val factory = Async2CallAdapterFactory.create()

  init {
    "Non Async Class should return null" {
      factory.get(object : TypeToken<List<String>>() {}.type, NO_ANNOTATIONS, retrofit) shouldBe null
    }

    "Non parametrized type should throw exception" {
      val exceptionList = shouldThrow<IllegalArgumentException> {
        factory.get(List::class.java, NO_ANNOTATIONS, retrofit)
      }
      exceptionList.message shouldBe "Return type must be parameterized as List<Foo> or List<out Foo>"

      val exceptionIO = shouldThrow<IllegalArgumentException> {
        factory.get(IO::class.java, NO_ANNOTATIONS, retrofit)
      }
      exceptionIO.message shouldBe "Return type must be parameterized as IO<Foo> or IO<out Foo>"
    }

    "Should work for all types" {
      factory.get(object : TypeToken<IO<String>>() {}.type, NO_ANNOTATIONS, retrofit)!!
        .responseType() shouldBe String::class.java
      factory.get(object : TypeToken<CallK<String>>() {}.type, NO_ANNOTATIONS, retrofit)!!
        .responseType() shouldBe String::class.java
    }
  }
}