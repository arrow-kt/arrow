//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[fold](fold.md)

# fold

[common]\
inline fun &lt;[C](fold.md)&gt; [fold](fold.md)(fa: ([A](index.md)) -&gt; [C](fold.md), fb: ([B](index.md)) -&gt; [C](fold.md), fab: ([A](index.md), [B](index.md)) -&gt; [C](fold.md)): [C](fold.md)

Applies fa if this is a [Left](-left/index.md), fb if this is a [Right](-right/index.md) or fab if this is a [Both](-both/index.md)

Example:

&lt;!--- KNIT example-ior-07.kt --&gt;\
val result: Ior&lt;EmailContactInfo, PostalContactInfo&gt; = obtainContactInfo()\
result.fold(\
     { log("only have this email info: $it") },\
     { log("only have this postal info: $it") },\
     { email, postal -&gt; log("have this postal info: $postal and this email info: $email") }\
)<!--- KNIT example-ior-08.kt -->

#### Return

the results of applying the function

## Parameters

common

| | |
|---|---|
| fa | the function to apply if this is a [Left](-left/index.md) |
| fb | the function to apply if this is a [Right](-right/index.md) |
| fab | the function to apply if this is a [Both](-both/index.md) |
