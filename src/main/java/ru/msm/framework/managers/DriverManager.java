package ru.msm.framework.managers;

import org.apache.commons.exec.OS;
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static ru.msm.framework.utils.PropertiesConstants.*;

public class DriverManager {

    private static DriverManager DRIVER_MANAGER = null;

    private WebDriver driver;

    private final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getINSTANCE();

    /**
     * Объект явного ожидания
     * При применении будет ожидать заданного состояния 10 секунд с интервалом в 1 секунду
     */
    private WebDriverWait wait;

    public static DriverManager getINSTANCE() {
        if (DRIVER_MANAGER == null) {
            DRIVER_MANAGER = new DriverManager();
        }
        return DRIVER_MANAGER;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            initDriver();
        }
        return driver;
    }

    public WebDriverWait getWebDriverWait() {
        if (wait == null) {
            wait = new WebDriverWait(DRIVER_MANAGER.getDriver(),
                    Long.parseLong(PROPERTIES_MANAGER.getProperty(IMPLICITLY_WAIT)), 1000);
        }
        return wait;
    }

    private void initDriver() {
        if (OS.isFamilyWindows()) {
            initDriverWindowsOsFamily();
        } else if (OS.isFamilyMac()) {
            initDriverMacOsFamily();
        } else if (OS.isFamilyUnix()) {
            initDriverUnixOsFamily();
        }
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private void initDriverWindowsOsFamily() {
        initDriverAnyOsFamily(PATH_GECKO_DRIVER_WINDOWS, PATH_CHROME_DRIVER_WINDOWS);
    }

    private void initDriverMacOsFamily() {
        initDriverAnyOsFamily(PATH_GECKO_DRIVER_MAC, PATH_CHROME_DRIVER_MAC);
    }

    private void initDriverUnixOsFamily() {
        initDriverAnyOsFamily(PATH_GECKO_DRIVER_UNIX, PATH_CHROME_DRIVER_UNIX);
    }

    private void initDriverAnyOsFamily(String gecko, String chrome) {
        switch (PROPERTIES_MANAGER.getProperty(TYPE_BROWSER)) {
            case "firefox":
                System.setProperty("webdriver.gecko.driver", PROPERTIES_MANAGER.getProperty(gecko));
                driver = new FirefoxDriver();
                break;
            case "chrome":
                System.setProperty("webdriver.chrome.driver", PROPERTIES_MANAGER.getProperty(chrome));
                driver = new ChromeDriver();
                break;
            default:
                Assertions.fail("Типа браузера '" + PROPERTIES_MANAGER.getProperty(TYPE_BROWSER) + "' не существует во фреймворке");
        }
    }
}
