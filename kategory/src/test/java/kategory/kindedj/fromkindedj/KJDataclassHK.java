package kategory.kindedj.fromkindedj;

import io.kindedj.HK;

public class KJDataclassHK {
    private KJDataclassHK() {
    }

    public static <A> String show(HK<KJDataclassHK, A> hk) {
        return ((KJDataclass1<A>) hk).a.toString();
    }

    public static final class KJDataclass1<A> implements HK<KJDataclassHK, A> {
        public final A a;

        public KJDataclass1(A a) {
            this.a = a;
        }
    }

    public static final class KJDataclass2<A, B> implements HK<HK<KJDataclassHK, A>, B> {
        public final A a;

        public KJDataclass2(A a) {
            this.a = a;
        }
    }

    public static final class KJDataclass3<A, B, C> implements HK<HK<HK<KJDataclassHK, A>, B>, C> {
        public final A a;

        public KJDataclass3(A a) {
            this.a = a;
        }
    }

    public static final class KJDataclass4<A, B, C, D> implements HK<HK<HK<HK<KJDataclassHK, A>, B>, C>, D> {
        public final A a;

        public KJDataclass4(A a) {
            this.a = a;
        }
    }

    public static final class KJDataclass5<A, B, C, D, E> implements HK<HK<HK<HK<HK<KJDataclassHK, A>, B>, C>, D>, E> {
        public final A a;

        public KJDataclass5(A a) {
            this.a = a;
        }
    }
}
