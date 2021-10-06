package ru.msm.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StartPage extends BasePage{

    //добавить выбор верного региона (другой город и т.д.)
    @FindBy(xpath = "//a[@class and contains(text(), 'Да')]")
    private WebElement confirmRegion;

    @FindBy(xpath = "//div[contains(@class, 'additional-sales-tabs__title') and contains(text(),'Гарантия')]")
    private WebElement additionalSalesTabs;

    @FindBy(xpath = "//span[@class='product-warranty__period' and contains(text(),'+ 24')]/..")
    private WebElement span;

    //проверить, если товар в наличии заранее? или поставить фильтр "в наличии"?

//    public ProductPage chooseProduct(String data_code) {
//        Optional<WebElement> product = products.stream().filter(el -> el.getAttribute("data-code").equals(data_code))
//                .findFirst();
//
//        Assertions.assertTrue(product.isPresent(), "Элемента с data-code = " + data_code +
//                " на данной странице не существует!");
//        product.get().click();
//
//        return PAGE_MANAGER.getProductPage();
//    }

}
