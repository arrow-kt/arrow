package arrow.extreme

import arrow.Kind
import arrow.extension

@extension
object OptionMappable: Mappable<ForOption> {
  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> =
    when (val o: Option<A> = this.fix()) {
      is Some -> Some(f(o.a))
      None -> None
    }
}
