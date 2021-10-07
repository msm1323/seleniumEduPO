package ru.msm.framework.tests;

import org.junit.jupiter.api.Test;
import ru.msm.framework.BaseTestClass;

public class FirstTest extends BaseTestClass {

//    @RepeatedTest(5)
    @Test
    public void dnsTest() {

        String washer = "LG F1296NDS1";
        String game = "Detroit";

        PAGE_MANAGER.getStartPage()
                .search("стиральная машина")
                .chooseProduct(washer)
                .changeWarranty(24)
                .buy()
                .search("игра " + game)
                .chooseProduct(game)
                .buy()
                .checkCurrentTotalPrice()
                .moveToOrderPage()
                .checkWarrantyProductByNameM(washer, 24)
                .checkOrderPrices()
                .removeProductByName(game)
                .checkProductRemovingByName(game)
                .changeExistingProductNumByName(washer, true, 2)
                .checkTotalOrderPrice()
                .returnBackRemovedProduct()
                .checkProductReturnBackByName(game);
    }

}
