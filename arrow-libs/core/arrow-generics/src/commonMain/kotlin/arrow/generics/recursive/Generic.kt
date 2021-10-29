package arrow.generics.recursive

// these are the "interpretations" of generic representations
public typealias Generic<F, A> = Sum<F, F, A>

public sealed interface Interpr<out F : TopRepr, out G : Repr, out A>
// a sum is one choice or the other
public sealed interface Sum<out F : TopRepr, out G : SumRepr, out A> : Interpr<F, G, A>
// and when we take a choice we also encode its name
// (think of it as having any further metadata)
public data class This<out F : TopRepr, out G : ProductRepr, out Rest : SumRepr, out A>
  (val choice: String, val value: Product<F, G, A>) :
  Sum<F, TyS<G, Rest>, A>
public data class That<out F : TopRepr, out G : ProductRepr, out Rest : SumRepr, out A>
  (val next: Sum<F, Rest, A>) :
  Sum<F, TyS<G, Rest>, A>
// and a product is a heterogenerous list
public sealed interface Product<out F : TopRepr, out G : ProductRepr, out A> : Interpr<F, G, A>
public data class And<out F : TopRepr, out G : ValueRepr, out Rest : ProductRepr, out A>
  (val name: String, val value: Value<F, G, A>, val rest: Product<F, Rest, A>) :
  Product<F, TyP<G, Rest>, A>
public data class EndD<out F : TopRepr, out A>(val e: Int) : Product<F, EndP, A>
// which may have deep things
public sealed interface Value<out F : TopRepr, out G : ValueRepr, out A> : Interpr<F, G, A>
public data class Field<out F : TopRepr, out T, out A>(val value: T) : Value<F, FieldR<T>, A>
public data class Par<out F : TopRepr, out A>(val value: A) : Value<F, ParR, A>
public data class Rec<out F : TopRepr, out A>(val value: Generic<F, A>) : Value<F, RecR, A>
