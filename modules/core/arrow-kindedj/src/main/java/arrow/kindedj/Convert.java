package arrow.kindedj;

import org.jetbrains.annotations.NotNull;

import arrow.Kind;

public class Convert {
    @NotNull
    public static <F, A> FromKindedJToArrow<F, A> fromKindedJ(@NotNull io.kindedj.Hk<F, A> hk) {
        return new FromKindedJToArrow<>(hk);
    }

    @NotNull
    public static <F, A> FromArrowToKindedJ<F, A> toKindedJ(@NotNull Kind<F, A> hk) {
        return new FromArrowToKindedJ<>(hk);
    }

    public static class FromKindedJToArrow<F, A> implements Kind<Kind<ForConvert, F>, A> {

        @NotNull
        private final io.kindedj.Hk<F, A> hk;

        FromKindedJToArrow(@NotNull io.kindedj.Hk<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public Kind<Kind<ForConvert, F>, A> toArrow() {
            return this;
        }

        @NotNull
        public io.kindedj.Hk<F, A> toKindedJ() {
            return hk;
        }
    }

    public static class FromArrowToKindedJ<F, A> implements io.kindedj.Hk<io.kindedj.Hk<ForConvert, F>, A> {

        @NotNull
        private final Kind<F, A> hk;

        FromArrowToKindedJ(@NotNull Kind<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public Kind<F, A> toArrow() {
            return hk;
        }

        @NotNull
        public io.kindedj.Hk<io.kindedj.Hk<ForConvert, F>, A> toKindedJ() {
            return this;
        }
    }
}
