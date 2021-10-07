package ru.msm.framework.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.msm.framework.managers.DriverManager;

import java.util.List;

public class ProductPage extends BasePage {

    private Product product;

    @FindBy(xpath = "//h1[@data-product-title]")
    private WebElement name;

    @FindBy(xpath = "//div[contains(@class,'price')]//div[contains(@class,'price')]")
    private WebElement price;

    //button[@class='button-ui buy-btn button-ui_brand']
    @FindBy(xpath = "//div[@class='product-buy product-buy_one-line']//button[contains(@class,'button-ui buy-btn')]")
    private WebElement buttonBuy;

    //    @FindBy(xpath = "//span[contains(@class,'ui-radio__content')]")
//    private List<WebElement> typesOfWarranty;
//
//    @FindBy(xpath = "//span[contains(@class,'period')]")
//    private List<WebElement> addMonths;
//
//    @FindBy(xpath = "//span[contains(@class,'price')]")
//    private List<WebElement> addPrice;
//    @FindBy(xpath = "//input[@type='radio']")
//    private List<WebElement> checks;
//
//    @FindBy(xpath = "//div[contains(@class,'additional-sales-tabs__title') and contains(text(), 'арантия')]")
//    private WebElement w;

    //?..
    @FindBy(xpath = "//span[@class='cart-link__badge']")
    private WebElement orderNumProductsLabel;

    @FindBy(xpath = "//span[@class='cart-link__price']")
    private WebElement orderPriceLabel;

    WebDriver DRIVER = DriverManager.getINSTANCE().getDriver();

    public ProductPage() {

        String n = name.getText();
        int pr = formatD(price);

        List<WebElement> sales_tabs = DRIVER.findElements(By.xpath("//div[contains(@class,'additional-sales-tabs__title')]"));

        if (sales_tabs.stream().anyMatch(el -> el.getText().contains("арантия"))) {

//            Optional<WebElement> op = sales_tabs.stream().filter(el -> el.getText().contains("арантия")).findFirst();
//            Assertions.assertTrue(op.isPresent());
//            WebElement w = op.get();

            WebElement w = DRIVER.findElement(By.xpath("//div[contains(@class,'additional-sales-tabs__title') and contains(text(), 'арантия')]"));

            waitUntilElementToBeClickable(w).click();

            DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(w, "class",
                    "additional-sales-tabs__title additional-sales-tabs__title_active"));

            WebElement defType = DRIVER.findElement(By.xpath("//input[@value='default']/..//span[contains(@class,'content')]"));

            WebElement webM = DRIVER.findElement(By.xpath("//input[@value='default']/..//span[contains(@class,'period')]"));
            Integer m = formatD(webM);

            WebElement webP = DRIVER.findElement(By.xpath("//input[@value='default']/..//span[contains(@class,'price')]"));
            Integer p = formatD(webP);

            product = new Product(n, pr, defType.getText(), m, p);
        } else {
            product = new Product(n, pr);
        }
//        List<String> types = typesOfWarranty.stream()
//                .collect(
//                        ArrayList::new, // создаем ArrayList
//                        (list, item) -> list.add(item.getText()), // добавляем в список элемент
//                        ArrayList::addAll); // добавляем в список другой список (?)
//
//        System.out.println(types.size());
//
//        List<Integer> m = addMonths.stream()
//                .collect(
//                        ArrayList::new, // создаем ArrayList
//                        (list, item) -> list.add(Integer.parseInt(item.getText().replaceAll("\\D", ""))), // добавляем в список элемент
//                        ArrayList::addAll); // добавляем в список другой список
//        System.out.println(m.size());
//
//        List<Integer> p = addPrice.stream()
//                .collect(
//                        ArrayList::new, // создаем ArrayList
//                        (list, item) -> list.add(Integer.parseInt(item.getText().replaceAll("\\D", ""))), // добавляем в список элемент
//                        ArrayList::addAll); // добавляем в список другой список
//        //или сделать через Optional, как в методе chooseProduct (ResultPage) ?
//        System.out.println(p.size());
//
//        List<WebElement> g = checks.stream().filter(el -> el.getAttribute("checked").equals("true"))
//                .collect(Collectors.toList());
//        Assertions.assertTrue(g.size() != 0);
//        product = new Product(n, pr, types, m, p, checks.indexOf(g.get(0)));

    }

    public ProductPage changeWarranty(int m) {
        String xP = String.format("//span[contains(text(),'%d мес')]/..", m);
        WebElement spanTypeW = DRIVER.findElement(By.xpath(xP));
        waitUntilElementToBeClickable(spanTypeW).click();
        String type = spanTypeW.getText();

        //подходит только если доп гарантию выбираем
        DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(
                By.xpath("//div[contains(@class,'product-buy__sub')]"), "innerText", "цена изменена"));

        String xPp = String.format("//span[contains(text(),'%d мес')]/../..//span[contains(@class,'price')]", m);
        Integer p = formatD(DRIVER.findElement(By.xpath(xPp)));

        product.changeWarranty(type, m, p);

        return this;
    }

    public ProductPage buy() {
        waitUntilElementToBeClickable(buttonBuy).click();
        OrderPage.addProductToOrder(product);   //?..
        numProductsInOrder++;
        return this;
    }

    public ProductPage checkCurrentTotalPrice() {
        DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel,
                "outerText", String.valueOf(numProductsInOrder)));

        Assertions.assertEquals(
                OrderPage.getCurrentTotalPrice(),
                formatD(orderPriceLabel),
                "Цена корзины не равна сумме покупок!"
        );
        return this;
    }

}