package ru.msm.framework;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.msm.framework.managers.DriverManager;
import ru.msm.framework.managers.InitManager;
import ru.msm.framework.managers.PageManager;
import ru.msm.framework.managers.PropertiesManager;

import static ru.msm.framework.utils.PropertiesConstants.BASE_URL;

public class BaseTestClass {

    /**
     * Менеджер страничек
     */
    protected PageManager PAGE_MANAGER = PageManager.getINSTANCE();

    /**
     * Менеджер WebDriver
     */
    private final DriverManager DRIVER_MANAGER = DriverManager.getINSTANCE();

    @BeforeAll
    public static void beforeAll() {
        InitManager.initFramework();
    }

    @BeforeEach
    public void beforeEach() {
        DRIVER_MANAGER.getDriver().get(PropertiesManager.getINSTANCE().getProperty(BASE_URL));
    }

    @AfterAll
    public static void afterAll() {
        InitManager.quitFramework();
    }
}
