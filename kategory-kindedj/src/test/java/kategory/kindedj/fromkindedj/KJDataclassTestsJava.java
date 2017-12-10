package kategory.kindedj.fromkindedj;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import kategory.kindedj.Convert;
import kategory.kindedj.KJDataclassKategoryShow;
import kategory.kindedj.fromkindedj.KJDataclassHK.*;

@RunWith(JUnit4.class)
public class KJDataclassTestsJava {

    private final KJDataclass1<Integer> kinded = new KJDataclass1<>(0);

    private final KJDataclass2<Integer, String> kinded2 = new KJDataclass2<>(0);

    private final KJDataclass3<Integer, String, Boolean> kinded3 = new KJDataclass3<>(0);

    private final KJDataclass4<Integer, String, Boolean, Long> kinded4 = new KJDataclass4<>(0);

    private final KJDataclass5<Integer, String, Boolean, Long, Float> kinded5 = new KJDataclass5<>(0);

    @Test
    public void hk1CanBeConvertedToKategory() {
        final Convert.FromKindedJToKategory<KJDataclassHK, Integer> hkKindedJ = Convert.fromKindedJ(kinded);
        Assert.assertEquals(KJDataclassKindedJShow.INSTANCE.show(kinded), KJDataclassKategoryShow.INSTANCE.show(hkKindedJ));
    }
    /*

    @Test
    public void hk2IsKindJ() {
        final io.kindedj.Hk<HK<KindedHK_J, ? extends Integer>, String> hkStringHK = FromKindedJ.fromKindedJ(kinded2);
        final io.kindedj.Hk<io.kindedj.Hk<KindedHK_J, Integer>, String> hkStringHK1 = ToKindedJ.toKindedJ2(kinded2);
        Assert.assertEquals(kinded2, hkStringHK);
        Assert.assertEquals(hkStringHK, hkStringHK1);
    }

    @Test
    public void hk3IsKindJ() {
        final io.kindedj.Hk<HK<HK<KindedHK_J, ? extends Integer>, ? extends String>, Boolean> hkBooleanHK = ToKindedJ.toKindedJ(kinded3);
        final io.kindedj.Hk<io.kindedj.Hk<HK<KindedHK_J, ? extends Integer>, String>, Boolean> hkBooleanHK1 = ToKindedJ.toKindedJ2(kinded3);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<KindedHK_J, Integer>, String>, Boolean> hkBooleanHK2 = ToKindedJ.toKindedJ3(kinded3);
        Assert.assertEquals(kinded3, hkBooleanHK);
        Assert.assertEquals(hkBooleanHK, hkBooleanHK1);
        Assert.assertEquals(hkBooleanHK1, hkBooleanHK2);
    }

    @Test
    public void hk4IsKindJ() {
        final io.kindedj.Hk<HK<HK<HK<KindedHK_J, ? extends Integer>, ? extends String>, ? extends Boolean>, Long> hkLongHK = ToKindedJ.toKindedJ(kinded4);
        final io.kindedj.Hk<io.kindedj.Hk<HK<HK<KindedHK_J, ? extends Integer>, ? extends String>, Boolean>, Long> hkLongHK1 = ToKindedJ.toKindedJ2(kinded4);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<HK<KindedHK_J, ? extends Integer>, String>, Boolean>, Long> hkLongHK2 = ToKindedJ.toKindedJ3(kinded4);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<KindedHK_J, Integer>, String>, Boolean>, Long> hkLongHK3 = ToKindedJ.toKindedJ4(kinded4);
        Assert.assertEquals(kinded4, hkLongHK);
        Assert.assertEquals(hkLongHK, hkLongHK1);
        Assert.assertEquals(hkLongHK1, hkLongHK2);
        Assert.assertEquals(hkLongHK2, hkLongHK3);
    }

    @Test
    public void hk5IsKindJ() {
        final io.kindedj.Hk<HK<HK<HK<HK<KindedHK_J, ? extends Integer>, ? extends String>, ? extends Boolean>, ? extends Long>, Float> hkFloatHK = ToKindedJ.toKindedJ(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<HK<HK<HK<KindedHK_J, ? extends Integer>, ? extends String>, ? extends Boolean>, Long>, Float> hkFloatHK1 = ToKindedJ.toKindedJ2(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<HK<HK<KindedHK_J, ? extends Integer>, ? extends String>, Boolean>, Long>, Float> hkFloatHK2 = ToKindedJ.toKindedJ3(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<HK<KindedHK_J, ? extends Integer>, String>, Boolean>, Long>, Float> hkFloatHK3 = ToKindedJ.toKindedJ4(kinded5);
        final io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<io.kindedj.Hk<KindedHK_J, Integer>, String>, Boolean>, Long>, Float> hkFloatHK4 = ToKindedJ.toKindedJ5(kinded5);
        Assert.assertEquals(kinded5, hkFloatHK);
        Assert.assertEquals(hkFloatHK, hkFloatHK1);
        Assert.assertEquals(hkFloatHK1, hkFloatHK2);
        Assert.assertEquals(hkFloatHK2, hkFloatHK3);
        Assert.assertEquals(hkFloatHK3, hkFloatHK4);
    }
    */

}
