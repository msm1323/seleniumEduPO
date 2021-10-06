package ru.msm.framework.managers;

import ru.msm.framework.pages.*;

public class PageManager {

    private static PageManager PAGE_MANAGER = null;

    private PageManager() {
    }

    public static PageManager getINSTANCE() {
        if (PAGE_MANAGER == null) {
            PAGE_MANAGER = new PageManager();
        }
        return PAGE_MANAGER;
    }

    private StartPage startPage;
    private ResultPage resultPage;
    private OrderPage orderPage;
//    private ProductPage productPage;

    public StartPage getStartPage() {
        if (startPage == null) {
            startPage = new StartPage();
        }
        return startPage;
    }

    public ProductPage getProductPage(){
        return new ProductPage();
    }

//    public ProductPage getProductPage() {
//        if (productPage == null) {
//            productPage = new ProductPage();
//        }
//        return productPage;
//    }

    public ResultPage getResultPage() {
        if (resultPage == null) {
            resultPage = new ResultPage();
        }
        return resultPage;
    }

    public OrderPage getOrderPage() {
        if (orderPage == null) {
            orderPage = new OrderPage();
        }
        return orderPage;
    }

}
