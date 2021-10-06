package ru.msm.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ResultPage extends BasePage{

    //не может до них доскролить ? => не видит их, не инициализирует => страница null
//    @FindBy(xpath = "//a[@href and contains(@class,'catalog-product__name')]")
//    private List<WebElement> products;

    //?..
    @FindBy(xpath = "//span[contains(text(),'LG F1296NDS1')]")
    private WebElement washer;


    public ProductPage chooseProduct(String name) {
        if (name.equals("LG F1296NDS1")) {   //?..
            washer.click();
        }
        return PAGE_MANAGER.getProductPage();
    }
}