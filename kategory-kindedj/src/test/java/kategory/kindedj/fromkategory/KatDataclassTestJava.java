package kategory.kindedj.fromkategory;

import org.junit.Assert;
import org.junit.Test;

import kategory.kindedj.Convert;
import kategory.kindedj.KatDataclass1;
import kategory.kindedj.KatDataclass2;
import kategory.kindedj.KatDataclass3;
import kategory.kindedj.KatDataclass4;
import kategory.kindedj.KatDataclass5;
import kategory.kindedj.KatDataclassHK;
import kategory.kindedj.KatDataclassKategoryShow;

public class KatDataclassTestJava {

    private final KatDataclass1<Integer> kinded = new KatDataclass1<>(0);

    private final KatDataclass2<Integer, String> kinded2 = new KatDataclass2<>(0);

    private final KatDataclass3<Integer, String, Boolean> kinded3 = new KatDataclass3<>(0);

    private final KatDataclass4<Integer, String, Boolean, Long> kinded4 = new KatDataclass4<>(0);

    private final KatDataclass5<Integer, String, Boolean, Long, Float> kinded5 = new KatDataclass5<>(0);

    @Test
    public void hk1CanBeConvertedToKategory() {
        final Convert.FromKategoryToKindedJ<KatDataclassHK, Integer> toKindedJ = Convert.toKindedJ(kinded);
        Assert.assertEquals(KatDataclassKategoryShow.INSTANCE.show(this.kinded), KatDataclassKindedJShow.INSTANCE.show(toKindedJ));
    }

    /*

    @Test
    public void hk2IsKindJ() {
        final io.kindedj.Hk<HK<KindedHK_K, ? extends Integer>, String> hkStringHK = ToKindedJ.toKindedJ(kinded2);
        final io.kindedj.Hk<io.kindedj.Hk<KindedHK_K, Integer>, String> hkStringHK1 = ToKindedJ.toKindedJ2(kinded2);
        Assert.assertEquals(kinded2, hkStringHK);
        Assert.assertEquals(hkStringHK, hkStringHK1);
    }

    @Test
    public void hk3IsKindJ() {
        final io.kindedj.Hk<HK<HK<KindedHK_K, ? extends Integer>, ? extends String>, Boolean> hkBooleanHK = ToKindedJ.toKindedJ(kinded3);
        final io.kindedj.Hk<io.kindedj.Hk<HK<KindedHK_K, ? extends Integer>, String>, Boolean> hkBooleanHK1 = ToKindedJ.toKindedJ2(kinded3);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<KindedHK_K, Integer>, String>, Boolean> hkBooleanHK2 = ToKindedJ.toKindedJ3(kinded3);
        Assert.assertEquals(kinded3, hkBooleanHK);
        Assert.assertEquals(hkBooleanHK, hkBooleanHK1);
        Assert.assertEquals(hkBooleanHK1, hkBooleanHK2);
    }

    @Test
    public void hk4IsKindJ() {
        final io.kindedj.Hk<HK<HK<HK<KindedHK_K, ? extends Integer>, ? extends String>, ? extends Boolean>, Long> hkLongHK = ToKindedJ.toKindedJ(kinded4);
        final io.kindedj.Hk<io.kindedj.Hk<HK<HK<KindedHK_K, ? extends Integer>, ? extends String>, Boolean>, Long> hkLongHK1 = ToKindedJ.toKindedJ2(kinded4);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<HK<KindedHK_K, ? extends Integer>, String>, Boolean>, Long> hkLongHK2 = ToKindedJ.toKindedJ3(kinded4);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<KindedHK_K, Integer>, String>, Boolean>, Long> hkLongHK3 = ToKindedJ.toKindedJ4(kinded4);
        Assert.assertEquals(kinded4, hkLongHK);
        Assert.assertEquals(hkLongHK, hkLongHK1);
        Assert.assertEquals(hkLongHK1, hkLongHK2);
        Assert.assertEquals(hkLongHK2, hkLongHK3);
    }

    @Test
    public void hk5IsKindJ() {
        final io.kindedj.Hk<HK<HK<HK<HK<KindedHK_K, ? extends Integer>, ? extends String>, ? extends Boolean>, ? extends Long>, Float> hkFloatHK = ToKindedJ.toKindedJ(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<HK<HK<HK<KindedHK_K, ? extends Integer>, ? extends String>, ? extends Boolean>, Long>, Float> hkFloatHK1 = ToKindedJ.toKindedJ2(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<HK<HK<KindedHK_K, ? extends Integer>, ? extends String>, Boolean>, Long>, Float> hkFloatHK2 = ToKindedJ.toKindedJ3(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<HK<KindedHK_K, ? extends Integer>, String>, Boolean>, Long>, Float> hkFloatHK3 = ToKindedJ.toKindedJ4(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<KindedHK_K, Integer>, String>, Boolean>, Long>, Float> hkFloatHK4 = ToKindedJ.toKindedJ5(kinded5);
        Assert.assertEquals(kinded5, hkFloatHK);
        Assert.assertEquals(hkFloatHK, hkFloatHK1);
        Assert.assertEquals(hkFloatHK1, hkFloatHK2);
        Assert.assertEquals(hkFloatHK2, hkFloatHK3);
        Assert.assertEquals(hkFloatHK3, hkFloatHK4);
    }
    */
}
