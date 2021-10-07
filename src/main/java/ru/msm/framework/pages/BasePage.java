package ru.msm.framework.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.msm.framework.managers.*;

abstract public class BasePage {

    protected final DriverManager DRIVER_MANAGER = DriverManager.getINSTANCE();

    protected final PageManager PAGE_MANAGER = PageManager.getINSTANCE();

    private final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getINSTANCE();

    @FindBy(xpath = "//a[@class='ui-link cart-link']")
    private WebElement order;

    @FindBy(xpath = "//input[@type='search' and not(@id)]")
    private WebElement searchLine;

    //костыль
    @FindBy(xpath = "//span[@class='cart-link__badge']")
    protected WebElement orderNumProductsLabel;

    public static int numProductsInOrder = 0;   //берется из лейбла с верхней панели (преимущественно)

    public ResultPage search(String input) {
        waitUntilElementToBeClickable(searchLine);
        searchLine.click();
        searchLine.sendKeys(input);
        searchLine.submit();
        return PAGE_MANAGER.getResultPage();
    }

    public OrderPage moveToOrderPage() {
        waitUntilElementToBeClickable(order).click();
        return PAGE_MANAGER.getOrderPage();
    }

    //переименовать?
    public int formatD(WebElement element) {
        try {
            return Integer.parseInt(element.getText().replaceAll("\\D", ""));
        } catch (NumberFormatException ex) {
            ex.getStackTrace();
            System.out.println(ex.getMessage());
            System.out.println("element.getText() = " + element.getText());
        }
        return 1;
    }

    /**
     * Объект для имитации реального поведения мыши или клавиатуры
     */
    protected Actions action = new Actions(DRIVER_MANAGER.getDriver());


    /**
     * Объект для выполнения любого js кода
     */
    protected JavascriptExecutor js = (JavascriptExecutor) DRIVER_MANAGER.getDriver();

//    /**
//     * Объект явного ожидания
//     * При применении будет ожидать заданного состояния 10 секунд с интервалом в 1 секунду
//     */
//    protected WebDriverWait wait = new WebDriverWait(DRIVER_MANAGER.getDriver(), 10, 1000);
    //переместила в DriverManager !!!

    public BasePage() {
        PageFactory.initElements(DRIVER_MANAGER.getDriver(), this);
    }

    /**
     * Функция позволяющая производить scroll до любого элемента с помощью js
     *
     * @param element - веб-элемент странички
     */
    protected WebElement scrollToElementJs(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        return element;
    }

    /**
     * Функция позволяющая производить scroll до любого элемента с помощью js со смещением
     * Смещение задается количеством пикселей по вертикали и горизонтали, т.е. смещение до точки (x, y)
     *
     * @param element - веб-элемент странички
     * @param x       - параметр координаты по горизонтали
     * @param y       - параметр координаты по вертикали
     */
    public WebElement scrollWithOffset(WebElement element, int x, int y) {
        String code = "window.scroll(" + (element.getLocation().x + x) + ","
                + (element.getLocation().y + y) + ");";
        ((JavascriptExecutor) DRIVER_MANAGER.getDriver()).executeScript(code, element, x, y);
        return element;
    }

    /**
     * Явное ожидание состояния clickable элемента
     *
     * @param element - веб-элемент который требует проверки clickable
     * @return WebElement - возвращаем тот же веб элемент, что был передан в метод
     */
    protected WebElement waitUntilElementToBeClickable(WebElement element) {
        return DriverManager.getINSTANCE().getWebDriverWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Явное ожидание того что элемент станет видимым
     *
     * @param element - веб элемент который мы ожидаем, что будет виден на странице
     */
    protected WebElement waitUtilElementToBeVisible(WebElement element) {
        return DRIVER_MANAGER.getWebDriverWait().until(ExpectedConditions.visibilityOf(element));
    }

}
