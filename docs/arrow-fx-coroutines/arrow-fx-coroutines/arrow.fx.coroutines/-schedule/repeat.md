//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[repeat](repeat.md)

# repeat

[common]\
suspend fun [repeat](repeat.md)(fa: suspend () -&gt; [Input](index.md)): [Output](index.md)

Runs this effect once and, if it succeeded, decide using the provided policy if the effect should be repeated and if so, with how much delay. Returns the last output from the policy or raises an error if a repeat failed.
