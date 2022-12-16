package models.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

// Класс POJO для заказа
@AllArgsConstructor
@Getter
@Setter
public class Order {

    private ArrayList<String> ingredients;
}
