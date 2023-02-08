package fr.unice.polytech.cf.model.shop;

import fr.unice.polytech.cf.model.client.SurpriseBasket;
import fr.unice.polytech.cf.model.client.User;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Shop {

    private UUID id;

    private String city;

    private double tax;

    private LocalTime openingHour =LocalTime.of(8, 0, 0, 0);

    private LocalTime closingHour = LocalTime.of(18, 0, 0, 0);

    private HashMap<Ingredient, Integer> stock = new HashMap<>();

    private List<UUID> occasionIds = new ArrayList<>() ;

    private List<User> userObservers = new ArrayList<>();

    public Shop(String city) {
        this.id = UUID.randomUUID();
        this.city = city;
    }
}
