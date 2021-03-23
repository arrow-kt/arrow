package arrow.core;

import java.util.Arrays;

import static arrow.core.NonEmptyListKt.*;

public class NonEmptyListUsage {

    public void testUsage() {
        NonEmptyList<Integer> integers = nonEmptyListOf(1, 2, 3, 4, 5);
        int i = compareTo(
                nonEmptyListOf(1, 2, 3, 4, 5),
                nonEmptyListOf(1, 2, 3, 4, 5)
        );
        NonEmptyList<Integer> flatten = flatten(nonEmptyListOf(
                nonEmptyListOf(1, 2),
                nonEmptyListOf(3, 4)
        ));
        Option<NonEmptyList<Integer>> nonEmptyListOption = NonEmptyList.fromList(Arrays.asList(1, 2, 3));
        NonEmptyList<Integer> integers1 = NonEmptyList.fromListUnsafe(Arrays.asList(1, 2, 3));
    }
}
