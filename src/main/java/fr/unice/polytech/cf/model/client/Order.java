package fr.unice.polytech.cf.model.client;


import fr.unice.polytech.cf.exception.EmptyBasketException;
import fr.unice.polytech.cf.model.enumeration.StateOrder;
import fr.unice.polytech.cf.model.recipe.Recipe;
import fr.unice.polytech.cf.model.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Order {

    private UUID id;

    private LocalDateTime pickUpHour;

    private Shop shop;

    private double price;

    private long totalPreparationMinutes;

    private UUID cookId;

    private UUID clientId;

    private StateOrder state =StateOrder.IN_PROGRESS;

    private Set<Item> basket = new HashSet<>();

    private LocalDateTime cancelingTime = null;

    private LocalDateTime readyTime = null;

    private boolean isLoginClient =false;

    public Order(UUID clientId, Shop shop, Set<Item> basket) {
        this.id = UUID.randomUUID();
        this.clientId =clientId;
        this.shop=shop;
        this.basket=basket;
        this.state =StateOrder.IN_PROGRESS;
    }

    public Order() {
        this.id = UUID.randomUUID();
    }

    public double calculatePrice(){
        double price = 0;
        double tax = shop.getTax();
        for (Item item : basket) {
            price += item.getRecipe().getPrice() *item.getQuantity();
        }
        price = price * (1.0 + tax);
        return price;
    }

    public LocalDateTime getPreparationHour() throws EmptyBasketException {
        this.totalPreparationMinutes = getTotalPreparationMinutes();
        long roundMinutes = totalPreparationMinutes;
        while (roundMinutes % 15 != 0)
            roundMinutes++;
        return pickUpHour.minusMinutes(roundMinutes);
    }


    public long getTotalPreparationMinutes() throws EmptyBasketException {
        int total = 0;
        for (Item item : getBasket()) {
            Recipe recipe =item.getRecipe();
            total += (recipe.getPreparingTime()) * item.getQuantity();
        }
        total += getRecipeWithMaxCookingTime().getCookingTime();
        setTotalPreparationMinutes(total);
        return total;
    }


    public Recipe getRecipeWithMaxCookingTime() throws EmptyBasketException {
        if (!getBasket().isEmpty()) {
            Recipe max = new Recipe();
            max.setCookingTime(0);
            for (Item item : getBasket()) {
                Recipe recipe = item.getRecipe();
                if (recipe.getCookingTime() > max.getCookingTime()) {
                    max = recipe;
                }
            }
            return max;
        }
        throw new EmptyBasketException("Le panier est vide");
    }

    public int getNumberCookies(){
        int res = 0;
        for(Item item : this.basket){
            res += item.getQuantity();
        }
        return res;
    }
}
