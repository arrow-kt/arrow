package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import arrow.integrations.retrofit.adapter.retrofit.retrofit
import arrow.test.UnitSpec
import com.google.common.reflect.TypeToken
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import okhttp3.HttpUrl
import org.junit.runner.RunWith

private val NO_ANNOTATIONS = emptyArray<Annotation>()

private val retrofit = retrofit(HttpUrl.parse("http://localhost:1")!!)
private val factory = CallKindAdapterFactory.create()

@RunWith(KTestJUnitRunner::class)
class CallKindAdapterFactoryTest : UnitSpec() {
  init {
    "Non CallK Class should return null" {
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

    "Should work for CallK types" {
      factory.get(object : TypeToken<CallK<String>>() {}.type, NO_ANNOTATIONS, retrofit)!!
        .responseType() shouldBe String::class.java
    }
  }
}
