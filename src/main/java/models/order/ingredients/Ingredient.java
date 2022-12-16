package models.order.ingredients;

import lombok.*;

// Класс POJO для ингредиента
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Ingredient {

    private String _id;
    private String name;
    private String type;
    private int proteins;
    private int fat;
    private int carbohydrates;
    private int calories;
    private float price;
    private String image;
    private String image_mobile;
    private String image_large;
    private int __v;
}
