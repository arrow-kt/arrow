@file:Suppress("UNCHECKED_CAST")

package arrow.generic.tup

operator fun <A> Tup1Plus<A>.component1(): A = data[0] as A

operator fun <A> Tup2Plus<*, A>.component2(): A = data[1] as A

operator fun <A> Tup3Plus<*, *, A>.component3(): A = data[2] as A

operator fun <A> Tup4Plus<*, *, *, A>.component4(): A = data[3] as A

operator fun <A> Tup5Plus<*, *, *, *, A>.component5(): A = data[4] as A

operator fun <A> Tup6Plus<*, *, *, *, *, A>.component6(): A = data[5] as A

operator fun <A> Tup7Plus<*, *, *, *, *, *, A>.component7(): A = data[6] as A

operator fun <A> Tup8Plus<*, *, *, *, *, *, *, A>.component8(): A = data[7] as A

operator fun <A> Tup9Plus<*, *, *, *, *, *, *, *, A>.component9(): A = data[8] as A

operator fun <A> Tup10Plus<*, *, *, *, *, *, *, *, *, A>.component10(): A = data[9] as A

operator fun <A> Tup11Plus<*, *, *, *, *, *, *, *, *, *, A>.component11(): A = data[10] as A

operator fun <A> Tup12Plus<*, *, *, *, *, *, *, *, *, *, *, A>.component12(): A = data[11] as A

operator fun <A> Tup13Plus<*, *, *, *, *, *, *, *, *, *, *, *, A>.component13(): A = data[12] as A

operator fun <A> Tup14Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, A>.component14(): A =
  data[13] as A

operator fun <A> Tup15Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component15(): A =
  data[14] as A

operator fun <A> Tup16Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component16(): A =
  data[15] as A

operator fun <A> Tup17Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component17(): A =
  data[16] as A

operator fun <A> Tup18Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component18(): A =
  data[17] as A

operator fun <A>
  Tup19Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component19(): A =
  data[18] as A

operator fun <A>
  Tup20Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component20(): A =
  data[19] as A

operator fun <A>
  Tup21Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component21(): A =
  data[20] as A

operator fun <A>
  Tup22Plus<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, A>.component22(): A =
  data[21] as A
