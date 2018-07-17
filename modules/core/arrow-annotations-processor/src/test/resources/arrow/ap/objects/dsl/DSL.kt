package arrow.ap.objects.dsl



/**
 * DSL to compose an [Iso] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Iso] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Optional] with a focus in A
 */
inline val <A, B> arrow.optics.Iso<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Optional<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Lens] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Lens] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Optional] with a focus in A
 */
inline val <A, B> arrow.optics.Lens<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Optional<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Prism] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Lens] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Optional] with a focus in A
 */
inline val <A, B> arrow.optics.Prism<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Optional<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Optional] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Lens] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Optional] with a focus in A
 */
inline val <A, B> arrow.optics.Optional<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Optional<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Getter] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Getter] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Fold] with a focus in A
 */
inline val <A, B> arrow.optics.Getter<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Fold<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Setter] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Lens] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Setter] with a focus in A
 */
inline val <A, B> arrow.optics.Setter<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Setter<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Traversal] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Lens] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Traversal] with a focus in A
 */
inline val <A, B> arrow.optics.Traversal<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Traversal<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()

/**
 * DSL to compose a [Fold] with focus of kotlin.collections.List<A> with a [arrow.optics.Optional] with focus A
 *
 * @receiver [Lens] with a focus kotlin.collections.List<A>
 * @return [arrow.optics.Fold] with a focus in A
 */
inline val <A, B> arrow.optics.Fold<B, `kotlin`.`collections`.`List`<`A`>>.first: arrow.optics.Fold<B, `A`> inline get() = this compose arrow.ap.objects.dsl.first()
