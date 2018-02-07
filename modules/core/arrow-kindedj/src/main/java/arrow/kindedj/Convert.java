package arrow.kindedj;

import org.jetbrains.annotations.NotNull;

import arrow.HK;

public class Convert {
    @NotNull
    public static <F, A> FromKindedJToArrow<F, A> fromKindedJ(@NotNull io.kindedj.Hk<F, A> hk) {
        return new FromKindedJToArrow<>(hk);
    }

    @NotNull
    public static <F, A> FromArrowToKindedJ<F, A> toKindedJ(@NotNull HK<F, A> hk) {
        return new FromArrowToKindedJ<>(hk);
    }

    public static class FromKindedJToArrow<F, A> implements HK<HK<ConvertHK, F>, A> {

        @NotNull
        private final io.kindedj.Hk<F, A> hk;

        FromKindedJToArrow(@NotNull io.kindedj.Hk<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public HK<HK<ConvertHK, F>, A> toArrow() {
            return this;
        }

        @NotNull
        public io.kindedj.Hk<F, A> toKindedJ() {
            return hk;
        }
    }

    public static class FromArrowToKindedJ<F, A> implements io.kindedj.Hk<io.kindedj.Hk<ConvertHK, F>, A> {

        @NotNull
        private final HK<F, A> hk;

        FromArrowToKindedJ(@NotNull HK<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public HK<F, A> toArrow() {
            return hk;
        }

        @NotNull
        public io.kindedj.Hk<io.kindedj.Hk<ConvertHK, F>, A> toKindedJ() {
            return this;
        }
    }
}
