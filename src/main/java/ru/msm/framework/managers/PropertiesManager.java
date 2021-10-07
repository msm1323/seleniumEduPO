package ru.msm.framework.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {

    private final Properties properties = new Properties();

    private static PropertiesManager PROPERTIES_MANAGER = null;

    private PropertiesManager() {
        loadApplicationProperties();
        loadCustomProperties();
    }

    public static PropertiesManager getINSTANCE() {
        if (PROPERTIES_MANAGER == null) {
            PROPERTIES_MANAGER = new PropertiesManager();
        }
        return PROPERTIES_MANAGER;
    }

    /**
     * Метод подгружает содержимого файла application.properties в переменную {@link #properties}
     * Либо из файла, переданного пользователем через настройку -DpropFile={nameFile}
     */
    private void loadApplicationProperties() {
        try {
            properties.load(new FileInputStream(
                    "src/main/resources/" +
                            System.getProperty("propFile", "application") + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод заменяет значение содержащиеся в ключах переменной {@link #properties}
     * Заменяет на те значения, что передал пользователь через maven '-D{name.key}={value.key}'
     * Замена будет происходить только в том случае если пользователь передаст совпадающий key из application.properties
     */
    private void loadCustomProperties() {
        properties.forEach((key, value) -> System.getProperties()
                .forEach((customUserKey, customUserValue) -> {
                    if (key.toString().equals(customUserKey.toString()) &&
                            !value.toString().equals(customUserValue.toString())) {
                        properties.setProperty(key.toString(), customUserValue.toString());
                    }
                }));
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
