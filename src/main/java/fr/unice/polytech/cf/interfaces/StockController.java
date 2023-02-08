package fr.unice.polytech.cf.interfaces;

import fr.unice.polytech.cf.exception.ResourceNotFoundException;
import fr.unice.polytech.cf.model.client.Item;
import fr.unice.polytech.cf.model.recipe.Ingredient;
import fr.unice.polytech.cf.model.shop.Shop;

import java.util.UUID;

public interface StockController {

    boolean verifyStock(Shop shop, Item item);

    void updateStock(Shop shop, Item item) throws ResourceNotFoundException;

    void addToStock(UUID shopId, Ingredient ingredient) throws ResourceNotFoundException;

    void removeFromStock(UUID shopId,Ingredient ingredient) throws ResourceNotFoundException;


}

