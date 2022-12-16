package data;

import clients.OrderClient;
import models.order.Order;
import models.order.ingredients.Ingredient;
import models.order.ingredients.Ingredients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Класс для генерации данных заказа
public class OrderGenerator {


//    // Метод возвращает список id всех ингредиентов.
//    public static ArrayList<String> getIngredientIdList() {
//        OrderClient orderClient = new OrderClient();
//        ValidatableResponse response = orderClient.getIngredients();
//
////        Ingredients ingredients = response.extract().body().as(Ingredients.class);
////        List<String> ingrIds = new ArrayList<>();
////        for (Ingredient i : ingredients.getData()) {
////            ingrIds.add(i.get_id());
////            System.out.println(i.get_id());
////        }
//
//        return response.extract().path("data._id");
//    }

//    // Метод возвращает множество уникальных типов ингредиентов.
//    public static HashSet<String> getIngredientTypes() {
//        OrderClient orderClient = new OrderClient();
//        ValidatableResponse response = orderClient.getIngredients();
//
//        Ingredients ingredients = response.extract().body().as(Ingredients.class);
//        HashSet<String> types = new HashSet<>();
//        for (Ingredient i : ingredients.getData()) {
//            types.add(i.getType());
//        }
//        return types;
//    }

    // Метод возвращает заказ с ингредиентами: по 1 ингредиенту bun, main и sauce.
    public static Order getNewOrder(OrderClient orderClient) {
        Ingredients ingredients = orderClient.getIngredients();
        ArrayList<String> ingredientsForOrder = getIngredientsForOrder(ingredients);
        return new Order(ingredientsForOrder);
    }

    // Метод возвращает список ингредиентов для заказа: по 1 ингредиенту bun, main и sauce.
    public static ArrayList<String> getIngredientsForOrder(Ingredients ingredients) {
        Random random = new Random();

        ArrayList<String> buns = getBuns(ingredients);
        int bunIndex = random.nextInt(buns.size() -1);
        String bun = buns.get(bunIndex);

        ArrayList<String> mains = getMains(ingredients);
        int mainIndex = random.nextInt(mains.size() -1);
        String main = mains.get(mainIndex);

        ArrayList<String> sauces = getSauces(ingredients);
        int sauceIndex = random.nextInt(sauces.size() -1);
        String sauce = sauces.get(sauceIndex);

        ArrayList<String> ingredientsForOrder = new ArrayList<>();
        ingredientsForOrder.add(bun);
        ingredientsForOrder.add(main);
        ingredientsForOrder.add(sauce);

        return ingredientsForOrder;
    }

    // Метод возвращает заказ с невалидным ингредиентом.
    public static Order getOrderWithInvalidIngr(OrderClient orderClient) {

        Ingredients ingredients = orderClient.getIngredients();
        String invalidIngr = "a" + ingredients.getData().get(0).get_id().substring(1);

//        Faker faker = new Faker();
//        String invalidIngr = String.valueOf(faker.hashCode());

        ArrayList<String> ingrList = new ArrayList<>();
        ingrList.add(invalidIngr);

        return new Order(ingrList);
    }

    // Метод возвращает заказ без ингредиентов.
    public static Order getOrderWithoutIngredients() {

        ArrayList<String> ingrList = new ArrayList<>();

        return new Order(ingrList);
    }

    // Метод возвращает список id ингредиентов с типом bun (булка)
    public static ArrayList<String> getBuns(Ingredients ingredients) {
        ArrayList<String> buns = new ArrayList<>();
        for (Ingredient i : ingredients.getData()) {
            if (i.getType().equals("bun")) {
                buns.add(i.get_id());
            }
        }
        return buns;
    }

    // Метод возвращает список id ингредиентов с типом main
    public static ArrayList<String> getMains(Ingredients ingredients) {
        ArrayList<String> mains = new ArrayList<>();
        for (Ingredient i : ingredients.getData()) {
            if (i.getType().equals("main")) {
                mains.add(i.get_id());
            }
        }
        return mains;
    }

    // Метод возвращает список id ингредиентов с типом sauce
    public static ArrayList<String> getSauces(Ingredients ingredients) {
        ArrayList<String> sauces = new ArrayList<>();
        for (Ingredient i : ingredients.getData()) {
            if (i.getType().equals("sauce")) {
                sauces.add(i.get_id());
            }
        }
        return sauces;
    }

    // Метод возвращает список из 2х заказов с ингредиентами.
    public static List<Order> getOrderList(OrderClient orderClient) {
        Ingredients ingredients = orderClient.getIngredients();

        ArrayList<String> ingredientsForOrder1 = getIngredientsForOrder(ingredients);
        Order order1 = new Order(ingredientsForOrder1);

        ArrayList<String> ingredientsForOrder2 = getIngredientsForOrder(ingredients);
        Order order2 = new Order(ingredientsForOrder2);

        return Arrays.asList(order1, order2);
    }

}
