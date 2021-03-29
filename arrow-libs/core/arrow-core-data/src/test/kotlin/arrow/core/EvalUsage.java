package arrow.core;

public class EvalUsage {

    public void testUsage() {
        Eval<Integer> now = Eval.now(1);
        Eval.Later<Integer> later = Eval.later(() -> 1);
        Eval raise = Eval.raise(new RuntimeException());
        Eval.Always<Integer> always = Eval.always(() -> 1);
        Eval.defer(() -> Eval.now(1));
    }
}
