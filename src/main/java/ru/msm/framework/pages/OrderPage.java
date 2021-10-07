package ru.msm.framework.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

    public OrderPage() {
        currentTotalPrice = 0;
        for (Product p : products) {
            currentTotalPrice = currentTotalPrice + p.getPrice();
            if (p.isWarrantyExist()) {
                currentTotalPrice = currentTotalPrice + p.getWarrantyPrice();
            }
        }
    }

    private Product getProductByName(String name) {
        for (Product p : products) {
            if (p.getName().contains(name)) {   //частичное совпадение по имени выглядит сомнительно
                return p;
            }
        }
        Assertions.fail("Товар с именем " + name + " не найден!");
        return null;
    }

    public static void addProductToOrder(Product product) {
        products.add(product);
        currentTotalPrice = currentTotalPrice + product.getPrice();
        if (product.isWarrantyExist()) {
            currentTotalPrice = currentTotalPrice + product.getWarrantyPrice();
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

//        xP = String.format("//a[contains(text(),'%s')]/../../../../..//input[@class='count-buttons__input']", name);
//        WebElement numOfProduct = DRIVER.findElement(By.xpath(xP));

//        numProductsInOrder = formatD(orderNumProductsLabel) - Integer.parseInt(numOfProduct.getAttribute("_value"));
        numProductsInOrder = formatD(orderNumProductsLabel) - getProductByName(name).getNum();

//        waitUtilElementToBeVisible(DRIVER.findElement(By.xpath("//a[contains(text(),'Detroit')]/../../../../../../..//div[contains(@class,'accessories__items')]")));
//        action.moveToElement(removeProduct).click();
        waitUntilElementToBeClickable(removeProduct).click();   //иногда промахивается вниз на ключ активации
        Iterator<Product> it = products.iterator();
        while (it.hasNext()) {
            Product p = it.next();
            if (p.getName().contains(name)) {
                removedProducts.push(p);
                currentTotalPrice = currentTotalPrice - p.getNum() * p.getPrice();
                if (p.isWarrantyExist()) {
                    currentTotalPrice = currentTotalPrice - p.getNum() * p.getWarrantyPrice();
                }
                it.remove();
                break;
            }
        }
        DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel, //вынести в метод?
                "outerText", String.valueOf(numProductsInOrder)));
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

    public OrderPage changeExistingProductNumByName(String name, boolean increase, int n) {
        String a;
        if (increase) {
            a = "plus";
        } else {
            a = "minus";
        }
        String xP = String.format("//a[contains(text(),'%s')]/../../../../..//i[@class='count-buttons__icon-%s']", name, a);
        WebElement apply = DRIVER.findElement(By.xpath(xP));
        if (increase) {
            getProductByName(name).changeNum(getProductByName(name).getNum() + n);
            numProductsInOrder = numProductsInOrder + n;
        } else {
            getProductByName(name).changeNum(getProductByName(name).getNum() - n);
            numProductsInOrder = numProductsInOrder - n;
        }
        waitUntilElementToBeClickable(apply).click();
        DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel,
                "outerText", String.valueOf(numProductsInOrder)));
        return this;
    }

    public OrderPage returnBackRemovedProduct() {
        try {   //?..
            numProductsInOrder = numProductsInOrder + removedProducts.getFirst().getNum();

            String xP = "//div[@class='group-tabs__tab']//span[@class='restore-last-removed']";
            WebElement aReturnBack = DRIVER.findElement(By.xpath(xP));

            scrollWithOffset(aReturnBack, 0, -150);

            waitUntilElementToBeClickable(aReturnBack).click();
            addProductToOrder(removedProducts.pop());

            DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.attributeContains(orderNumProductsLabel,
                    "outerText", String.valueOf(numProductsInOrder)));

        } catch (NoSuchElementException ex) {
            System.out.println(ex.getMessage());
            Assertions.fail("Невозможно вернуть удаленный товар! Ссылка \"Вернуть удаленный товар\" не найдена.");
        }
        return this;
    }

    //переписать все эти методы *ByName как с локаторами by
    public OrderPage checkProductReturnBackByName(String name) {
        List<WebElement> productsCarts = DRIVER.findElements(By.xpath("//a[@class='cart-items__product-name-link']"));
        Optional<WebElement> op = productsCarts.stream().filter(el -> el.getText().contains(name)).findFirst();
        if (op.isEmpty()) {
            Assertions.fail("Товар " + name + " не возвращен в корзину!/");
        } else {
            Assertions.assertEquals(currentTotalPrice, formatD(totalOrderPrice),
                    "Итоговая стоимость корзины не изменилась!");
        }
        return this;
    }

}
