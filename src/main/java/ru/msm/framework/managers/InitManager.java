package ru.msm.framework.managers;

import java.util.concurrent.TimeUnit;

import static ru.msm.framework.utils.PropertiesConstants.IMPLICITLY_WAIT;
import static ru.msm.framework.utils.PropertiesConstants.PAGE_LOAD_TIMEOUT;

public class InitManager {

    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getINSTANCE();

    private static final DriverManager DRIVER_MANAGER = DriverManager.getINSTANCE();

    /**
     * Инициализация framework и запуск браузера со страницей приложения
     */
    public static void initFramework() {
        DRIVER_MANAGER.getDriver().manage().window().maximize();
        DRIVER_MANAGER.getDriver().manage().timeouts()
                .implicitlyWait(Integer.parseInt(PROPERTIES_MANAGER.getProperty(IMPLICITLY_WAIT)), TimeUnit.SECONDS);
        DRIVER_MANAGER.getDriver().manage().timeouts()
                .pageLoadTimeout(Integer.parseInt(PROPERTIES_MANAGER.getProperty(PAGE_LOAD_TIMEOUT)), TimeUnit.SECONDS);
    }

    public static void quitFramework() {
        DRIVER_MANAGER.quitDriver();
    }

}
