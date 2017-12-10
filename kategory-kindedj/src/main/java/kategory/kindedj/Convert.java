package kategory.kindedj;

import org.jetbrains.annotations.NotNull;

import kategory.HK;

public class Convert {
    @NotNull
    public static <F, A> FromKindedJToKategory<F, A> fromKindedJ(@NotNull io.kindedj.Hk<F, A> hk) {
        return new FromKindedJToKategory<>(hk);
    }

    @NotNull
    public static <F, A> FromKategoryToKindedJ<F, A> toKindedJ(@NotNull HK<F, A> hk) {
        return new FromKategoryToKindedJ<>(hk);
    }

    public static class FromKindedJToKategory<F, A> implements HK<HK<ConvertHK, F>, A> {

        @NotNull
        private final io.kindedj.Hk<F, A> hk;

        FromKindedJToKategory(@NotNull io.kindedj.Hk<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public HK<HK<ConvertHK, F>, A> toKategory() {
            return this;
        }

        @NotNull
        public io.kindedj.Hk<F, A> toKindedJ() {
            return hk;
        }
    }

    public static class FromKategoryToKindedJ<F, A> implements io.kindedj.Hk<io.kindedj.Hk<ConvertHK, F>, A> {

        @NotNull
        private final HK<F, A> hk;

        FromKategoryToKindedJ(@NotNull HK<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public HK<F, A> toKategory() {
            return hk;
        }

        @NotNull
        public io.kindedj.Hk<io.kindedj.Hk<ConvertHK, F>, A> toKindedJ() {
            return this;
        }
    }
}