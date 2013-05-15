package org.funktionale.utils

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/05/13
 * Time: 13:11
 */
public class GetterSetterOperation<K, V>(override val getter: (K) -> V, override val setter: (K, V) -> Unit):
GetterOperation<K, V>,
SetterOperation<K, V>