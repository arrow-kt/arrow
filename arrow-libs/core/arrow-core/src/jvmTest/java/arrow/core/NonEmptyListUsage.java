package arrow.core;

import java.util.Arrays;
import java.util.List;

public class NonEmptyListUsage {

    public void testUsage() {
        // from the Java side, we just have List
        List<Integer> integers = NonEmptyList.of(1, 2, 3, 4, 5);
        int i = IterableKt.compareTo(
                NonEmptyList.of(1, 2, 3, 4, 5),
                NonEmptyList.of(1, 2, 3, 4, 5)
        );
        // List<Integer> flatten = flatten(nonEmptyListOf(
        //         nonEmptyListOf(1, 2),
        //         nonEmptyListOf(3, 4)
        // ));
        Option<NonEmptyList<Integer>> nonEmptyListOption = NonEmptyList.fromList(Arrays.asList(1, 2, 3));
        List<Integer> integers2 = NonEmptyList.ofOrNull(Arrays.asList(1, 2, 3));
    }
}
