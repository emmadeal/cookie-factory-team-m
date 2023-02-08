package fr.unice.polytech.cf.model.client;

import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SurpriseBasket {

    private UUID id;

    private Set<Item> recipes = new HashSet<>();

    private double price;

    private Shop shop;

    private String description;

    private boolean reserve = false;

    private UUID tooGoodToGoId=null;

    private StateOrder state;

    public SurpriseBasket(Set<Item> recipes, double price, Shop shop, String description) {
        this.id = UUID.randomUUID();
        this.recipes = recipes;
        this.price = price;
        this.shop = shop;
        this.description = description;
    }

    public SurpriseBasket() {
        this.id = UUID.randomUUID();
    }
}
