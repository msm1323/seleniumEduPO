package ru.msm.framework.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.msm.framework.managers.DriverManager;

import java.util.*;

public class OrderPage extends BasePage {

    @FindBy(xpath = "//div[@class='total-amount__label']//span[@class='price__current']")
    private WebElement webTotalPrice;

    @FindBy(xpath = "//div[@class='total-amount__content']//span[@class='price__current']")
    private WebElement totalOrderPrice;

    public static int getCurrentTotalPrice() {
        return currentTotalPrice;
    }

    private static int currentTotalPrice;

    WebDriver DRIVER = DriverManager.getINSTANCE().getDriver();

    //теоретически там разные категории внутри корзины (эл ключи отдельно пихнул, к примеру), но на это сейчас забьем
    public static List<Product> products = new LinkedList<>();
    Deque<Product> removedProducts = new ArrayDeque<>();

    private Product getProductByName(String name) {
        for (Product p : products) {
            if (p.getName().contains(name)) {   //частичное совпадение по имени выглядит сомнительно
                return p;
            }
        }
        return null;
    }

    public static void addProductToOrder(Product product) {
        products.add(product);
        currentTotalPrice = currentTotalPrice + product.getPrice();
        if (product.isWarrantyExist()) {
            currentTotalPrice = currentTotalPrice + product.getWarrantyPrice();
        }
    }

    public OrderPage() {
        currentTotalPrice = 0;
        for (Product p: products){
            currentTotalPrice = currentTotalPrice + p.getPrice();
            if (p.isWarrantyExist()) {
                currentTotalPrice = currentTotalPrice + p.getWarrantyPrice();
            }
        }
    }

    public OrderPage checkWarrantyProductByNameM(String name, int m) {
        String xP = String.format("//a[contains(text(),'%s')]/../../../../../.." +
                "//span[contains(@class,'radio-button__icon_checked')]", name);
        WebElement check = DRIVER.findElement(By.xpath(xP));
        Assertions.assertNotNull(getProductByName(name), "В products нет товара " + name + "!");
        Assertions.assertTrue(check.getText().contains(String.valueOf(m)), "Не совпали");
        return this;
    }

    public OrderPage checkOrderPrices() {
        checkProductPrices();
        checkTotalOrderPrice();
        return this;
    }

    public OrderPage checkProductPrices() {
        for (Product p : products) {
            String xP = String.format("//a[contains(text(),'%s')]/../../../../..//span[@class='price__current']", p.getName());
            WebElement checkPrice = DRIVER.findElement(By.xpath(xP));
            //у них при изменении вида гарантии внутри корзины не меняется цена в этом вебэлементе - только в общей сумме корзины
            Assertions.assertEquals(p.getPrice(), formatD(checkPrice), "Цены не совпали!" +
                    "\nЦена товара в products = " + p.getPrice() +
                    "\nЦена товара в корзине = " + formatD(checkPrice));
        }
        return this;
    }

    public OrderPage checkTotalOrderPrice() {
        Assertions.assertEquals(
                currentTotalPrice,
                formatD(totalOrderPrice),
                "Цена корзины не равна сумме покупок!"
        );
        return this;
    }

    public OrderPage removeProductByName(String name) {
        String xP = String.format("//a[contains(text(),'%s')]/../..//button[contains(text(),'далить')]", name);
        WebElement removeProduct = DRIVER.findElement(By.xpath(xP));

        xP = String.format("//a[contains(text(),'%s')]/../../../../..//input[@class='count-buttons__input']", name);
        WebElement numOfProduct = DRIVER.findElement(By.xpath(xP));

        newNum = newNum - Integer.parseInt(numOfProduct.getAttribute("_value"));
        waitUntilElementToBeClickable(removeProduct).click();   //иногда промахивается вниз на ключ активации
        Iterator<Product> it = products.iterator();
        while (it.hasNext()) {
            Product p = it.next();
            if (p.getName().contains(name)) {
                removedProducts.push(p);
                currentTotalPrice = currentTotalPrice - p.getPrice();
                it.remove();
                break;
            }
        }
        DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel, //вынести в метод?
                "outerText", String.valueOf(newNum)));
        return this;
    }

    public OrderPage checkProductRemovingByName(String name) {
        //возможны проблемы с поиском - со скроллом? нужна проверка на большем списке эл-тов в корзине
        List<WebElement> productsCarts = DRIVER.findElements(By.xpath("//a[@class='cart-items__product-name-link']"));
        for (WebElement el : productsCarts) {       //перебор элементов мммда
            Assertions.assertFalse(el.getText().contains(name), "Товар " + name + " не удален из корзины!");
        }

        Assertions.assertEquals(currentTotalPrice, formatD(totalOrderPrice),
                "Итоговая стоимость корзины не изменилась!");

        return this;
    }

    public OrderPage increaseExistingProductNumByName(String name, int plusNum) {
        String xP = String.format("//a[contains(text(),'%s')]/../../../../..//i[@class='count-buttons__icon-plus']", name);
        WebElement p = DRIVER.findElement(By.xpath(xP));
        for (int i = 0; i < plusNum; i++) {
            newNum++;
            waitUntilElementToBeClickable(p).click();
            DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel,
                    "outerText", String.valueOf(newNum)));
            addProductToOrder(getProductByName(name));
        }
        return this;
    }

    public OrderPage returnBackRemovedProduct() {
        try {   //?..
            newNum++;

            String xP = "//div[@class='group-tabs__tab']//span[@class='restore-last-removed']";
            WebElement aReturnBack = DRIVER.findElement(By.xpath(xP));

            scrollWithOffset(aReturnBack, 0, -150);

            waitUntilElementToBeClickable(aReturnBack).click();
            addProductToOrder(removedProducts.pop());

            DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel,
                    "outerText", String.valueOf(newNum)));

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Assertions.fail("Невозможно вернуть удаленный товар! Ссылка \"Вернуть удаленный товар\" не найдена.");
        }
        return this;
    }

    //переписать все эти методы *ByName как с локаторами by
    public OrderPage checkProductReturnBackByName(String name) {
        List<WebElement> productsCarts = DRIVER.findElements(By.xpath("//a[@class='cart-items__product-name-link']"));
        for (WebElement el : productsCarts) {       //перебор элементов - найти другой способ
            if (el.getText().contains(name)) {
                Assertions.assertEquals(currentTotalPrice, formatD(totalOrderPrice),
                        "Итоговая стоимость корзины не изменилась!");
                return this;
            }
        }
        Assertions.fail("Товар " + name + " не возвращен в корзину!");
        return this;
    }

}
