package ru.msm.framework.pages;

public class Product {

    private final int price;
    //    private String shortDescription;
//    private List<Product> accessories;
//    private List<Product> analogs;
//    private List<Warranty> warranty;
    private Warranty currentWarranty;
    private final Warranty defaultWarranty;

    public String getName() {
        return name;
    }

    private final String name;

    private int num = 1;

    public int getNum() {
        return num;
    }

    //возвращать старое значение?
    public void changeNum(int newNum){
        num = newNum;
    }

    public int getPrice() {
        return price;
    }

    public boolean isWarrantyExist() {
        return currentWarranty != null;
    }

    //сделать недоступным, при null гарантии?
    public int getWarrantyPrice() {
        if (currentWarranty == null) {
            return 0;
        }
        return currentWarranty.price;
    }
    //будем считать, что первый (0) type дефолтный => к нему прибавляются месяцы остальных, но как это адекватно отметить?
//    Product(String name, int price, List<String> type, List<Integer> addMonths, List<Integer> addPrice, int index) {
//        this.name = name;
//        this.price = price;
//        Assertions.assertTrue((type.size() == addMonths.size()) && (addMonths.size() == addPrice.size()),
//                "Длины листов элементов гарантии не совпадают");
//        for (int i = 0; i < type.size(); i++) {
//            warranty.add(new Warranty(type.get(i), addMonths.get(i), addPrice.get(i)));
//        }
//        currentWarranty = warranty.get(index);
//    }

    //первый type дефолтный => к нему прибавляются месяцы остальных - как это адекватно отметить?
    Product(String name, int price, String type, Integer months, Integer addPrice) {
        this.name = name;
        this.price = price;
        this.defaultWarranty = new Warranty(type, months, addPrice);
        this.currentWarranty = new Warranty(type, months, addPrice); //скопировать?
    }

    Product(String name, int price) {
        this.name = name;
        this.price = price;
        defaultWarranty = null; //сделать какую-то выборку есть\нет?
        currentWarranty = null;
    }

    public Product changeWarranty(String type, Integer months, Integer addPrice) {
        if (currentWarranty == null) {
            return null;
        }
        //вынести изменения в класс Warranty
        if (currentWarranty.type.equals(type)) {
            //надо какое-то сообщение выкидывать? ничего не поменяется, но это и не ошибка
            return this;
        }
        currentWarranty.type = type;
        if (currentWarranty.type.equals(defaultWarranty.type)) {
            currentWarranty.months = currentWarranty.months + months;
            currentWarranty.price = currentWarranty.price + addPrice;
        } else {
            currentWarranty.months = defaultWarranty.months + months;
            currentWarranty.price = defaultWarranty.price + addPrice;
        }
        return this;
    }

    private class Warranty {
        private String type;
        private int months;
        private int price;

        Warranty(String type, int months, int addPrice) {
            this.type = type;
            this.months = months;
            this.price = addPrice;
        }
    }

//    enum TypeOfWarranty {
//        COMMERCIAL_WARRANTY,
//        ADDITIONAL_WARRANTY_12,//имена ADDITIONAL_WARRANTY_* не очень
//        ADDITIONAL_WARRANTY_24;
//    }

}
