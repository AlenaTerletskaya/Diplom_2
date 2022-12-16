package models.order.ingredients;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// Класс POJO для ответа на запрос списка ингредиентов
@NoArgsConstructor
@Getter
@Setter
public class Ingredients {

    private boolean success;
    private List<Ingredient> data;
}
